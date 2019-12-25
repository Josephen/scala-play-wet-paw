package services

import com.google.inject.Inject
import models.User
import repositories.UserRepository

import scala.concurrent.Future

class UserService @Inject() (users: UserRepository) {

  def listAll: Future[Seq[User]] = {
    users.listAll
  }

  def getUser(username: String): Future[User] = {
    users.read(username)
  }

  def addUser(email: String, username: String, password: String): Future[Option[String]] =
    users.register(email, username, password)


  def deleteUser(id: Long): Future[Option[String]] =
    users.delete(id)

  def authenticate(username: String, password: String): Future[Option[String]] =
    users.authenticate(username, password)


}
