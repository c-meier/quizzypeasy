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
      "answer" -> text,
      "is_final" -> boolean
    )(QuizAnswerData.apply)(QuizAnswerData.unapply)
  )

  def quizQuestion(id: Long, q: Long) = Action.async { implicit request =>
    request.session.get("connected") match {
      case Some(u) =>
        for {
          u <- usersDAO.findByName(u)
          t <-
            if (u.isDefined) answersDAO.getQuestionsAndAnswers(id)
            else Future.successful(Seq.empty)
        } yield {
          if (u.isEmpty) Unauthorized("You ar not connected")
          else {
            val list: Seq[(Question, Answer)] = t.map(x => (x._2, x._3))
            Ok(views.html.quiz(list))
          }
        }
      case None => Future {
        Unauthorized("You are not connected")
      }
    }

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
