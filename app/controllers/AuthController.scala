package controllers

import play.api.mvc._
import play.api._
import play.api.libs.Crypto
import play.api.libs.json.Json
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._
import com.wordnik.swagger.annotations._
import ApiDocumentation._

@Api(value = "/auth", description = "Authentication operations")
object AuthController extends Controller {

  val credentials: Map[String, String] =
    Map(
      "user-1" -> "pass",
      "user-2" -> "pass",
      "user-3" -> "pass",
      "user-4" -> "pass",
      "user-5" -> "pass",
      "user-6" -> "pass",
      "user-7" -> "pass",
      "user-8" -> "pass",
      "user-9" -> "pass",
      "user-10" -> "pass"
    )

  @ApiOperation(
    value = "Authenticates the user.",
    notes = "Authenticates the user with a given password. Valid usernames are `user-1` to `user-10`. The password is `pass`",
    nickname = "auth",
    httpMethod = "POST")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "body", value = "JSON body must contain a username and password.", required = false,
      dataType = "application/json", paramType = "body", defaultValue = authBodyDefaultValue)))
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Username and password is valid. Returns a token. The token contains the encrypted username."),
    new ApiResponse(code = 400, message = "Username or password not supplied."),
    new ApiResponse(code = 400, message = "Password is incorrect."),
    new ApiResponse(code = 400, message = "Username doesn't exist.")))
  def auth = Action(parse.json) { request =>
    val usernameAndPasswordFromReqOpt =
      for {
        username <- (request.body \ "username").asOpt[String]
        password <- (request.body \ "password").asOpt[String]
      } yield (username, password)

    Logger.debug(s"Try to authenticate user ${usernameAndPasswordFromReqOpt.map(_._1).getOrElse("None")}")

    usernameAndPasswordFromReqOpt match {
      case None =>
        Logger.debug("Authentication error: Username or password not supplied.")
        BadRequest(Json.obj("error" -> "Username or password not supplied."))
      case Some((username, password)) => {
        if (credentials.contains(username)) {
          if (password == credentials(username)) {
            val token = Crypto.signToken(username)
            Logger.debug(s"Authentication successful. New token: $token")
            Ok(Json.obj("token" -> token))
          } else {
            Logger.debug("Authentication error: Password is incorrect.")
            Unauthorized(Json.obj("error" -> "Password is incorrect."))
          }
        } else {
          Logger.debug("Authentication error: Username doesn't exist.")
          Unauthorized(Json.obj("error" -> "Username doesn't exist."))
        }
      }
    }
  }
}
