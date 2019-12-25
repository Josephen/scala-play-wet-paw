package controllers

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import services.ArticleService
import utilities.JwtUtility

import scala.concurrent.{ExecutionContext, Future}

class ArticleController @Inject() (
                                    articleService: ArticleService,
                                    cc: MessagesControllerComponents
                                  )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val articleForm: Form[ArticleFormData] = Form {
    mapping(
      "address" -> nonEmptyText,
      "sex" -> boolean,
      "description" -> nonEmptyText,
      "contacts" -> text,
      "image" -> text,
      "category" -> nonEmptyText
    )(ArticleFormData.apply)(ArticleFormData.unapply)
  }

  def listApprovedArticles = Action.async { implicit request =>
    articleService.findApprovedArticles(true).map { articles =>
      Ok(Json.toJson(articles.reverse))
    }
  }

  def getArticle(id: Long) = Action.async { implicit request =>
    articleService.getArticle(id).map { article =>
      Ok(Json.toJson(article))
    }
  }

  def listArticlesByUsername (username: String) = Action.async { implicit request =>
    articleService.articlesByAuthor(username).map { articles =>
      Ok(Json.toJson(articles.reverse))
    }
  }

  def listArticlesByCategoryAndApproved(category: String) = Action.async { implicit request =>
    articleService.findApprovedCategory(category).map{ articles =>
      Ok(Json.toJson(articles.reverse))
    }
  }

  def createArticle = Action.async { implicit request =>
    val token = request.headers.get("Authorization").getOrElse("")
    if (JwtUtility.isValidToken(token)) {
      val payload = JwtUtility.decodePayload(token)
      payload match {
        case Some(credentials) => {
          val username = (Json.parse(credentials) \ "username").as[String]
          articleForm.bindFromRequest.fold(
            errorForm => {
              Future.successful(Ok(s"Form submission with error: ${errorForm}"))
            },
            data => {
              articleService.addArticle(data.address, username, data.sex, data.description, data.contacts, data.image, data.category)
                .map {
                  case Some(msg) => Ok(Json.obj("status" -> "OK", "msg" -> msg))
                  case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "cant create"))
                }
            }
          )
        }
        case _ => Future.successful(Unauthorized(
          Json.obj("status" -> "FAIL", "msg" -> "Credentials Invalid in token")
        ))
      }
    }
    else Future.successful(Unauthorized(
      Json.obj("status" -> "FAIL", "msg" -> "No authentication token found"))
    )
  }

  def deleteArticle(id: Long) = Action.async { implicit request =>
    val token = request.headers.get("Authorization").getOrElse("")
    if (JwtUtility.isValidToken(token)) {
      val payload = JwtUtility.decodePayload(token)
      payload match {
        case Some(cred) => articleService.deleteArticle(id).map {
          case Some(msg) => Ok(Json.obj("status" -> "OK", "msg" -> msg))
          case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "nothing to delete"))
        }
        case _ => Future.successful(BadRequest(Json.obj("status" -> "FAIL", "msg" -> "cant delete")))
      }
    }
    else Future.successful(Unauthorized(
      Json.obj("status" -> "FAIL", "msg" -> "Incorrect token"))
    )
  }

  def updateArticle(id: Long) = Action.async { implicit request =>
    val token = request.headers.get("Authorization").getOrElse("")
    if (JwtUtility.isValidToken(token)) {
      val payload = JwtUtility.decodePayload(token)
      payload match {
        case Some(credentials) => {
          val username = (Json.parse(credentials) \ "username").as[String]
          articleForm.bindFromRequest.fold(
            errorForm => {
              Future.successful(Ok(s"Form submission with error: ${errorForm}"))
            },
            data => {
              articleService.updateArticle(id, data.address, username, data.sex, data.description, data.contacts, data.image, data.category)
                .map {
                  case Some(msg) => Ok(Json.obj("status" -> "OK", "msg" -> msg))
                  case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "cant update"))
                }
            }
          )
        }
        case _ => Future.successful(Unauthorized(
          Json.obj("status" -> "FAIL", "msg" -> "Credentials Invalid in token")
        ))
      }
    }
    else Future.successful(Unauthorized(
      Json.obj("status" -> "FAIL", "msg" -> "No authentication token found"))
    )
  }
}

case class ArticleFormData(address: String, sex: Boolean, description: String, contacts: String, image: String, category: String)