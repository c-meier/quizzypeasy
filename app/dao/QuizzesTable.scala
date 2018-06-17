package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait QuizzesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's user table in a object-oriented entity: the User model.
  class QuizzesTable(tag: Tag) extends Table[Quiz](tag, "quizzes") {
    def id = column[Long]("id_quiz", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def score = column[Int]("score")
    def categoryId = column[Long]("category_id")
    def userId = column[Long]("user_id")

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
  extends HasDatabaseConfigProvider[JdbcProfile] with QuizzesComponent with CategoriesComponent {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val quizzes = TableQuery[QuizzesTable]
  val categories = TableQuery[CategoriesTable]


  def list(): Future[Seq[Quiz]] = {
    val query = quizzes.sortBy(_.categoryId)
    db.run(query.result)
  }

  def listFromUser(userId: Long): Future[Seq[(Quiz, Category)]] = {
    val query = for {
      q <- quizzes if q.userId === userId
      c <- categories if c.id === q.categoryId
    } yield (q, c)
    db.run(query.result)
  }

  /**Insert a new quiz**/
  def insert(quiz: Quiz): Future[Quiz] = {
    val query = quizzes returning quizzes.map(_.id) into ((quiz,id)=> quiz.copy(Some(id)))
    db.run(query += quiz)
  }

  def update(quiz: Quiz): Future[Int] = {
    val query = quizzes.insertOrUpdate(quiz)
    db.run(query)
  }
}
