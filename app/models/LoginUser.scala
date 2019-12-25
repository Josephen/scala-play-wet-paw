package models

import play.api.libs.json.{Json, OFormat}

case class LoginUser(
                    username : String,
                    password : String)

object LoginUser extends JsonFormatter {
  implicit val userForm: OFormat[User] = Json.format[User]
}
