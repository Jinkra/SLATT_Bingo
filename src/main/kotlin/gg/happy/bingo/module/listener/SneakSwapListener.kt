package gg.happy.bingo.module.listener

import gg.happy.bingo.module.conf.Conf
import gg.happy.bingo.util.runKether
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import taboolib.common.platform.event.ProxyListener
import taboolib.common.platform.function.registerBukkitListener
import taboolib.common.platform.function.submit
import taboolib.common.platform.function.unregisterListener

object SneakSwapListener
{
    private val shotCutTemp = mutableSetOf<Player>()

    private lateinit var playerToggleSneakListener: ProxyListener

    private lateinit var playerSwapHandItemsListener: ProxyListener

    private fun onPlayerToggleSneak(event: PlayerToggleSneakEvent)
    {
        if (event.isSneaking)
        {
            shotCutTemp.add(event.player)
            submit(delay = 10) {
                shotCutTemp.remove(event.player)
            }
        }
        else
            shotCutTemp.remove(event.player)
    }

    private fun onPlayerSwapHandItems(event: PlayerSwapHandItemsEvent)
    {
        if (shotCutTemp.contains(event.player))
        {
            shotCutTemp.remove(event.player)
            event.player.runKether(Conf.sneakSwapAction)
            event.isCancelled = true
        }
    }

    fun register()
    {
        playerToggleSneakListener =
            registerBukkitListener(PlayerToggleSneakEvent::class.java) { onPlayerToggleSneak(it) }
        playerSwapHandItemsListener =
            registerBukkitListener(PlayerSwapHandItemsEvent::class.java) { onPlayerSwapHandItems(it) }
    }

    fun unregister()
    {
        unregisterListener(playerToggleSneakListener)
        unregisterListener(playerSwapHandItemsListener)
    }
}
