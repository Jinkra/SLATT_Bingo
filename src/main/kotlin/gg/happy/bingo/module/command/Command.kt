package gg.happy.bingo.module.command

import gg.happy.bingo.module.gui.Gui
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand

@CommandHeader("Bingo")
object Command
{
    @CommandBody
    val main = mainCommand {
        literal("admin") {
            execute<Player> { sender, _, _ ->
                if (!sender.hasPermission("bingo.admin")) {
                    sender.sendMessage("${ChatColor.RED}You do not have permission to do that.")
                    return@execute
                }
                Gui.openAdmin(sender)
            }
        }
        execute<Player> { sender, _, _ ->
            Gui.openBoard(sender)
        }
    }

    @CommandBody
    val debug = subCommand {
    }
}
