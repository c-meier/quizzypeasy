package dao

import javax.inject.{Inject, Singleton}
import models.QuestionType.QuestionType
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models.{Question, QuestionType, Quiz, User}

trait QuestionsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's user table in a object-oriented entity: the User model.
  class QuestionsTable(tag: Tag) extends Table[Question](tag, "questions") {
    def id = column[Long]("id_question", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("name")
    def content = column[String]("content")
    def questionType = column[QuestionType]("question_type")
    def correctAnswerId = column[Long]("correct_answer_id")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, content, questionType, correctAnswerId) <> (Quiz.tupled, Quiz.unapply)
  }
}


// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class QuestionsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuestionsComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val questions = TableQuery[QuestionsTable]
}
