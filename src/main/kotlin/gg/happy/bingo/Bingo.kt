package gg.happy.bingo

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile

object Bingo : Plugin() {

    @Config("config.yml", autoReload = true, migrate = true)
    lateinit var conf: ConfigFile
        private set

    override fun onEnable() {
        info("Successfully running Bingo!")
    }
}