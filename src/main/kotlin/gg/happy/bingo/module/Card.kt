package gg.happy.bingo.module

import gg.happy.bingo.module.conf.Conf
import org.bukkit.Material
import taboolib.common.util.random

object Card
{
    const val SIZE = 25

    val items = MutableList<Material?>(SIZE) { null }

    fun generate()
    {
        val toSelect = Conf.items.toMutableList()
        for (i in 0 until SIZE)
            items[i] = toSelect.removeAt(random(toSelect.size))
    }

}