package dao

import javax.inject.{Inject, Singleton}
import models.QuestionType.QuestionType
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models._

trait AnswersComponent extends QuizzesComponent with QuestionsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's user table in a object-oriented entity: the User model.
  class AnswersTable(tag: Tag) extends Table[Answer](tag, "answers") {
    def id = column[Long]("id_answer", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def userAnswer = column[String]("user_answer")
    def isFinal = column[Boolean]("is_final")
    def questionId = column[Long]("question_id")
    def quizId = column[Long]("quizz_id")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, userAnswer, isFinal, questionId, quizId) <> (Answer.tupled, Answer.unapply)
  }
}


// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class AnswersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends AnswersComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val answers = TableQuery[AnswersTable]
}
