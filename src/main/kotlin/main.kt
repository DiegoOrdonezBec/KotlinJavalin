import config.SessionHandler
import controller.AuthController
import controller.DessertsCrudController
import controller.UsersCrudController
import entity.Usuario
import io.javalin.Javalin
import io.javalin.plugin.rendering.vue.VueComponent
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.http.Context
import io.javalin.http.Handler
import kotliquery.HikariCP
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory
import org.eclipse.jetty.http2.HTTP2Cipher
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory
import org.eclipse.jetty.server.*
import org.eclipse.jetty.util.ssl.SslContextFactory
import service.AuthService

enum class JavalinRole : Role {
    ANYONE, USER, ADMIN
}

fun main() {
    HikariCP.default("jdbc:firebirdsql://localhost:3050/JavalinVue?encoding=UTF8","sysdba","masterkey")

    val app = Javalin.create {
        it.enableWebjars()
        it.enableCorsForAllOrigins()
        it.sessionHandler{SessionHandler.sqlSessionHandler()}
        it.accessManager { handler: Handler, ctx: Context, roles: Set<Role> ->
            val currentUser = ctx.sessionAttribute("current-user") ?: Usuario()
            when {
                currentUser.username != null && ctx.path() == "/login" -> ctx.redirect("/")
                currentUser.username == null && !roles.contains(AuthService.rolenameToJavalinRole(currentUser.rolename ?: "ANYONE")) -> ctx.redirect("/login")
                roles.contains(AuthService.rolenameToJavalinRole(currentUser.rolename ?: "ANYONE")) -> handler.handle(ctx)
                else -> ctx.redirect("/")
            }
        }
        it.server {createHttp2Server()}
    }.start(8080)

    app.routes {
        get("/login", VueComponent("login-page"), roles(JavalinRole.ANYONE))
        get("/", VueComponent("home-page"), roles(JavalinRole.USER, JavalinRole.ADMIN))
        get("/table", VueComponent("table-page"), roles(JavalinRole.USER, JavalinRole.ADMIN))
        get("/users", VueComponent("users-page"), roles(JavalinRole.ADMIN))
        path("/api") {
            post("/auth/", AuthController::login, roles(JavalinRole.ANYONE))
            post("/auth/signout", AuthController::signout, roles(JavalinRole.USER, JavalinRole.ADMIN))
            crud("/dessert/:id", DessertsCrudController(), roles(JavalinRole.USER, JavalinRole.ADMIN))
            crud("/user/:id", UsersCrudController(), roles(JavalinRole.ADMIN))
        }
    }
}

private fun createHttp2Server(): Server {
    val alpn = ALPNServerConnectionFactory().apply {
        defaultProtocol = "h2"
    }

    val sslContextFactory = SslContextFactory.Client().apply {
        keyStorePath = this::class.java.getResource("/keystore.jks").toExternalForm()
        //keyStorePath = Resource.newClassPathResource("/keystore.jks").toString()
        setKeyStorePassword("claveKeystore")
        cipherComparator = HTTP2Cipher.COMPARATOR
        provider = "Conscrypt"
    }

    val ssl = SslConnectionFactory(sslContextFactory, alpn.protocol)

    val httpsConfig = HttpConfiguration().apply {
        sendServerVersion = false
        secureScheme = "https"
        securePort = 8443
        addCustomizer(SecureRequestCustomizer())
    }

    val http2 = HTTP2ServerConnectionFactory(httpsConfig)

    val fallback = HttpConnectionFactory(httpsConfig)

    return Server().apply {

        addConnector(ServerConnector(server).apply {
            port = 8080
        })

        addConnector(ServerConnector(server, ssl, alpn, http2, fallback).apply {
            port = 8443
        })
    }

}
