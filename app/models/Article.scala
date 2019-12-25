package models

import java.sql.Timestamp
import java.util.Date

import play.api.libs.json.{Json, OFormat}

case class Article(
                id: Long,
                address: String,
                author: String,
                sex: Boolean,
                description: String,
                contacts: String,
                image: String,
                category: String,
                approved: Boolean = false,
                created_at: Timestamp = new Timestamp(new Date().getTime))

object Article extends JsonFormatter {
  implicit val postForm: OFormat[Article] = Json.format[Article]
}