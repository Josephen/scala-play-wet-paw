package services

import com.google.inject.Inject
import models.{AdminUserTable, User}
import repositories.UserRepository

import scala.concurrent.Future

class UserService @Inject() (users: UserRepository) {

  def listAll: Future[Seq[AdminUserTable]] = {
    users.listAllUsers
  }

  def getUser(username: String): Future[User] = {
    users.read(username)
  }

  def addUser(email: String, username: String, password: String): Future[Option[String]] =
    users.register(email, username, password)


  def deleteUser(username: String): Future[Option[String]] =
    users.delete(username)

  def authenticate(username: String, password: String): Future[Option[String]] =
    users.authenticate(username, password)


}
