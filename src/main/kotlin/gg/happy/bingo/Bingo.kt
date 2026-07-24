package gg.happy.bingo

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info

object Bingo : Plugin() {

    override fun onEnable() {
        info("Successfully running Bingo!")
    }
}