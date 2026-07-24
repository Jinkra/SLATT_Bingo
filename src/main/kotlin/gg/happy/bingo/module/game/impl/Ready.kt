package gg.happy.bingo.module.game.impl

import gg.happy.bingo.module.game.GamePhase
import gg.happy.bingo.module.listener.SneakSwapListener

object Ready : GamePhase
{
    override fun onStart()
    {
        SneakSwapListener.register()
    }

    override fun onEnd()
    {
        TODO("Not yet implemented")
    }
}