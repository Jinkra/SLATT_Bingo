package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import gg.happy.bingo.module.listener.SneakSwapListener

object Finished: GamePhase
{
    override fun onStart()
    {
        TODO("Not yet implemented")
    }

    override fun onEnd()
    {
        SneakSwapListener.unregister()
    }
}