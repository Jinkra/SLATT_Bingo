package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import org.bukkit.Bukkit

object Waiting : GamePhase
{
    override fun onStart()
    {
        Bukkit.broadcastMessage("Bingo is waiting for an administrator to start a match.")
    }

    override fun onEnd()
    {
        // No state-specific resources are retained while waiting.
    }

}
