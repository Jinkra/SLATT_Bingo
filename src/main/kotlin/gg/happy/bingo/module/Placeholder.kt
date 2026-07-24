package gg.happy.bingo.module

import gg.happy.bingo.module.conf.Conf
import gg.happy.bingo.module.game.GameManager
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object Placeholder : PlaceholderExpansion
{
    override val identifier: String
        get() = Conf.placeholderIdentifier

    override fun onPlaceholderRequest(player: Player?, args: String): String
    {
        return when (args.lowercase()) {
            "team" -> player?.let { TeamManager.teamOf(it)?.displayName } ?: ""
            "phase" -> GameManager.phase.javaClass.simpleName
            else -> ""
        }
    }
}
