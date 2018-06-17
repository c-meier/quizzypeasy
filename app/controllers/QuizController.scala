package controllers

import java.time.LocalDateTime

import dao.{AnswersDAO, QuestionsDAO, QuizzesDAO, UsersDAO}
import javax.inject._
import models._
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport
import play.api.libs.typedmap.TypedKey
import play.api.mvc.Results.Forbidden
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.matching.Regex


/**
 * This controller handle HTTP Request for the quizzes
 */
@Singleton
class QuizController @Inject()(cc: ControllerComponents, authenticatedAction: AuthenticatedAction, usersDAO: UsersDAO, quizzesDAO: QuizzesDAO, questionsDAO: QuestionsDAO, answersDAO: AnswersDAO) extends AbstractController(cc) with I18nSupport {
  def UserQuizCheckAction(quizId: Long) = new ActionFilter[AuthenticatedRequest] {
    def executionContext = global

    def filter[A](input: AuthenticatedRequest[A]) = input.userInfo match {
      case Some((id, name, isAdmin)) =>
        for {
          q <- quizzesDAO.listFromUser(id)
        } yield {
          if(q.exists(_._1.id.get == quizId))
            None
          else
            Some(Redirect(routes.QuizController.listQuizzes()).flashing("info" -> "The quiz you tried to acces is not yours."))
        }
      case None =>
        Future.successful {
          Some(Forbidden("You are not authorized"))
        }
    }
  }

  val quizAnswerForm = Form(
    mapping(
      "id" -> longNumber,
      "answer" -> optional(text),
    )(QuizAnswerData.apply)(QuizAnswerData.unapply)
  )

  /**
    * Methods used to get a questions from a quiz
    * @param id The quiz id
    * @param q The question id
    */
  def quizQuestion(id: Long, q: Long) = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction)
    .andThen(UserQuizCheckAction(id)).async { implicit request =>
      for {
        curQuestionOpt <- answersDAO.getQuestionAndAnswer(id, q)
        possibleAnswers <- if (curQuestionOpt.isDefined) questionsDAO.getPossibleAnswers(curQuestionOpt.get._3.id.get) else Future.successful(Seq.empty)
        allAnswers <- answersDAO.getQuizAnswers(id)
      } yield {
        curQuestionOpt match {
          case Some((cat, quiz, quest, ans)) =>
            val answerForm = quizAnswerForm.fill(QuizAnswerData(ans.id.get, if(ans.userAnswer != "") Some(ans.userAnswer) else None))
            Ok(views.html.quiz(answerForm, FullQuizzQuestion(cat, quiz, quest, ans, possibleAnswers.map(t => (t._1, t._2.correctAnswer))), allAnswers))
          case None => BadRequest("There is no question matching the ids")
        }
      }
    }

  /**
    * Save the current question and redirect to the selected question
    * @param id Quiz id
    * @param q The id of the selected question
    */
  def skipToQuizQuestion(id: Long, q: Long) = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction)
    .andThen(UserQuizCheckAction(id)).async { implicit request =>
      val redirect = Redirect(routes.QuizController.quizQuestion(id, q))
      quizAnswerForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful {
            redirect.flashing("info" -> "The answer to the last question has not been saved because there was an error")
          }
        },
        aData => {
          if(aData.answer.isDefined) {
            answersDAO.getQuizAnswer(id, aData.id).flatMap{
              case Some(a) =>
                if(a.isFinal){
                  Future.successful {
                    redirect.flashing("info" -> "The answer to the last question can't be modified")
                  }
                }
                else{
                  answersDAO.update(Answer(a.id, aData.answer.get, false, a.questionId, a.quizId)).map{
                    case 0 => redirect.flashing("info" -> "Could not update last question")
                    case _ => redirect
                  }
                }
              case None => Future.successful {
                redirect.flashing("info" -> "The answer to the last question does not match the quizz")
              }
            }
          } else {
            Future.successful{
              redirect
            }
          }
        }
      )
    }

  /**
    * Lock current question answer, compare user answer to correct answer and redirect to the current page
    * @param id Quiz id
    * @param q Question id
    */
  def submitQuizQuestion(id: Long, q: Long) = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction)
    .andThen(UserQuizCheckAction(id)).async { implicit request =>
      val redirect = Redirect(routes.QuizController.quizQuestion(id, q))
      quizAnswerForm.bindFromRequest.fold(
        formWithErrors => {
          Future.successful {
            redirect.flashing("info" -> "The answer to the question has not been saved because there was an error")
          }
        },
        aData => {
          if(aData.answer.isDefined) {
            answersDAO.getQuizAnswer(id, aData.id).flatMap{
              case Some(a) =>
                if(a.isFinal){
                  Future.successful {
                    redirect.flashing("info" -> "The answer to the last question can't be modified")
                  }
                }
                else{
                  answersDAO.update(Answer(a.id, aData.answer.get, true, a.questionId, a.quizId)).map{
                    case 0 => redirect.flashing("info" -> "Could not update the question")
                    case _ => redirect
                  }
                }
              case None => Future.successful {
                redirect.flashing("info" -> "The answer to the question does not match the quizz")
              }
            }
          } else {
            Future.successful{
              redirect
            }
          }
        }
      )
    }

  /**
    * Get the review of the selected quiz
    * @param id Quiz id
    */
  def quizReview(id: Long) = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction)
    .andThen(UserQuizCheckAction(id)).async { implicit request =>
      for {
        qs <- answersDAO.getQuestionsAndAnswers(id)
        as <- questionsDAO.getQuizPossibleAnswers(id)
      } yield {
        val posAnsMap = as.groupBy(_._2.questionId)
        Ok(views.html.review(qs.map({
          case (cat, quiz, quest, ans) => FullQuizzQuestion(cat, quiz, quest, ans, posAnsMap(quest.id.get).map(t => (t._1, t._2.correctAnswer)))
        })))
      }
    }

  /**
    * Lock all questions of a quiz and calculates the score given by the number of correct answers
    * @param id Quiz id
    */
  def quizScore(id: Long) = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction)
    .andThen(UserQuizCheckAction(id)).async { implicit request =>
      def calculateScore(qs: Seq[(Category, Quiz, Question, Answer)], as: Seq[(PossibleAnswer, AnswersQuestion)]) : Int = {
        val posAnsMap = as.groupBy(_._2.questionId)
        val allQuestions = qs.map({
          case (cat, quiz, quest, ans) => FullQuizzQuestion(cat, quiz, quest, ans, posAnsMap(quest.id.get).map(t => (t._1, t._2.correctAnswer)))
        })
        allQuestions.count(_.isCorrect)
      }
      for {
        qs <- answersDAO.getQuestionsAndAnswers(id)
        as <- questionsDAO.getQuizPossibleAnswers(id)
        qz <- quizzesDAO.update(Quiz(qs.head._2.id, calculateScore(qs, as), qs.head._2.categoryId, qs.head._2.userId))
        qa <- answersDAO.lockAll(id)
      } yield Redirect(routes.QuizController.quizReview(id))
    }

  /**
    * Get the list of quiz of the user
    */
  def listQuizzes() = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction).async { implicit request =>
      for {
        s <- quizzesDAO.listFromUser(request.userInfo.get._1)
      } yield {
        Ok(views.html.userquizzes(s))
      }
    }

  /**
    * Create a quiz given the selected category and reidrect to the first question of the quiz
    * @param categoryId The id of the selected category
    */
  def create(categoryId: Long) = authenticatedAction
    .andThen(authenticatedAction.PermissionCheckAction).async { implicit request =>
      val answerFor = for {
        quiz <- quizzesDAO.insert(Quiz(None, -1, categoryId, request.userInfo.get._1))
        questions <- questionsDAO.getQuestions(categoryId)
        a <- answersDAO.insertAll(for (q <- questions) yield Answer(None, "", false, q.id.get, quiz.id.get))
      } yield a

      answerFor map {
        as => Redirect(routes.QuizController.quizQuestion(as.head.quizId, as.head.id.get))
      }
    }
}
