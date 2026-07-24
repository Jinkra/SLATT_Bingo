package gg.happy.bingo.module

import gg.happy.bingo.module.conf.Conf
import org.bukkit.Material
import java.util.Collections

object Card
{
    const val SIZE = 25

    val items = MutableList<Material?>(SIZE) { null }

    fun generate()
    {
        val toSelect = Conf.items.distinct().toMutableList()
        require(toSelect.size >= SIZE) { "At least $SIZE different materials are required for a Bingo board." }
        Collections.shuffle(toSelect)
        for (i in 0 until SIZE) items[i] = toSelect[i]
    }

}
