package models


import play.api.data.Form
import play.api.data.Forms._

case class QuizData(
                 id: Option[Long],
                 score: Option[Int],
                 categoryId: Long,
                 userId: Long
               )

object NewQuizForm {
  val form: Form[QuizData] = Form(
    mapping(
      "id" -> optional(longNumber),
      "score" -> optional(number),
      "categoryId" -> longNumber,
      "userId" -> longNumber
    )(QuizData.apply)(QuizData.unapply)
  )
}