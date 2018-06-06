package controllers

import java.time.LocalDateTime

import dao.UsersDAO
import javax.inject._
import models.{LoginData, QuizAnswerData, SignUpData}
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's login page.
 */
@Singleton
class QuizController @Inject()(cc: ControllerComponents, usersDAO: UsersDAO) extends AbstractController(cc) with I18nSupport {

  val quizAnswerForm = Form(
    mapping(
      "answer" -> text,
      "is_final" -> boolean,
    )(QuizAnswerData.apply)(QuizAnswerData.unapply)
  )

  def quizQuestion(id: Long, q: Long) = Action { implicit request =>
    Ok(views.html.quiz(quizAnswerForm))
  }
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
  }
}
