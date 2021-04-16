package controller

import entity.Dessert
import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import service.DessertService
import java.util.concurrent.CompletableFuture.supplyAsync

class DessertsCrudController : CrudHandler {
    override fun create(ctx: Context) {
        ctx.json(supplyAsync { DessertService.insert(ctx) })
    }

    override fun delete(ctx: Context, resourceId: String){
        ctx.result(supplyAsync { DessertService.delete(resourceId) })
    }

    override fun getAll(ctx: Context) {
        ctx.json(supplyAsync { DessertService.getAll() })
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.json(supplyAsync { DessertService.getOne(resourceId) })
    }

    override fun update(ctx: Context, resourceId: String) {
        ctx.result(supplyAsync { DessertService.update(ctx, resourceId) })
    }
}