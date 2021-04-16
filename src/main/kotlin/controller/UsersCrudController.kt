package controller

import entity.Usuario
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import service.DessertService
import service.UserService
import java.util.concurrent.CompletableFuture

class UsersCrudController : CrudHandler {
    override fun create(ctx: Context) {
        ctx.json(CompletableFuture.supplyAsync { UserService.insert(ctx) })
    }

    override fun delete(ctx: Context, resourceId: String){
        ctx.result(CompletableFuture.supplyAsync { UserService.delete(resourceId) })
    }

    override fun getAll(ctx: Context) {
        ctx.json(CompletableFuture.supplyAsync { UserService.getAll() })
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.json(CompletableFuture.supplyAsync { UserService.getOne(resourceId) })
    }

    override fun update(ctx: Context, resourceId: String) {
        ctx.result(CompletableFuture.supplyAsync { UserService.update(ctx, resourceId) })
    }
}