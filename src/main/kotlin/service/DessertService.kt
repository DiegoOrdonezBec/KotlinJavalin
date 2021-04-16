package service

import entity.Dessert
import io.javalin.http.Context
import kotliquery.*
import java.util.*

object DessertService {
    fun insert(ctx: Context): String{
        val id = UUID.randomUUID().toString();
        val dessert = ctx.bodyValidator<Dessert>().get()
        var result = ""

        val query = queryOf("""INSERT INTO POSTRE (ID, NAME, CARBS, PROTEINS, FAT, CALORIES)  
            VALUES(CHAR_TO_UUID(?),?,?,?,?,?)""".trimMargin(),
            id, dessert.name, dessert.carbs, dessert.proteins, dessert.fat, dessert.calories)

        using(sessionOf(HikariCP.dataSource())) {
            result = if(it.run(query.asUpdate) > 0) id else "FAIL"
        }

        return result
    }

    fun update(ctx: Context, resourceId: String): String{
        val dessert = ctx.bodyValidator<Dessert>().get()
        var result = ""

        val query = queryOf("""UPDATE POSTRE SET NAME = ?, CARBS = ?, PROTEINS = ?, FAT = ?, CALORIES = ?  
            WHERE ID = CHAR_TO_UUID(?)""".trimMargin(),
            dessert.name, dessert.carbs, dessert.proteins, dessert.fat, dessert.calories, resourceId)

        using(sessionOf(HikariCP.dataSource())) {
            result = if(it.run(query.asUpdate) > 0) resourceId else "FAIL"
        }

        return result
    }

    fun delete(resourceId: String): String{
        var result = ""

        val query = queryOf("DELETE FROM POSTRE WHERE ID = CHAR_TO_UUID(?)", resourceId)

        using(sessionOf(HikariCP.dataSource())) {
            result = if(it.run(query.asUpdate) > 0) "DONE" else "FAIL"
        }

        return result
    }

    fun getOne(resourceId: String): Dessert{
        var result = Dessert()

        val query = queryOf(
            "SELECT UUID_TO_CHAR(ID) AS ID, NAME, CALORIES, FAT, CARBS, PROTEINS FROM POSTRE WHERE ID = CHAR_TO_UUID(?)",
            resourceId
        ).map {
            rowToDessert(it)
        }

        using(sessionOf(HikariCP.dataSource())) {
            result = it.run(query.asSingle) ?: Dessert()
        }

        return result
    }

    fun getAll(): List<Dessert>{
        var result: List<Dessert> = ArrayList<Dessert>()

        val query = queryOf(
            "SELECT UUID_TO_CHAR(ID) AS ID, NAME, CALORIES, FAT, CARBS, PROTEINS FROM POSTRE"
        ).map {
            rowToDessert(it)
        }

        using(sessionOf(HikariCP.dataSource())) {
            result = it.run(query.asList)
        }

        return result
    }

    private fun rowToDessert(row: Row): Dessert {
        return Dessert(
            id = row.stringOrNull("ID"),
            name = row.stringOrNull("NAME"),
            calories = row.doubleOrNull("CALORIES"),
            fat = row.doubleOrNull("FAT"),
            carbs = row.doubleOrNull("CARBS"),
            proteins = row.doubleOrNull("PROTEINS")
        )
    }
}