package repositories

import java.sql.Timestamp
import java.util.Date

import org.mindrot.jbcrypt.BCrypt
import com.google.inject.Inject
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms._
import models.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import models.LoginUser
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import utilities.JwtUtility
import slick.jdbc.MySQLProfile.api._



class UserTable(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def email = column[String]("email", O.Unique)
  def username = column[String]("username", O.Unique)
  def password = column[String]("password")
  def created_at =
    column[Timestamp]("created_at",
      O.Default(new Timestamp(new Date().getTime)))
  override def * = (id, email, username, password, created_at) <>((User.apply _).tupled, User.unapply)
}

class LoginTable(tag: Tag) extends Table[LoginUser](tag, "users") {
  def username = column[String]("username", O.Unique)
  def password = column[String]("password")
  override def * = (username, password) <> ((LoginUser.apply _).tupled, LoginUser.unapply)
}
class UserRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider
                               )(
                                 implicit executionContext: ExecutionContext
                               ) extends HasDatabaseConfigProvider[JdbcProfile] {
  val users = TableQuery[UserTable]
  val loginU = TableQuery[LoginTable]
  def listAll: Future[Seq[User]] ={
    db.run(users.result)
  }

  def register(email: String, username: String, password: String): Future[Option[String]] =
    db.run(users.filter(_.email === email).result.headOption).flatMap {
      case None => db.run{
        val hashedPassword = getHashedPassword(password)
        users += User(0, email, username, hashedPassword)}
        .map(_=> Some("User successfully registered"))

      case _ => Future(None)
    }

  def read (username: String): Future[User] =
    db.run(users.filter(_.username === username).result.head)

  def authenticate(username: String, password: String): Future[Option[String]] = {
    db.run(loginU.filter(_.username === username).result.headOption).map{
      case Some(LoginUser(usr, pwd)) => {
        if (BCrypt.checkpw(password, pwd)) Some(
          JwtUtility.createToken(Json.obj("username" -> username,
            "password" -> password).toString
        ))
        else None
      }
      case _ => None
    }
  }

  def delete (id: Long): Future[Option[String]] =
    db.run(users.filter(_.id === id).result.headOption).flatMap
    {
      case None => Future(None)
      case _ => db.run(users.filter(_.id === id).delete)
        .map(_=> Some("User deleted"))
    }

  private def getHashedPassword(password: String): String = {
    val salt = BCrypt.gensalt()
    BCrypt.hashpw(password, salt)
  }
}