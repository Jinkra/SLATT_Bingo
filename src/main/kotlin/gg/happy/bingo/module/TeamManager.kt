package gg.happy.bingo.module

import gg.happy.bingo.Bingo
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.UUID

data class BingoTeam(val id: String, val displayName: String, val color: ChatColor)

object TeamManager
{
    private val completed = mutableMapOf<String, MutableSet<Int>>()

    val teams: List<BingoTeam>
        get()
        {
            val section = Bingo.conf.getConfigurationSection("teams") ?: return emptyList()
            return section.getKeys(false).map { id ->
                val base = "teams.$id"
                BingoTeam(
                    id,
                    ChatColor.translateAlternateColorCodes('&', Bingo.conf.getString("$base.display", id)!!),
                    runCatching { ChatColor.valueOf(Bingo.conf.getString("$base.color", "WHITE")!!.uppercase()) }
                        .getOrDefault(ChatColor.WHITE)
                )
            }
        }

    fun teamOf(player: Player): BingoTeam? = teamOf(player.uniqueId)

    fun teamOf(uuid: UUID): BingoTeam?
    {
        val id = Bingo.conf.getString("assignments.$uuid") ?: return null
        return teams.firstOrNull { it.id == id }
    }

    fun assign(player: Player, team: BingoTeam?)
    {
        Bingo.conf.set("assignments.${player.uniqueId}", team?.id)
        Bingo.conf.saveToFile()
    }

    fun clearProgress() = completed.clear()

    fun markCompleted(team: BingoTeam, cardIndex: Int): Boolean =
        completed.getOrPut(team.id) { mutableSetOf() }.add(cardIndex)

    fun isCompleted(team: BingoTeam?, cardIndex: Int): Boolean =
        team != null && completed[team.id]?.contains(cardIndex) == true

    fun completedCount(team: BingoTeam): Int = completed[team.id]?.size ?: 0

    fun hasBingo(team: BingoTeam): Boolean
    {
        val marks = completed[team.id] ?: return false
        val lines = listOf(
            intArrayOf(0, 1, 2, 3, 4), intArrayOf(5, 6, 7, 8, 9), intArrayOf(10, 11, 12, 13, 14),
            intArrayOf(15, 16, 17, 18, 19), intArrayOf(20, 21, 22, 23, 24),
            intArrayOf(0, 5, 10, 15, 20), intArrayOf(1, 6, 11, 16, 21), intArrayOf(2, 7, 12, 17, 22),
            intArrayOf(3, 8, 13, 18, 23), intArrayOf(4, 9, 14, 19, 24),
            intArrayOf(0, 6, 12, 18, 24), intArrayOf(4, 8, 12, 16, 20)
        )
        return lines.any { line -> line.all { it in marks } }
    }
}
