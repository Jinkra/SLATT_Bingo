package gg.happy.bingo

import gg.happy.bingo.module.game.GameManager
import org.bukkit.Bukkit
import org.popcraft.chunky.api.ChunkyAPI
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
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
        Language.default = "en_US"
        console().sendLang("plugin-loading")
    }

    override fun onEnable()
    {
        val startTime = System.currentTimeMillis()
        console().sendLang("plugin-enabled", System.currentTimeMillis() - startTime)
    }

    override fun onActive()
    {
        Bukkit.getServer().servicesManager.load(ChunkyAPI::class.java)?.run {
            startTask("world", "square", 0.0, 0.0, 160.0, 160.0, "concentric")
            onGenerationComplete { console().sendLang("world-generated", it.world) }
        }
        GameManager.phase.onStart()
        console().sendLang("plugin-active")
    }

    override fun onDisable()
    {
        console().sendLang("plugin-disabled")
    }
}