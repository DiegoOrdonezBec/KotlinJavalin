package controller

import io.javalin.http.Context
import service.AuthService
import java.util.concurrent.CompletableFuture

object AuthController {
    fun login(ctx: Context){
        ctx.json(CompletableFuture.supplyAsync { AuthService.login(ctx) })
    }

    fun signout(ctx: Context){
        ctx.json(CompletableFuture.supplyAsync { AuthService.signout(ctx) })
    }
}