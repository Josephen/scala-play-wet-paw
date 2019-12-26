package services

import com.google.inject.Inject
import models.Article
import repositories.ArticleRepository

import scala.concurrent.Future

class ArticleService @Inject() (articles: ArticleRepository) {

  def listAll: Future[Seq[Article]] =
    articles.listAll

  def addArticle (address: String, author: String, sex: Boolean, description: String, contacts: String, image: String, category: String): Future[Option[String]] = {
    articles.create(address, author, sex, description, contacts, image, category)
  }

  def approveArticle (id: Long): Future[Option[String]] =
    articles.approveArticle(id)

  def findApprovedArticles (approve: Boolean): Future[Seq[Article]] =
    articles.findByApproved(approve)

  def findCategoryArticles(category: String): Future[Seq[Article]] =
    articles.findByCategory(category)

  def findApprovedCategory (category: String): Future[Seq[Article]] =
    articles.findByApprovedAndCategory(category)

  def articlesByAuthor (username: String): Future[Seq[Article]] =
    articles.findByUsername(username)

  def deleteArticle(id: Long): Future[Option[String]] = {
    articles.delete(id)
  }

  def updateArticle(id: Long, address: String, author: String, sex: Boolean, description: String, contacts: String, image: String, approved: Boolean, category: String): Future[Option[String]] =
    articles.update(id, address, author, sex, description, contacts, image, approved, category)

  def getArticle(id: Long): Future[Article] = {
    articles.getArticle(id)
  }
}
