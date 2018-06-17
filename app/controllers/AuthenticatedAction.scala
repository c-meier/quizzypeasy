package controllers

import dao.{QuizzesDAO, UsersDAO}
import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.libs.typedmap.TypedKey
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val userInfo: Option[(Long, String, Boolean)], request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedAction @Inject()(val parser: BodyParsers.Default, usersDAO: UsersDAO, quizzesDAO: QuizzesDAO)(implicit val executionContext: ExecutionContext)
  extends ActionBuilder[AuthenticatedRequest, AnyContent] with ActionTransformer[Request, AuthenticatedRequest] {


  def transform[A](request: Request[A]): Future[AuthenticatedRequest[A]] = {
    request.session.get("connected") match {
      case None =>
        Future.successful(new AuthenticatedRequest(None, request))
      case Some(u) =>
        val user = for {
          u <- usersDAO.findByName(u)
          if u.isDefined
        } yield u
        user.map[AuthenticatedRequest[A]]({
          case Some(User(Some(id), name, _, _, isAdmin)) => {
            val userInfo = (id, name, isAdmin)
            new AuthenticatedRequest(Some(userInfo), request)
          }
          case None => new AuthenticatedRequest(None, request)
        })
    }
  }

  def PermissionCheckAction(implicit ec: ExecutionContext) = new ActionFilter[AuthenticatedRequest] {
    def executionContext = ec

    def filter[A](input: AuthenticatedRequest[A]) = Future.successful {
      if (input.userInfo.isEmpty)
        Some(Forbidden("You are not authorized"))
      else
        None
    }
  }
}

