package controllers


import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc._
import services.{ArticleService, UserService}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex

class UserController @Inject () (
                                         userService: UserService,
                                         articleService: ArticleService,
                                         cc: MessagesControllerComponents
                                       )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {
  val userForm: Form[UserFormData] = Form {
    mapping(
      "email" -> email,
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserFormData.apply)(UserFormData.unapply)
  }

  val loginForm: Form[LoginFormData] = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginFormData.apply)(LoginFormData.unapply)
  }

  def signUp = Action.async { implicit  request =>
    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(s"Form submission with error: ${errorForm}"))
      },
      data => {
        //if (validateEmail(data.email))
        userService.addUser(data.email, data.username, data.password )
          .map {
            case Some(msg) => Ok(Json.obj("status" -> "OK", "msg" -> msg))
            case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "email or username already exists"))
          }
        //else Future.successful(BadRequest(Json.obj("status" -> "FAIL", "msg" -> "Incorrect email form")))
      }
    )
  }

  def signIn = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(s"Form submission with error: ${errorForm}"))
      },
      data => {
      userService.authenticate(data.username, data.password)
        .map {
          case Some(token) => Ok(Json.obj("status" -> "SUCCESS", "username" -> data.username, "token" -> token))
          case _ => BadRequest(Json.obj("status" -> "FAIL", "msg" -> "fail"))
        }
      })
  }

  def getUser(username: String) = Action.async { _ =>
    userService.getUser(username).map (res =>
      Ok(s"${res.username}, ${res.email}"))
  }

  private def validateEmail(email: String): Boolean = {
    val reg: Regex = """(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r
    reg.pattern.matcher(email).matches()
  }

}
case class UserFormData(email : String, username : String, password : String)
case class LoginFormData (username : String, password : String)
