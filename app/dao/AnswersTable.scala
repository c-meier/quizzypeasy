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

  /**
    * Recuperate all the question and associated information used by the given quiz.
    * @param quizId The quiz id
    */
  def getQuestionsAndAnswers(quizId: Long): Future[Seq[(Category, Quiz, Question, Answer)]] = {
    val questionAnswer = for {
      quiz <- quizzes if quiz.id === quizId
      as <- answers if as.quizId === quizId
      cat <- categories if cat.id === quiz.categoryId
      qs <- questions if qs.id === as.questionId
    } yield (cat, quiz, qs, as)
    db.run(questionAnswer.result)
  }

  /**
    * Recuperate the question and its associated information.
    * @param quizId The quiz id
    * @param answerId The id of the answer that links a question to a quiz
    */
  def getQuestionAndAnswer(quizId: Long, answerId: Long): Future[Option[(Category, Quiz, Question, Answer)]] = {
    val questionAnswer = for {
      as <- answers if as.id === answerId && as.quizId === quizId
      quiz <- quizzes if quiz.id === quizId
      cat <- categories if cat.id === quiz.categoryId
      qs <- questions if qs.id === as.questionId
    } yield (cat, quiz, qs, as)
    db.run(questionAnswer.result.headOption)
  }

  /**
    * Recuperate the answer indicated by the given id if it belong to the given quiz.
    * @param quizId The id of the quiz it must match.
    * @param answerId The id of the answer to recuperate.
    */
  def getQuizAnswer(quizId: Long, answerId: Long): Future[Option[Answer]] = {
    val answer = for {
      as <- answers if as.id === answerId && as.quizId === quizId
    } yield as
    db.run(answer.result.headOption)
  }

  /**
    * Update an answer.
    * @param answer The answer to update.
    * @return The number of row that were affected.
    */
  def update(answer: Answer): Future[Int] = {
    val query = answers.insertOrUpdate(answer)
    db.run(query)
  }

  /**
    * Set all the answers that belong to the given quiz as final.
    * @param quizId The id of the quiz.
    * @return The number of row that were affected.
    */
  def lockAll(quizId: Long): Future[Int] = {
    val query = for {
      a <- answers if a.quizId === quizId
    } yield a.isFinal
    db.run(query.update(true))
  }

  /**
    * Recuperate all the answers that belong to the given quiz.
    * @param quizId The id of the quiz.
    */
  def getQuizAnswers(quizId: Long): Future[Seq[Answer]] = {
    val query = answers.filter(_.quizId === quizId)
    db.run(query.result)
  }

  /**
    * Insert all the given answers to the DB.
    * @param as The sequence of answers.
    */
  def insertAll(as: Seq[Answer]): Future[Seq[Answer]] = {
    val query = answers returning answers.map(_.id) into ((answer,id)=> answer.copy(Some(id)))
    db.run(query ++= as)
  }
}
