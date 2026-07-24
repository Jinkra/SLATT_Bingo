package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import gg.happy.bingo.module.BingoTeam
import org.bukkit.Bukkit

object Finished: GamePhase
{
    var winner: BingoTeam? = null

    override fun onStart()
    {
        winner?.let { Bukkit.broadcastMessage("${it.color}${it.displayName} 赢得了本局 Bingo！") }
            ?: Bukkit.broadcastMessage("Bingo 比赛结束，时间到！")
    }

    override fun onEnd()
    {
        // The ready phase owns the temporary sneak-and-swap listener.
    }
}
