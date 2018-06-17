package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait QuizzesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class QuizzesTable(tag: Tag) extends Table[Quiz](tag, "quizzes") {
    def id = column[Long]("id_quiz", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def score = column[Int]("score")
    def categoryId = column[Long]("category_id")
    def userId = column[Long]("user_id")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, score, categoryId, userId) <> (Quiz.tupled, Quiz.unapply)
  }
}

@Singleton
class QuizzesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with QuizzesComponent with CategoriesComponent {
  import profile.api._

  val quizzes = TableQuery[QuizzesTable]
  val categories = TableQuery[CategoriesTable]

  /**
    * Get the list of all quiz sort by category
    */
  def list(): Future[Seq[Quiz]] = {
    val query = quizzes.sortBy(_.categoryId)
    db.run(query.result)
  }

  /**
    * Get the list of quiz of a user
    * @param userId the user ID
    */
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

  /**
    * Update a quiz
    */
  def update(quiz: Quiz): Future[Int] = {
    val query = quizzes.insertOrUpdate(quiz)
    db.run(query)
  }
}
