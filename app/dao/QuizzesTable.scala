package dao

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models.{Quiz, User}

trait QuizzesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's user table in a object-oriented entity: the User model.
  class QuizzesTable(tag: Tag) extends Table[Quiz](tag, "quizzes") {
    def id = column[Long]("id_quiz", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def score = column[Option[Int]]("score")
    def categoryId = column[Long]("category_id")
    def userId = column[Option[Long]]("user_id")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, score, categoryId, userId) <> (Quiz.tupled, Quiz.unapply)
  }
}


// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class QuizzesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends QuizzesComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val quizzes = TableQuery[QuizzesTable]
}
