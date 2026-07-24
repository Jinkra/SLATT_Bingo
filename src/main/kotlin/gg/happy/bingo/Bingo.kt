package gg.happy.bingo

import gg.happy.bingo.module.game.GameManager
import gg.happy.bingo.module.gui.Gui
import gg.happy.bingo.module.listener.GameProgressListener
import gg.happy.bingo.module.scoreboard.BingoScoreboard
import org.bukkit.Bukkit
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.popcraft.chunky.api.ChunkyAPI
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.registerBukkitListener
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile
import taboolib.module.lang.Language
import taboolib.module.lang.sendLang

object Bingo : Plugin()
{
    @Config("config.yml", autoReload = true, migrate = true)
    lateinit var conf: ConfigFile
        private set

    @Config("items.yml", autoReload = true, migrate = true)
    lateinit var itemsConf: ConfigFile
        private set

    override fun onLoad()
    {
        Language.default = "zh_CN"
        console().sendLang("plugin-loading")
    }

    override fun onEnable()
    {
        val startTime = System.currentTimeMillis()
        console().sendLang("plugin-enabled", System.currentTimeMillis() - startTime)
    }

    override fun onActive()
    {
        registerBukkitListener(InventoryClickEvent::class.java) { Gui.onClick(it) }
        registerBukkitListener(InventoryCloseEvent::class.java) { Gui.onClose(it) }
        registerBukkitListener(AsyncPlayerChatEvent::class.java) { Gui.onChat(it) }
        registerBukkitListener(BlockBreakEvent::class.java) { GameProgressListener.onBreak(it) }
        registerBukkitListener(EntityPickupItemEvent::class.java) { GameProgressListener.onPickup(it) }
        registerBukkitListener(CraftItemEvent::class.java) { GameProgressListener.onCraft(it) }
        Bukkit.getServer().servicesManager.load(ChunkyAPI::class.java)?.run {
            startTask("world", "square", 0.0, 0.0, 160.0, 160.0, "concentric")
            onGenerationComplete { console().sendLang("world-generated", it.world) }
        }
        GameManager.phase.onStart()
        submit(period = 20) { BingoScoreboard.updateAll() }
        console().sendLang("plugin-active")
    }

    override fun onDisable()
    {
        console().sendLang("plugin-disabled")
    }
}
