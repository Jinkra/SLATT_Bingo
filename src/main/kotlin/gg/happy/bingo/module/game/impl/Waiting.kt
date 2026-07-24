package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import org.bukkit.Bukkit

object Waiting : GamePhase
{
    override fun onStart()
    {
        Bukkit.broadcastMessage("Bingo 正在等待管理员开始比赛。")
    }

    override fun onEnd()
    {
        // No state-specific resources are retained while waiting.
    }

}
