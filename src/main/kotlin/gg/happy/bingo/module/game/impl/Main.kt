package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import gg.happy.bingo.module.Card
import gg.happy.bingo.module.TeamManager
import gg.happy.bingo.module.conf.Conf
import gg.happy.bingo.module.game.GameManager
import org.bukkit.Bukkit
import taboolib.common.platform.function.submit

object Main : GamePhase
{
    override fun onStart()
    {
        Card.generate()
        TeamManager.clearProgress()
        Finished.winner = null
        GameManager.beginTimer(Conf.gameDurationSeconds)
        submit(delay = Conf.gameDurationSeconds * 20L) {
            if (GameManager.phase === Main) GameManager.finish()
        }
        Bukkit.broadcastMessage("Bingo has started! Use /bingo to view the board.")
    }

    override fun onEnd()
    {
        // Progress remains available for the finished screen.
    }
}
