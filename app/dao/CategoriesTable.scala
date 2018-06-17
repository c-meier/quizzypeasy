package dao

import java.util.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import models.{Category}

trait CategoriesComponent {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  class CategoriesTable(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Long]("id_category", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("category_name")
    def description = column[String]("description")
    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, description) <> (Category.tupled, Category.unapply)
  }

}

@Singleton
class CategoriesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with CategoriesComponent {
  import profile.api._

  val categories = TableQuery[CategoriesTable]

  /**
    * Get a list of all category sort by name
    */
  def list(): Future[Seq[Category]] = {
    val query = categories.sortBy(_.name)
    db.run(query.result)
  }

  /**
    * Get a category
    * @param name Name of the category
    */
  def findByName(name: String): Future[Option[Category]] = {
    db.run(categories.filter(_.name === name).result.headOption)
  }
}