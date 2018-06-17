package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait AnswersComponent {
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
  extends HasDatabaseConfigProvider[JdbcProfile]
    with AnswersComponent with QuizzesComponent with QuestionsComponent with CategoriesComponent {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val answers = TableQuery[AnswersTable]
  val quizzes = TableQuery[QuizzesTable]
  val questions = TableQuery[QuestionsTable]
  val categories = TableQuery[CategoriesTable]

  def getQuestionsAndAnswers(quizId: Long): Future[Seq[(Category, Quiz, Question, Answer)]] = {
    val questionAnswer = for {
      quiz <- quizzes if quiz.id === quizId
      as <- answers if as.quizId === quizId
      cat <- categories if cat.id === quiz.categoryId
      qs <- questions if qs.id === as.questionId
    } yield (cat, quiz, qs, as)
    db.run(questionAnswer.result)
  }

  def getQuestionAndAnswer(quizId: Long, answerId: Long): Future[Option[(Category, Quiz, Question, Answer)]] = {
    val questionAnswer = for {
      as <- answers if as.id === answerId && as.quizId === quizId
      quiz <- quizzes if quiz.id === quizId
      cat <- categories if cat.id === quiz.categoryId
      qs <- questions if qs.id === as.questionId
    } yield (cat, quiz, qs, as)
    db.run(questionAnswer.result.headOption)
  }

  def getQuizAnswer(quizId: Long, answerId: Long): Future[Option[Answer]] = {
    val answer = for {
      as <- answers if as.id === answerId && as.quizId === quizId
    } yield as
    db.run(answer.result.headOption)
  }

  def update(answer: Answer): Future[Int] = {
    val query = answers.insertOrUpdate(answer)
    db.run(query)
  }

  def lockAll(quizId: Long): Future[Int] = {
    val query = for {
      a <- answers if a.quizId === quizId
    } yield a.isFinal
    db.run(query.update(true))
  }

  def getQuizAnswers(quizId: Long): Future[Seq[Answer]] = {
    val query = answers.filter(_.quizId === quizId)
    db.run(query.result)
  }

  def insertAll(as: Seq[Answer]): Future[Seq[Answer]] = {
    val query = answers returning answers.map(_.id) into ((answer,id)=> answer.copy(Some(id)))
    db.run(query ++= as)
  }
}
