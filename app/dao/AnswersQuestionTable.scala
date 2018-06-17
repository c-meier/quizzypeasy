package dao

import javax.inject.{Inject, Singleton}
import models.AnswersQuestion
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait AnswersQuestionComponent extends QuestionsComponent with PossibleAnswerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class AnswersQuestionTable(tag: Tag) extends Table[AnswersQuestion](tag, "answers_question") {
    def id = column[Long]("id_answers_question", O.PrimaryKey,O.AutoInc)
    def answerId = column[Long]("id_answers")
    def questionId = column[Long]("id_question")
    def correctAnswer = column[Boolean]("correct_answer")

    def * = (id.?, answerId, questionId, correctAnswer) <> (AnswersQuestion.tupled, AnswersQuestion.unapply)
  }
}



@Singleton
class AnswersQuestionDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with AnswersQuestionComponent {
  import profile.api._

  val answersQuestion = TableQuery[AnswersQuestionTable]
}
