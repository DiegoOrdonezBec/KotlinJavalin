package service

import entity.Usuario
import io.javalin.http.Context
import kotliquery.*
import java.util.*

object UserService {
    fun insert(ctx: Context): String{
        val id = UUID.randomUUID().toString();
        val usuario = ctx.bodyValidator<Usuario>().get()
        var result = ""

        val query = queryOf("""INSERT INTO USUARIO (ID, USERNAME, CONTRASENA, ROLENAME, ENABLED)  
            VALUES(CHAR_TO_UUID(?),?,?,?,?)""".trimMargin(),
            id, usuario.username, usuario.contrasena, usuario.rolename, usuario.enabled)

        using(sessionOf(HikariCP.dataSource())) {
            if(usuario.rolename.equals("USER") || usuario.rolename.equals("ADMIN")){
                result = if(it.run(query.asUpdate) > 0) id else "FAIL"
            }else{
                result = "FAIL"
            }
        }

        return result
    }

    fun update(ctx: Context, resourceId: String): String{
        val usuario = ctx.bodyValidator<Usuario>().get()
        var result = ""

        val query = queryOf("""UPDATE USUARIO SET USERNAME = ?, CONTRASENA = ?, ROLENAME = ?, ENABLED = ?   
            WHERE ID = CHAR_TO_UUID(?)""".trimMargin(),
            usuario.username, usuario.contrasena, usuario.rolename, usuario.enabled, resourceId)

        using(sessionOf(HikariCP.dataSource())) {
            if(usuario.rolename.equals("USER") || usuario.rolename.equals("ADMIN")){
                result = if(it.run(query.asUpdate) > 0) resourceId else "FAIL"
            }else{
                result = "FAIL"
            }
        }

        return result
    }

    fun delete(resourceId: String): String{
        return "FAIL"
    }

    fun getOne(resourceId: String): Usuario {
        var result = Usuario()

        val query = queryOf(
            "SELECT UUID_TO_CHAR(ID) AS ID, USERNAME, CONTRASENA, ROLENAME, ENABLED FROM USUARIO WHERE ID = CHAR_TO_UUID(?)",
            resourceId
        ).map {
            rowToUsuario(it)
        }

        using(sessionOf(HikariCP.dataSource())) {
            result = it.run(query.asSingle) ?: Usuario()
        }

        return result
    }

    fun getAll(): List<Usuario>{
        var result: List<Usuario> = ArrayList<Usuario>()

        val query = queryOf(
            "SELECT UUID_TO_CHAR(ID) AS ID, USERNAME, CONTRASENA, ROLENAME, ENABLED FROM USUARIO"
        ).map {
            rowToUsuario(it)
        }

        using(sessionOf(HikariCP.dataSource())) {
            result = it.run(query.asList)
        }

        return result
    }

    private fun rowToUsuario(row: Row): Usuario {
        return Usuario(
            id = row.stringOrNull("ID"),
            username = row.stringOrNull("USERNAME"),
            contrasena = row.stringOrNull("CONTRASENA"),
            rolename = row.stringOrNull("ROLENAME"),
            enabled = row.stringOrNull("ENABLED")
        )
    }
}