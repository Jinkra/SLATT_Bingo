package gg.happy.bingo.module.conf

import gg.happy.bingo.Bingo
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import taboolib.common.platform.function.console
import taboolib.module.lang.sendError

object Conf
{
    val conf = Bingo.conf

    val itemsConf = Bingo.itemsConf

    var placeholderIdentifier = conf.getString("placeholder-identifier", "blockracing")!!

    var spawn = Location(
        Bukkit.getWorld(conf.getString("spawn.world", "world")!!),
        conf.getDouble("spawn.x", 0.0),
        conf.getDouble("spawn.y", 0.0),
        conf.getDouble("spawn.z", 0.0),
        conf.getDouble("spawn.yaw", 0.0).toFloat(),
        conf.getDouble("spawn.pitch", 0.0).toFloat()
    )

    var sneakSwapAction = conf.getStringList("sneak-swap-action")
    var mainCommand = conf.getStringList("main-command")

    var items = mutableListOf<Material>().apply {
        conf.getStringList("items").forEach { id ->
            Material
                .getMaterial(id.replace(' ', '_').uppercase())
                ?.let { add(it) }
                ?: console().sendError("item-load-fail", id)
        }
    }
}