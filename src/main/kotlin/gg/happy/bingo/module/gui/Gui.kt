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
import org.bukkit.event.player.AsyncPlayerChatEvent
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
private class TeamSelectHolder : BingoHolder()
private class BoardHolder : BingoHolder()

object Gui
{
    private val boardSlots = intArrayOf(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51)
    private val teamCreation = mutableSetOf<java.util.UUID>()

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
        val inv = inventory(holder, 27, "&8Bingo 管理面板")
        val canStart = GameManager.canStart()
        inv.setItem(10, item(if (canStart) Material.LIME_CONCRETE else Material.BARRIER,
            if (canStart) "&a开始比赛" else "&c比赛正在进行", listOf("&7开始五秒倒计时。")))
        inv.setItem(12, item(Material.CHEST, "&e编辑物品池", listOf("&7至少需要 25 种不同物品。")))
        inv.setItem(14, item(Material.PLAYER_HEAD, "&b分配队伍", listOf("&7点击在线玩家切换其队伍。")))
        inv.setItem(16, item(Material.SUNFLOWER, "&6打开玩家卡牌", listOf("&7预览当前共用的 Bingo 卡牌。")))
        inv.setItem(18, item(Material.CLOCK, "&d比赛时长：${formatDuration(Conf.gameDurationSeconds)}", listOf(
            "&7左键：增加 1 分钟",
            "&7右键：减少 1 分钟",
            "&7最短：1 分钟"
        )))
        player.openInventory(inv)
    }

    fun openItemEditor(player: Player)
    {
        val holder = ItemEditorHolder()
        val inv = inventory(holder, 45, "&8Bingo 物品池")
        Bingo.conf.getStringList("items").mapNotNull { Material.matchMaterial(it) }.take(45).forEachIndexed { index, material ->
            inv.setItem(index, ItemStack(material))
        }
        player.openInventory(inv)
    }

    fun openTeams(player: Player)
    {
        val holder = TeamHolder()
        val inv = inventory(holder, 54, "&8Bingo 队伍管理")
        val teams = TeamManager.teams
        inv.setItem(8, item(Material.NAME_TAG, "&a创建队伍", listOf("&7点击后在聊天框输入队伍名称。")))
        teams.take(9).forEachIndexed { index, team ->
            inv.setItem(index, item(teamMaterial(team.color), team.displayName, listOf("&7第 ${index + 1} 队")))
        }
        Bukkit.getOnlinePlayers().take(45).forEachIndexed { index, target ->
            val team = TeamManager.teamOf(target)
            inv.setItem(index + 9, item(Material.PLAYER_HEAD, "&f${target.name}", listOf(
                "&7当前队伍：${team?.displayName ?: "&7未分配"}",
                "&e点击分配到下一支队伍。"
            )))
        }
        player.openInventory(inv)
    }

    fun openTeamSelector(player: Player)
    {
        val holder = TeamSelectHolder()
        val inv = inventory(holder, 27, "&8选择 Bingo 队伍")
        val current = TeamManager.teamOf(player)
        TeamManager.teams.take(9).forEachIndexed { index, team ->
            inv.setItem(index, item(teamMaterial(team.color), team.displayName, listOf(
                if (current?.id == team.id) "&a你当前的队伍" else "&7点击加入这支队伍"
            )))
        }
        inv.setItem(22, item(Material.BARRIER, "&c离开队伍", listOf("&7成为未分配状态。")))
        player.openInventory(inv)
    }

    private fun beginTeamCreation(player: Player)
    {
        teamCreation += player.uniqueId
        player.closeInventory()
        player.sendMessage(colour("&e请在聊天框输入队伍名称（1-16 个字母、数字、下划线或短横线）。"))
    }

    fun openBoard(player: Player)
    {
        val holder = BoardHolder()
        val inv = inventory(holder, 54, "&8Bingo 卡牌")
        val team = TeamManager.teamOf(player)
        val frame = team?.let { teamFrameMaterial(it.color) } ?: Material.GRAY_STAINED_GLASS_PANE
        intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53)
            .forEach { inv.setItem(it, item(frame, "&8${team?.displayName ?: "未分配队伍"}")) }
        Card.items.forEachIndexed { index, material ->
            if (material == null) return@forEachIndexed
            val completed = TeamManager.isCompleted(team, index)
            inv.setItem(boardSlots[index], item(material, "&f${materialName(material)}", listOf(
                if (completed) "&a已收集" else "&7未收集",
                "&8${team?.displayName ?: "未分配队伍"}"
            )))
        }
        player.openInventory(inv)
    }

    private fun materialName(material: Material): String = mapOf(
        Material.IRON_PICKAXE to "铁镐",
        Material.STONE to "石头",
        Material.OAK_LOG to "橡木原木",
        Material.COAL to "煤炭",
        Material.COPPER_ORE to "铜矿石",
        Material.IRON_ORE to "铁矿石",
        Material.GOLD_ORE to "金矿石",
        Material.DIAMOND to "钻石",
        Material.REDSTONE to "红石",
        Material.LAPIS_LAZULI to "青金石",
        Material.OBSIDIAN to "黑曜石",
        Material.SAND to "沙子",
        Material.GRAVEL to "沙砾",
        Material.WHEAT to "小麦",
        Material.PUMPKIN to "南瓜",
        Material.MELON_SLICE to "西瓜片",
        Material.LEATHER to "皮革",
        Material.BONE to "骨头",
        Material.STRING to "线",
        Material.ENDER_PEARL to "末影珍珠",
        Material.BLAZE_ROD to "烈焰棒",
        Material.SLIME_BALL to "黏液球",
        Material.CACTUS to "仙人掌",
        Material.SUGAR_CANE to "甘蔗",
        Material.GLOWSTONE_DUST to "荧石粉"
    )[material] ?: material.name.lowercase().split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

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

    private fun teamFrameMaterial(color: ChatColor): Material = when (color) {
        ChatColor.RED, ChatColor.DARK_RED -> Material.RED_STAINED_GLASS_PANE
        ChatColor.BLUE, ChatColor.DARK_BLUE -> Material.BLUE_STAINED_GLASS_PANE
        ChatColor.GREEN, ChatColor.DARK_GREEN -> Material.LIME_STAINED_GLASS_PANE
        ChatColor.YELLOW, ChatColor.GOLD -> Material.YELLOW_STAINED_GLASS_PANE
        ChatColor.AQUA, ChatColor.DARK_AQUA -> Material.CYAN_STAINED_GLASS_PANE
        ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE -> Material.PURPLE_STAINED_GLASS_PANE
        else -> Material.WHITE_STAINED_GLASS_PANE
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
                    8 -> beginTeamCreation(event.whoClicked as Player)
                }
            }
            is TeamHolder -> {
                event.isCancelled = true
                if (!inTop) return
                if (event.rawSlot == 8) {
                    beginTeamCreation(event.whoClicked as Player)
                    return
                }
                if (event.rawSlot < 9) {
                    TeamManager.teams.getOrNull(event.rawSlot)?.let { TeamManager.cycleColor(it) }
                    openTeams(event.whoClicked as Player)
                    return
                }
                val target = Bukkit.getOnlinePlayers().elementAtOrNull(event.rawSlot - 9) ?: return
                val teams = TeamManager.teams
                if (teams.isEmpty()) return
                val current = TeamManager.teamOf(target)
                val next = teams[(teams.indexOf(current) + 1) % teams.size]
                TeamManager.assign(target, next)
                openTeams(event.whoClicked as Player)
            }
            is TeamSelectHolder -> {
                event.isCancelled = true
                if (!inTop) return
                val player = event.whoClicked as? Player ?: return
                if (event.rawSlot == 22) {
                    TeamManager.assign(player, null)
                    openTeamSelector(player)
                    return
                }
                val team = TeamManager.teams.getOrNull(event.rawSlot) ?: return
                TeamManager.assign(player, team)
                openBoard(player)
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
        submit { event.player.sendMessage(colour("&a物品池已保存，共 ${materials.size} 种物品。")) }
    }

    fun onChat(event: AsyncPlayerChatEvent)
    {
        if (!teamCreation.remove(event.player.uniqueId)) return
        event.isCancelled = true
        val id = event.message.trim()
        if (!Regex("[A-Za-z0-9_-]{1,16}").matches(id)) {
            event.player.sendMessage(colour("&c队伍名称无效。"))
            return
        }
        submit {
            val team = TeamManager.createTeam(id)
            if (team == null) event.player.sendMessage(colour("&c该队伍已经存在。"))
            else event.player.sendMessage(colour("&a已创建 ${team.displayName}&a。"))
            openTeams(event.player)
        }
    }
}
