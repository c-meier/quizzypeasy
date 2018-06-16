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

  // This class convert the database's user table in a object-oriented entity: the User model.
  class CategoriesTable(tag: Tag) extends Table[Category](tag, "categories") {
    def id = column[Long]("id_category", O.PrimaryKey, O.AutoInc) // Primary key, auto-incremented
    def name = column[String]("category_name")
    def description = column[String]("description")
    // Map the attributes with the model; the ID is optional.
    def * = (id.?, name, description) <> (Category.tupled, Category.unapply)
  }

}


// This class contains the object-oriented list of courses and offers methods to query the data.
// A DatabaseConfigProvider is injected through dependency injection; it provides a Slick type bundling a database and
// driver. The class extends the courses' query table and loads the JDBC profile configured in the application's
// configuration file.
@Singleton
class CategoriesDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends CategoriesComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  // Get the object-oriented list of courses directly from the query table.
  val categories = TableQuery[CategoriesTable]

  def findByName(name: String): Future[Option[Category]] = {
    db.run(categories.filter(_.name === name).result.headOption)
  }
}