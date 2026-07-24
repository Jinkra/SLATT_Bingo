package gg.happy.bingo.module.command

import gg.happy.bingo.module.conf.Conf
import gg.happy.bingo.util.runKether
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
        execute<Player> { sender, _, _ ->
            sender.runKether(Conf.mainCommand)
        }
    }

    @CommandBody
    val debug = subCommand {
    }
}