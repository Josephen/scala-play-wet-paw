package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{MessagesAbstractController, MessagesControllerComponents}
import play.mvc.Action
import services.{ArticleService, UserService}
import utilities.JwtUtility

import scala.concurrent.{ExecutionContext, Future}

class AdminController @Inject () (
                                   userService: UserService,
                                   articleService: ArticleService,
                                   cc: MessagesControllerComponents,
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  def canDeleteAllArticle(id: Long) = Action.async { implicit request =>
    val token = request.headers.get("Authorization").getOrElse("")
    if (token == "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InJhYmJpdCIsInBhc3N3b3JkIjoiQWxleGV5Jk5pa2l0YTI1MDYxNDA3In0.DEeXErDBpEimUAEPJRhSCwkUO2l-0FnOKOeXhZencBA") {
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
      Json.obj("status" -> "FAIL", "msg" -> "Sosi pisu"))
    )
  }

  def deleteUser(id: Long) = Action.async { implicit request =>
    val token = request.headers.get("Authorization").getOrElse("")
    if (token == "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InJhYmJpdCIsInBhc3N3b3JkIjoiQWxleGV5Jk5pa2l0YTI1MDYxNDA3In0.DEeXErDBpEimUAEPJRhSCwkUO2l-0FnOKOeXhZencBA") {
      val payload = JwtUtility.decodePayload(token)
      payload match {
        case Some(cred) => userService.deleteUser(id).map {
          case Some(msg) => Ok(Json.obj("status" -> "OK", "msg" -> msg))
          case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "nothing to delete"))
        }
        case _ => Future.successful(BadRequest(Json.obj("status" -> "FAIL", "msg" -> "cant delete")))
      }
    }
    else Future.successful(Unauthorized(
      Json.obj("status" -> "FAIL", "msg" -> "Sosi pisu"))
    )
  }

  def approveArticle(id:Long) = Action.async { implicit request =>
    val token = request.headers.get("Authorization").getOrElse("")
    if (token == "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InJhYmJpdCIsInBhc3N3b3JkIjoiQWxleGV5Jk5pa2l0YTI1MDYxNDA3In0.DEeXErDBpEimUAEPJRhSCwkUO2l-0FnOKOeXhZencBA") {
      val payload = JwtUtility.decodePayload(token)
      payload match {
        case Some(cred) => articleService.approveArticle(id).map {
          case Some(msg) => Ok(Json.obj("status" -> "OK", "msg" -> msg))
          case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "nothing to approve"))
        }
        case _ => Future.successful(BadRequest(Json.obj("status" -> "FAIL", "msg" -> "cant approve")))
      }
    }
    else Future.successful(Unauthorized(
      Json.obj("status" -> "FAIL", "msg" -> "incorrect token"))
    )
  }

  def listArticlesByCategory(category: String) = Action.async { implicit request =>
    articleService.findCategoryArticles(category).map{ articles =>
      Ok(Json.toJson(articles.reverse))
    }
  }


  def listArticles = Action.async{ implicit request =>
    articleService.listAll.map { articles=>
      Ok(Json.toJson(articles.reverse))
    }
  }
}

