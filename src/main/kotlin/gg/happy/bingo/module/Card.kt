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
        require(toSelect.size >= SIZE) { "Bingo 卡牌至少需要 $SIZE 种不同的物品。" }
        Collections.shuffle(toSelect)
        for (i in 0 until SIZE) items[i] = toSelect[i]
    }

}
