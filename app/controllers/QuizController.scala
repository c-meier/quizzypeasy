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
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's login page.
 */
@Singleton
class QuizController @Inject()(cc: ControllerComponents, authenticatedAction: AuthenticatedAction, usersDAO: UsersDAO, quizzesDAO: QuizzesDAO, questionsDAO: QuestionsDAO, answersDAO: AnswersDAO) extends AbstractController(cc) with I18nSupport {
  val quizAnswerForm = Form(
    mapping(
      "id" -> longNumber,
      "answer" -> text,
    )(QuizAnswerData.apply)(QuizAnswerData.unapply)
  )

  def quizQuestion(id: Long, q: Long) = authenticatedAction.andThen(authenticatedAction.PermissionCheckAction).async { implicit request =>
    for {
      curQuestionOpt@Some((cat, quiz, quest, ans)) <- answersDAO.getQuestionAndAnswer(id, q) if curQuestionOpt.isDefined
      possibleAnswers <- questionsDAO.getPossibleAnswers(quest.id.get)
      allAnswers <- answersDAO.getQuizAnswers(id)
    } yield {
      val answerForm = quizAnswerForm.fill(QuizAnswerData(ans.id.get, ans.userAnswer))
      Ok(views.html.quiz(answerForm, FullQuizzQuestion(cat, quiz, quest, ans, possibleAnswers.map(t => (t._1, t._2.correctAnswer))), allAnswers))
    }
  }

  def skipToQuizQuestion(id: Long, q: Long) = authenticatedAction.andThen(authenticatedAction.PermissionCheckAction).async { implicit request =>
    val redirect = Redirect(routes.QuizController.quizQuestion(id, q))
    quizAnswerForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          redirect.flashing("info" -> "The answer to the last question has not been saved because there was an error")
        }
      },
      aData => {
        answersDAO.getQuizAnswer(id, aData.id).flatMap{
          case Some(a) =>
            if(a.isFinal){
              Future {
                redirect.flashing("info" -> "The answer to the last question can't be modified")
              }
            }
            else{
              answersDAO.update(Answer(a.id, aData.answer, false, a.questionId, a.quizId)).map{
                case 0 => redirect.flashing("info" -> "Could not update last question")
                case _ => redirect
              }
            }
          case None => Future{
            redirect.flashing("info" -> "The answer to the last question does not match the quizz")
          }
        }
      }
    )
  }

  def submitQuizQuestion(id: Long) = authenticatedAction.andThen(authenticatedAction.PermissionCheckAction).async { implicit request =>
    Future.successful(NotImplemented("Not yet submit " + id))
  }

  /*
  def quizReview(id: Long) = Action { implicit request =>
    Ok(views.html.review())
  }

  def quizAnswer(id: Long, q: Long) = Action.async { implicit request =>
    quizAnswerForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(views.html.signup(formWithErrors))
        }
      },
      uData => {
        val optU = usersDAO.insert(models.User(None, uData.username, uData.password, LocalDateTime.now(), false))
        optU map {
          case u => Redirect(routes.HomeController.index()).withSession("connected" -> u.name)
          case _ => BadRequest(views.html.signup(signUpForm.fill(uData)))
        }
      }
    )
  }*/

  def create(categoryId: Long) = authenticatedAction.andThen(authenticatedAction.PermissionCheckAction).async { implicit request =>
    val answerFor = for {
      quiz <- quizzesDAO.insert(Quiz(None, 0, categoryId, request.userInfo.get._1))
      questions <- questionsDAO.getQuestions(categoryId)
      a <- answersDAO.insertAll(for (q <- questions) yield Answer(None, "", false, q.id.get, quiz.id.get))
    } yield a

    answerFor map {
      as => Redirect(routes.QuizController.quizQuestion(as.head.quizId, as.head.questionId))
    }
  }
}
