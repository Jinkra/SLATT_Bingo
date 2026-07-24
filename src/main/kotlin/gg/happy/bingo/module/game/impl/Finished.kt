package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import gg.happy.bingo.module.BingoTeam
import org.bukkit.Bukkit

object Finished: GamePhase
{
    var winner: BingoTeam? = null

    override fun onStart()
    {
        winner?.let { Bukkit.broadcastMessage("${it.color}${it.displayName} won this Bingo match!") }
            ?: Bukkit.broadcastMessage("Bingo match finished: time is up.")
    }

    override fun onEnd()
    {
        // The ready phase owns the temporary sneak-and-swap listener.
    }
}
