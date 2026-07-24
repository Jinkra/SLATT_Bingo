package gg.happy.bingo.util

import org.bukkit.entity.Player
import taboolib.common.platform.function.adaptCommandSender
import taboolib.common.platform.function.console
import taboolib.module.kether.KetherShell
import taboolib.module.kether.ScriptOptions
import java.util.concurrent.CompletableFuture


fun Player.runKether(script: List<String>): CompletableFuture<Any?>
{
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(this),
            detailError = true
        )
    )
}

fun Player.runKether(script: String): CompletableFuture<Any?>
{
    return KetherShell.eval(
        script, options = ScriptOptions(
            sender = adaptCommandSender(this),
            detailError = true
        )
    )
}

fun List<String>.runAsKether(sender: Any? = console()): CompletableFuture<Any?>
{
    return KetherShell.eval(
        this, options = ScriptOptions(
            sender = adaptCommandSender(sender ?: console()),
            detailError = true
        )
    )
}

fun String.runAsKether(sender: Any? = console()): CompletableFuture<Any?>
{
    return KetherShell.eval(
        this, options = ScriptOptions(
            sender = adaptCommandSender(sender ?: console()),
            detailError = true
        )
    )
}