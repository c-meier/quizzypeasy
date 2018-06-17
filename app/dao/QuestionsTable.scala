package dao

import javax.inject.{Inject, Singleton}
import models.QuestionType.QuestionType
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models._

trait QuestionsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit lazy val questionTypeMapper = MappedColumnType.base[QuestionType, Int](
    e => e.id,
    i => QuestionType.apply(i)
  )

  class QuestionsTable(tag: Tag) extends Table[Question](tag, "questions") {
    def id = column[Long]("id_question", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("name")
    def content = column[String]("content")
    def questionType = column[QuestionType]("question_type")

    def * = (id.?, name, content, questionType) <> (Question.tupled, Question.unapply)
  }
}

@Singleton
class QuestionsDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with QuestionsComponent with CategoriesComponent with QuestionCategoriesComponent
    with AnswersQuestionComponent with PossibleAnswerComponent with AnswersComponent {
  import profile.api._

  val questions = TableQuery[QuestionsTable]
  val categories = TableQuery[CategoriesTable]
  val questionCategory = TableQuery[QuestionCategoriesTable]
  val answersQuestion = TableQuery[AnswersQuestionTable]
  val possibleAnswers = TableQuery[PossibleAnswersTable]
  val answers = TableQuery[AnswersTable]

  def getQuestions(category: Long): Future[Seq[Question]] = {
    val randRow = SimpleFunction.nullary[Double]("rand")
    val query = for {
      questioncategory <- questionCategory if questioncategory.categoryId === category
      question <- questions if question.id === questioncategory.id
    } yield question
    db.run(query.sortBy(x=>randRow).take(10).result)
  }

  def getPossibleAnswers(question: Long): Future[Seq[(PossibleAnswer, AnswersQuestion)]] = {
    val query = for {
      aq <- answersQuestion if aq.questionId === question
      pa <- possibleAnswers if pa.id === aq.answerId
    } yield (pa, aq)
    db.run(query.result)
  }

  def getQuizPossibleAnswers(quiz: Long): Future[Seq[(PossibleAnswer, AnswersQuestion)]] = {
    val query = for {
      ans <- answers if ans.quizId === quiz
      aq <- answersQuestion if aq.questionId === ans.questionId
      pa <- possibleAnswers if pa.id === aq.answerId
    } yield (pa, aq)
    db.run(query.result)
  }
}
