package gg.happy.bingo.module.command

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("Bingo")
object Command
{
    @CommandBody
    val main = mainCommand {

    }
}