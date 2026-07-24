package gg.happy.bingo.module.listener

import gg.happy.bingo.module.Card
import gg.happy.bingo.module.TeamManager
import gg.happy.bingo.module.game.GameManager
import gg.happy.bingo.module.game.impl.Main
import gg.happy.bingo.module.scoreboard.BingoScoreboard
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.Bukkit

object GameProgressListener : Listener
{
    @EventHandler(ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) = record(event.player, event.block.type)

    @EventHandler(ignoreCancelled = true)
    fun onPickup(event: EntityPickupItemEvent)
    {
        val player = event.entity as? Player ?: return
        record(player, event.item.itemStack.type)
    }

    @EventHandler(ignoreCancelled = true)
    fun onCraft(event: CraftItemEvent)
    {
        val player = event.whoClicked as? Player ?: return
        record(player, event.currentItem?.type ?: return)
    }

    private fun record(player: Player, material: Material)
    {
        if (GameManager.phase !== Main) return
        val team = TeamManager.teamOf(player) ?: return
        val index = Card.items.indexOf(material)
        if (index < 0 || !TeamManager.markCompleted(team, index)) return
        Bukkit.broadcastMessage("${team.color}${team.displayName} 收集了 ${material.name.lowercase().replace('_', ' ')}。")
        BingoScoreboard.updateAll()
        if (TeamManager.hasBingo(team)) GameManager.finish(team)
    }
}
