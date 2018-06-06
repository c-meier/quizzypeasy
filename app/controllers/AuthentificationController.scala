package controllers

import javax.inject._
import play.api.mvc._
import models.{LoginData, SignUpData}
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n.I18nSupport


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's login page.
 */
@Singleton
class AuthentificationController @Inject()(val cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  val loginForm = Form(
    mapping(
      "user_login" -> text.verifying(pattern("""[a-zA-Z0-9_-]+""".r)) ,
      "user_pass" -> nonEmptyText,
      "remeber_me" -> boolean
    )(LoginData.apply)(LoginData.unapply)
  )

  val signUpForm = Form(
    mapping(
      "user_name" -> text.verifying(pattern("""[a-zA-Z0-9_-]+""".r)) ,
      "user_pass" -> nonEmptyText
    )(SignUpData.apply)(SignUpData.unapply)
  )

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm, signUpForm))
  }

  def signUp = Action { implicit request =>
    signUpForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login(loginForm, formWithErrors))
      },
      signUpInput => {
        Redirect(routes.HomeController.index())
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.HomeController.index())
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.login(formWithErrors, signUpForm))
      },
      loginInput => {
        Redirect(routes.HomeController.index())
      }
    )
  }

}
