package gg.happy.bingo.module.scoreboard

import gg.happy.bingo.module.TeamManager
import gg.happy.bingo.module.game.GameManager
import gg.happy.bingo.module.game.impl.Finished
import gg.happy.bingo.module.game.impl.Main
import gg.happy.bingo.module.game.impl.Ready
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

object BingoScoreboard
{
    private val title = "${ChatColor.GOLD}${ChatColor.BOLD}SLATT Bingo"

    fun updateAll()
    {
        Bukkit.getOnlinePlayers().forEach(::update)
    }

    fun update(player: Player)
    {
        val manager = Bukkit.getScoreboardManager() ?: return
        val board = manager.newScoreboard
        val objective = board.registerNewObjective("slatt_bingo", "dummy", title)
        objective.displaySlot = DisplaySlot.SIDEBAR

        val lines = mutableListOf(
            ChatColor.BLACK.toString(),
            "${ChatColor.WHITE}State: ${stateName()}",
            ChatColor.DARK_BLUE.toString(),
            "${ChatColor.WHITE}Your team: ${TeamManager.teamOf(player)?.displayName ?: "${ChatColor.GRAY}None"}",
            "${ChatColor.WHITE}Time: ${formatDuration(GameManager.remainingSeconds())}",
            ChatColor.DARK_GREEN.toString()
        )
        TeamManager.teams.take(9).forEachIndexed { index, team ->
            val winner = if (GameManager.phase is Finished && Finished.winner?.id == team.id) " ${ChatColor.GOLD}WIN" else ""
            val uniqueSuffix = ChatColor.values()[index + 3]
            lines += "${team.color}${team.displayName} ${ChatColor.WHITE}${TeamManager.completedCount(team)}/25$winner$uniqueSuffix"
        }
        lines.take(15).forEachIndexed { index, line -> objective.getScore(line).score = 15 - index }
        player.scoreboard = board
    }

    private fun stateName(): String = when (GameManager.phase) {
        Main -> "${ChatColor.GREEN}Running"
        Ready -> "${ChatColor.YELLOW}Starting"
        is Finished -> "${ChatColor.GOLD}Finished"
        else -> "${ChatColor.GRAY}Waiting"
    }

    private fun formatDuration(seconds: Int) = "%d:%02d".format(seconds / 60, seconds % 60)
}
