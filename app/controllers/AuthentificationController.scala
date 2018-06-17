package controllers

import java.time.LocalDateTime

import dao.UsersDAO
import javax.inject._
import models.{LoginData, SignUpData, User}
import play.api.data.Forms._
import play.api.data._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport
import play.api.mvc._
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's login page.
 */
@Singleton
class AuthentificationController @Inject()(cc: ControllerComponents, usersDAO: UsersDAO) extends AbstractController(cc) with I18nSupport {

  val loginForm = Form(
    mapping(
      "user_login" -> text.verifying(pattern("""[a-zA-Z0-9_-]+""".r)) ,
      "user_pass" -> nonEmptyText,
      "remember_me" -> boolean
    )(LoginData.apply)(LoginData.unapply)
  )

  val signUpForm = Form(
    mapping(
      "user_name" -> text.verifying(pattern("""[a-zA-Z0-9_-]+""".r)) ,
      "user_pass" -> nonEmptyText
    )(SignUpData.apply)(SignUpData.unapply)
  )

  def loginPage = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def signUpPage = Action { implicit request =>
    Ok(views.html.signup(signUpForm))
  }

  def signUp = Action.async { implicit request =>
    signUpForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(views.html.signup(formWithErrors))
        }
      },
      uData => {
        val passHash = BCrypt.hashpw(uData.password, BCrypt.gensalt)
        for {
          optU <- usersDAO.findByName(uData.username)
          u <- if (optU.isEmpty) usersDAO.insert(models.User(None, uData.username, passHash, LocalDateTime.now(), false)) else Future.successful{Nil}
        } yield u match {
          case User(_, name, _, _, _) => Redirect(routes.HomeController.index()).withSession("connected" -> name)
          case Nil => BadRequest(views.html.signup(signUpForm.fill(uData).withGlobalError("Username already in use")))
        }
      }
    )
  }

  def login = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Future {
          BadRequest(views.html.login(formWithErrors))
        }
      },
      uData => {
        val optU = usersDAO.findByName(uData.username)
        optU.map{
          case Some(u) if BCrypt.checkpw(uData.password,u.password) =>
            Redirect(routes.HomeController.index()).withSession("connected" -> u.name)
          case _ =>
            val errorForm = loginForm.fill(uData).withGlobalError("Username or password are not valid !")
            BadRequest(views.html.login(errorForm))
        }
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.HomeController.index()).withNewSession
  }

}
