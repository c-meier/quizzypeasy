package models

case class LoginData(username: String, password: String, rememberMe: Boolean)

case class SignUpData(username: String, password: String)