package com.poulastaa.routes.auth

import com.poulastaa.data.model.EndPoints
import com.poulastaa.data.model.auth.res.VerifiedMailStatus
import com.poulastaa.data.repository.ServiceRepository
import com.poulastaa.routes.utils.setCookie
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.verifySignUpEmail(
    service: ServiceRepository
) {
    route(EndPoints.VerifySignUpEmail.route) {
        get {
            val token = call.parameters["token"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

            val status = service.updateSignUpVerificationStatus(token)

            res(status, call)
        }
    }
}

fun Route.verifyLogInEmail(
    service: ServiceRepository
) {
    route(EndPoints.VerifyLogInEmail.route) {
        get {
            val token = call.parameters["token"] ?: return@get call.respondRedirect(EndPoints.UnAuthorised.route)

            val status = service.updateLogInVerificationStatus(token)

            if (status.first == VerifiedMailStatus.VERIFIED) setCookie(status.second.first, status.second.second)

            res(status.first, call)
        }
    }
}

private suspend fun res(
    status: VerifiedMailStatus,
    call: ApplicationCall
) {
    when (status) {
        VerifiedMailStatus.VERIFIED -> call.respondText(emailVerifiedRes(), ContentType.Text.Html)

        VerifiedMailStatus.TOKEN_USED -> {
            val html = File("static/AuthTokenUsed.html").readText()
            val css = File("static/errStyles.css").readText()

            call.respondText(otherResponse(html, css), ContentType.Text.Html)
        }

        VerifiedMailStatus.USER_NOT_FOUND -> {
            val html = File("static/AuthUserNotFound.html").readText()
            val css = File("static/errStyles.css").readText()

            call.respondText(otherResponse(html, css), ContentType.Text.Html)
        }

        else -> {
            val html = File("static/AuthInternalErr.html").readText()
            val css = File("static/errStyles.css").readText()

            call.respondText(otherResponse(html, css), ContentType.Text.Html)
        }
    }
}

private fun emailVerifiedRes(): String {
    val html = File("static/AuthSuccess.html").readText()
    val css = File("static/styles.css").readText()

    return """
        <!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Authentication Successful</title>
    <style>
    $css
    </style>
  </head>
  <body>
  $html    
  </body>
  </html>
    """.trimIndent()
}

private fun otherResponse(html: String, css: String): String {
    return """
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Authentication Failed</title>
      <style>
    $css
    </style>  
</head>
<body>
  </head>
  <body>
  $html    
  </body>
  </html>
    """.trimIndent()
}