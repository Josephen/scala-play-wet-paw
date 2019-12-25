package models

import java.sql.Timestamp
import java.util.Date

import play.api.libs.json.{Json, OFormat}

case class User (
                  id: Long,
                  email : String,
                  username : String,
                  password : String,
                  created_at: Timestamp = new Timestamp(new Date().getTime))

object User extends JsonFormatter {
  implicit val userForm: OFormat[User] = Json.format[User]
}