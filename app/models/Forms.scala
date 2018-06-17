package models

import play.api.data.Form
import play.api.data.Forms._

case class LoginData(username: String, password: String, rememberMe: Boolean)

case class SignUpData(username: String, password: String)

case class QuizAnswerData(id: Long, answer: Option[String])