package dao

import javax.inject.{Inject, Singleton}
import models._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

trait QuestionCategoriesComponent extends QuizzesComponent with QuestionsComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class QuestionCategoriesTable(tag: Tag) extends Table[QuestionCategory](tag, "question_categories") {
    def id = column[Long]("id_question_category", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def questionId = column[Long]("question_id")
    def categoryId = column[Long]("category_id")

    // Map the attributes with the model; the ID is optional.
    def * = (id.?, questionId, categoryId) <> (QuestionCategory.tupled, QuestionCategory.unapply)
  }
}

@Singleton
class QuestionCategoriesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with QuestionCategoriesComponent {
  import profile.api._

  val questionCategories = TableQuery[QuestionCategoriesTable]
}
