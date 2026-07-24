package gg.happy.bingo.module.conf

import gg.happy.bingo.Bingo

object Conf
{
    val conf = Bingo.conf

    var placeholderIdentifier = conf.getString("placeholder-identifier", "blockracing")!!
}