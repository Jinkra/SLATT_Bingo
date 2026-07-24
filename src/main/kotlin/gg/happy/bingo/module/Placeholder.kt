package gg.happy.bingo.module

import gg.happy.bingo.module.conf.Conf
import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object Placeholder : PlaceholderExpansion
{
    override val identifier: String
        get() = Conf.placeholderIdentifier

    override fun onPlaceholderRequest(player: Player?, args: String): String
    {
        return "null"
    }
}