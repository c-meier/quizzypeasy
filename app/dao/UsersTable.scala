package dao


import java.sql.Timestamp
import java.time.LocalDateTime

import javax.inject.{Inject, Singleton}
import models.{Quiz, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait UsersComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  /**
    * Value to map java LocalDateTime to database Timestamp value
    */
  implicit lazy val localDateToDate = MappedColumnType.base[LocalDateTime, Timestamp](
    l => Timestamp.valueOf(l),
    d => d.toLocalDateTime
  )

  class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id_user", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("user_name")
    def password = column[String]("password")
    def dateInscription = column[LocalDateTime]("date_inscription")
    def isAdmin = column[Boolean]("is_admin")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, password, dateInscription, isAdmin) <> (User.tupled, User.unapply)
  }

}

@Singleton
class UsersDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with UsersComponent with QuizzesComponent {
  import profile.api._

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