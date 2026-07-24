package gg.happy.bingo.module.game

import gg.happy.bingo.module.game.impl.Waiting
import gg.happy.bingo.module.game.impl.Ready
import gg.happy.bingo.module.game.impl.Finished
import gg.happy.bingo.module.BingoTeam
import gg.happy.bingo.module.Card
import gg.happy.bingo.module.conf.Conf

object GameManager
{
    var matchEndsAt: Long? = null
        private set
    var phase: GamePhase = Waiting
        set(value)
        {
            field.onEnd()
            field = value
            value.onStart()
        }

    fun canStart() = (phase === Waiting || phase === Finished) && Conf.items.distinct().size >= Card.SIZE

    fun start()
    {
        if (canStart()) phase = Ready
    }

    fun beginTimer(durationSeconds: Int)
    {
        matchEndsAt = System.currentTimeMillis() + durationSeconds * 1000L
    }

    fun remainingSeconds(): Int = matchEndsAt
        ?.let { ((it - System.currentTimeMillis()).coerceAtLeast(0) + 999L).toInt() / 1000 }
        ?: 0

    fun finish(winner: BingoTeam? = null)
    {
        Finished.winner = winner
        matchEndsAt = null
        phase = Finished
    }
}
