package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import gg.happy.bingo.module.listener.SneakSwapListener
import gg.happy.bingo.module.game.GameManager
import org.bukkit.Bukkit
import taboolib.common.platform.function.submit

object Ready : GamePhase
{
    override fun onStart()
    {
        SneakSwapListener.register()
        Bukkit.broadcastMessage("Bingo begins in 5 seconds.")
        submit(delay = 100) {
            if (GameManager.phase === Ready) GameManager.phase = Main
        }
    }

    override fun onEnd()
    {
        SneakSwapListener.unregister()
    }
}
