package gg.happy.bingo.module.gui

import gg.happy.bingo.Bingo
import gg.happy.bingo.module.Card
import gg.happy.bingo.module.TeamManager
import gg.happy.bingo.module.conf.Conf
import gg.happy.bingo.module.game.GameManager
import gg.happy.bingo.module.game.impl.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.function.submit

private abstract class BingoHolder : InventoryHolder
{
    lateinit var backingInventory: Inventory
    override fun getInventory(): Inventory = backingInventory
}

private class AdminHolder : BingoHolder()
private class ItemEditorHolder : BingoHolder()
private class TeamHolder : BingoHolder()
private class BoardHolder : BingoHolder()

object Gui
{
    private val boardSlots = intArrayOf(10, 11, 12, 13, 14, 19, 20, 21, 22, 23, 28, 29, 30, 31, 32, 37, 38, 39, 40, 41, 46, 47, 48, 49, 50)

    private fun item(material: Material, name: String, lore: List<String> = emptyList()): ItemStack =
        ItemStack(material).apply {
            itemMeta = itemMeta!!.apply {
                setDisplayName(colour(name))
                lore?.let { this.lore = it.map(::colour) }
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }
        }

    private fun colour(text: String) = ChatColor.translateAlternateColorCodes('&', text)

    private fun inventory(holder: BingoHolder, size: Int, title: String): Inventory =
        Bukkit.createInventory(holder, size, colour(title)).also { holder.backingInventory = it }

    fun openAdmin(player: Player)
    {
        val holder = AdminHolder()
        val inv = inventory(holder, 27, "&8Bingo Administration")
        val canStart = GameManager.canStart()
        inv.setItem(10, item(if (canStart) Material.LIME_CONCRETE else Material.BARRIER,
            if (canStart) "&aStart match" else "&cMatch is already running", listOf("&7Starts a five-second countdown.")))
        inv.setItem(12, item(Material.CHEST, "&eEdit item pool", listOf("&7Add at least 25 different materials.")))
        inv.setItem(14, item(Material.PLAYER_HEAD, "&bAssign teams", listOf("&7Click online players to cycle their team.")))
        inv.setItem(16, item(Material.SUNFLOWER, "&6Open player board", listOf("&7Preview the current shared board.")))
        inv.setItem(18, item(Material.CLOCK, "&dGame duration: ${formatDuration(Conf.gameDurationSeconds)}", listOf(
            "&7Left click: add 1 minute",
            "&7Right click: remove 1 minute",
            "&7Minimum: 1 minute"
        )))
        player.openInventory(inv)
    }

    fun openItemEditor(player: Player)
    {
        val holder = ItemEditorHolder()
        val inv = inventory(holder, 45, "&8Bingo Item Pool")
        Bingo.conf.getStringList("items").mapNotNull { Material.matchMaterial(it) }.take(45).forEachIndexed { index, material ->
            inv.setItem(index, ItemStack(material))
        }
        player.openInventory(inv)
    }

    fun openTeams(player: Player)
    {
        val holder = TeamHolder()
        val inv = inventory(holder, 54, "&8Bingo Teams")
        val teams = TeamManager.teams
        teams.take(9).forEachIndexed { index, team ->
            inv.setItem(index, item(teamMaterial(team.color), team.displayName, listOf("&7Team ${index + 1}")))
        }
        Bukkit.getOnlinePlayers().take(45).forEachIndexed { index, target ->
            val team = TeamManager.teamOf(target)
            inv.setItem(index + 9, item(Material.PLAYER_HEAD, "&f${target.name}", listOf(
                "&7Current: ${team?.displayName ?: "&7Unassigned"}",
                "&eClick to assign the next team."
            )))
        }
        player.openInventory(inv)
    }

    fun openBoard(player: Player)
    {
        val holder = BoardHolder()
        val inv = inventory(holder, 54, "&8Bingo Board")
        val team = TeamManager.teamOf(player)
        Card.items.forEachIndexed { index, material ->
            if (material == null) return@forEachIndexed
            val completed = TeamManager.isCompleted(team, index)
            inv.setItem(boardSlots[index], item(material, "&f${prettyName(material)}", listOf(
                if (completed) "&aCollected" else "&7Not collected",
                "&8${team?.displayName ?: "No team assigned"}"
            )))
        }
        inv.setItem(4, item(Material.NETHER_STAR, "&e${team?.displayName ?: "No team"}", listOf(
            "&7${if (GameManager.phase === Main) "Match in progress" else "Match is not running"}"
        )))
        player.openInventory(inv)
    }

    private fun prettyName(material: Material) = material.name.lowercase().split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    private fun formatDuration(seconds: Int) = "%d:%02d".format(seconds / 60, seconds % 60)

    private fun teamMaterial(color: ChatColor): Material = when (color) {
        ChatColor.RED, ChatColor.DARK_RED -> Material.RED_WOOL
        ChatColor.BLUE, ChatColor.DARK_BLUE -> Material.BLUE_WOOL
        ChatColor.GREEN, ChatColor.DARK_GREEN -> Material.GREEN_WOOL
        ChatColor.YELLOW, ChatColor.GOLD -> Material.YELLOW_WOOL
        ChatColor.AQUA, ChatColor.DARK_AQUA -> Material.CYAN_WOOL
        ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE -> Material.PURPLE_WOOL
        else -> Material.WHITE_WOOL
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent)
    {
        val holder = event.view.topInventory.holder
        val inTop = event.rawSlot in 0 until event.view.topInventory.size
        when (holder) {
            is AdminHolder -> {
                event.isCancelled = true
                if (!inTop) return
                when (event.rawSlot) {
                    10 -> if (GameManager.canStart()) { event.whoClicked.closeInventory(); GameManager.start() }
                    12 -> openItemEditor(event.whoClicked as Player)
                    14 -> openTeams(event.whoClicked as Player)
                    16 -> openBoard(event.whoClicked as Player)
                    18 -> {
                        val change = if (event.isRightClick) -60 else 60
                        Conf.setGameDurationSeconds(Conf.gameDurationSeconds + change)
                        openAdmin(event.whoClicked as Player)
                    }
                }
            }
            is TeamHolder -> {
                event.isCancelled = true
                if (!inTop || event.rawSlot < 9) return
                val target = Bukkit.getOnlinePlayers().elementAtOrNull(event.rawSlot - 9) ?: return
                val teams = TeamManager.teams
                if (teams.isEmpty()) return
                val current = TeamManager.teamOf(target)
                val next = teams[(teams.indexOf(current) + 1) % teams.size]
                TeamManager.assign(target, next)
                openTeams(event.whoClicked as Player)
            }
            is BoardHolder -> event.isCancelled = true
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent)
    {
        val holder = event.inventory.holder
        if (holder !is ItemEditorHolder) return
        val materials = event.inventory.contents.filterNotNull().map { it.type }.filter { it != Material.AIR }.distinct()
        Bingo.conf.set("items", materials.map { it.name })
        Bingo.conf.saveToFile()
        submit { event.player.sendMessage(colour("&aBingo item pool saved with ${materials.size} materials.")) }
    }
}
