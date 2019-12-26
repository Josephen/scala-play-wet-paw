package repositories;

import java.sql.Timestamp
import java.util.Date

import com.google.inject.Inject
import models.Article
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.mvc.BodyParser.Json
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.MySQLProfile.api._

class ArticleTable (tag: Tag) extends Table[Article] (tag, "articles") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def address = column[String]("address")
  def author = column[String]("author")
  def sex = column[Boolean]("sex")
  def description = column[String]("description")
  def contacts = column[String]("contacts")
  def image = column[String]("image")
  def category = column[String]("category")
  def approved = column[Boolean]("approved", O.Default(false))
  def created_at =
    column[Timestamp]("created_at",
      O.Default(new Timestamp(new Date().getTime)))
  override def * =
    (id, address, author, sex, description, contacts, image, category, approved, created_at) <> ((Article.apply _).tupled, Article.unapply)
}

class ArticleRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider
                               )(
                                 implicit executionContext: ExecutionContext
                               ) extends HasDatabaseConfigProvider[JdbcProfile] {
  val articles = TableQuery[ArticleTable]

  def listAll: Future[Seq[Article]] ={
    db.run(articles.result)
  }

  def getArticle (id: Long): Future[Article] =
    db.run(articles.filter(_.id === id).result.head)

  def create (address: String, author: String, sex: Boolean, description: String, contacts: String, image: String, category: String): Future[Option[String]] = {
    db.run(
      articles += Article(0, address, author, sex, description, contacts, image, category))
      .map (_ => Some("Post created"))
  }

  def update (id: Long, address: String, author: String, sex: Boolean, description: String, contacts: String, image: String, approved: Boolean, category: String): Future[Option[String]] = {
    db.run(
      articles.filter(_.id === id)
      .map (p => (p.address, p.author, p.sex, p.description, p.contacts, p.image, p.approved, p.category))
      .update(address, author, sex, description, contacts, image, approved, category)
    )
      .map (_ => Some("Post Updated"))
  }

  def articleAuthor (id: Long): Future[Option[String]] =
    db.run(articles.filter(_.id === id).result.headOption).flatMap
    {
      case None => Future(None)
      case _ => db.run(articles.filter(_.id === id).result.head)
        .map(_=> Some("User successfully registered"))
    }

  def approveArticle (id: Long): Future[Option[String]] =
    db.run(
      articles.filter(_.id === id)
        .map ( p => p.approved)
        .update(true)
    )
    .map (_ => Some("Post approved"))

  def findByCategory(category: String): Future[Seq[Article]] =
    db.run(articles.filter(_.category === category).result)

  def findByApproved(approved: Boolean): Future[Seq[Article]] =
    db.run(articles.filter(_.approved === approved).result)

  def findByApprovedAndCategory(category: String): Future[Seq[Article]] =
    db.run(articles.filter(_.category === category)
    .filter(_.approved === true).result)

  def findByUsername (username: String): Future[Seq[Article]] =
    db.run(articles.filter(_.author === username).result)

  def delete (id: Long): Future[Option[String]] =
    db.run(articles.filter(_.id === id).result.headOption).flatMap
  {
    case None => Future(None)
    case _ => db.run(articles.filter(_.id === id).delete)
      .map(_=> Some("Article deleted"))
  }
}