package dao

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models.{Quiz, User}

trait UsersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // This class convert the database's user table in a object-oriented entity: the User model.
  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id_user", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("user_name")
    def hash = column[String]("hash")
    def salt = column[String]("salt")
    def dateInscription = column[Date]("date_inscription")
    def isAdmin = column[Boolean]("is_admin")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, hash, salt, dateInscription, isAdmin) <> (User.tupled, User.unapply)
  }

}


// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class UsersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends UsersComponent with QuizzesComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val users = TableQuery[UsersTable]
  val quizzes = TableQuery[QuizzesTable]

  /** Retrieve a user from the name. */
  def findByName(name: String): Future[Option[User]] =
    db.run(users.filter(_.name === name).result.headOption)

  /** Get the quizzes associated with the given user's ID. */
  def getQuizzesOfUser(id: Long): Future[Seq[Quiz]] = {
    val query = for {
      quiz <- quizzes if quiz.userId === id
    } yield quiz

    db.run(query.result)
  }

  /** Insert a new user, then return it. */
  def insert(user: User): Future[User] = {
    val insertQuery = users returning users.map(_.id) into ((user, id) => user.copy(Some(id)))
    db.run(insertQuery += user)
  }

  /** Update a user, then return an integer that indicates if the user was found (1) or not (0). */
  def update(id: Long, user: User): Future[Int] = {
    val userToUpdate: User = user.copy(Some(id))
    db.run(users.filter(_.id === id).update(userToUpdate))
  }

  /** Delete a user, then return an integer that indicates if the user was found (1) or not (0) */
  def delete(id: Long): Future[Int] =
    db.run(users.filter(_.id === id).delete)
}