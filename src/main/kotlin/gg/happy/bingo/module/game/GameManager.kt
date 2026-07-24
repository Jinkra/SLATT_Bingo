package gg.happy.bingo.module.game

import gg.happy.bingo.module.game.impl.Waiting

object GameManager
{
    var phase: GamePhase = Waiting
        set(value)
        {
            field.onEnd()
            value.onStart()
            field = value
        }
}