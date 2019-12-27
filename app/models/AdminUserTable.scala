package models

import play.api.libs.json.{Json, OFormat}

case class AdminUserTable(
                           email : String,
                           username : String
                         )

object AdminUserTable extends JsonFormatter {
  implicit val adminUserForm: OFormat[AdminUserTable] = Json.format[AdminUserTable]
}
