package dao

import javax.inject.{Inject, Singleton}
import models.PossibleAnswer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait PossibleAnswerComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's user table in a object-oriented entity: the User model.
  class PossibleAnswersTable(tag: Tag) extends Table[PossibleAnswer](tag, "possible_answers") {
    def id = column[Long]("id_possible_answer", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def value = column[String]("value")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, value) <> (PossibleAnswer.tupled, PossibleAnswer.unapply)
  }

}


// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class PossibleAnswersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with PossibleAnswerComponent with QuestionsComponent {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val users = TableQuery[PossibleAnswersTable]
  val quizzes = TableQuery[QuestionsTable]
}