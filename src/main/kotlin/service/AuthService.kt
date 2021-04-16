package service

import JavalinRole
import dto.Login
import entity.Dessert
import entity.Usuario
import io.javalin.http.Context
import kotliquery.*

object AuthService {
    fun login(ctx: Context): Boolean {
        var result = false
        val login = ctx.bodyValidator<Login>().get()
        var user = Usuario()

        val query = queryOf(
            "SELECT UUID_TO_CHAR(ID) AS ID, USERNAME, ROLENAME, ENABLED FROM USUARIO WHERE USERNAME = ? AND CONTRASENA = ? AND ENABLED = 'T'",
            login.username, login.contrasena
        ).map {
            rowToUsuario(it)
        }

        using(sessionOf(HikariCP.dataSource())) {
            user = it.run(query.asSingle) ?: Usuario()
        }

        if(!user.username.isNullOrBlank()){
            ctx.sessionAttribute("current-user", user)
            result = true
        }

        return result
    }

    fun signout(ctx: Context): Boolean {
        var isSignin = (ctx.sessionAttribute<Usuario>("current-user") ?: Usuario()).username != null
        if(isSignin){
            ctx.sessionAttribute("current-user",null)
        }
        return !isSignin;
    }

    private fun rowToUsuario(row: Row): Usuario {
        return Usuario(
            id = row.stringOrNull("ID"),
            username = row.stringOrNull("USERNAME"),
            rolename = row.stringOrNull("ROLENAME"),
            enabled = row.stringOrNull("ENABLED")
        )
    }

    fun rolenameToJavalinRole(text: String): JavalinRole{
        when(text){
            "USER" -> {return JavalinRole.USER}
            "ADMIN" -> {return JavalinRole.ADMIN}
        }
        return JavalinRole.ANYONE
    }
}