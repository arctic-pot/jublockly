package io.github.arcticpot.jublockly

import net.minecraft.util.Formatting

class StatParser {
    /** A stat with a value, a max value and an overflow value */
    data class Stat(var value: Int, var max: Int, var overflow: Int)

    enum class HealthType {
        Normal,
        Poison,
        Wither
    }

    var healthType = HealthType.Normal
    var health = Stat(value = 0, max = 100, overflow = 0)
    var mana = Stat(value = 0, max = 100, overflow = 0)
    var defense = 0
    fun parse(actionbar: String): String {
        // Here, we split the actionbar by *more than two* spaces.
        val splitActionBar = actionbar.split(Regex("  +"))
        for (part in splitActionBar) {
            val stripped = Formatting.strip(part)!!
            if (part.contains("❤")) {
                updateHealth(stripped)
                continue
            }
            if (part.contains("❈ Defense")) {
                updateDefense(stripped)
                continue
            }
            if (part.contains("✎")) {
                updateMana(stripped)
                continue
            }
        }
        return ""
    }

    private fun parseSeperatedNum(str: String): Int {
        return str.replace(",", "").toInt()
    }

    private val healthRegex = Regex("(\\d{0,3}(,?\\d{3})*)/(\\d{0,3}(,?\\d{3})*).*")
    private fun updateHealth(content: String) {
        val match = healthRegex.matchEntire(content) ?: return
        val matchGroups = match.groups
        val value = parseSeperatedNum(matchGroups[1]!!.value)
        val max = parseSeperatedNum(matchGroups[3]!!.value)
        if (value <= max) {
            health.value = value
            health.max = max
            health.overflow = 0
        } else {
            health.value = max
            health.max = max
            health.overflow = value - max
        }
    }

    private val defenseRegex = Regex("(\\d{0,3}(,?\\d{3})*)❈.+?")
    private fun updateDefense(strippedContent: String) {
        val match = defenseRegex.matchEntire(strippedContent) ?: return
        defense = parseSeperatedNum(match.groups[1]!!.value)
    }

    private val manaRegex = Regex("(\\d{0,3}(,?\\d{3})*)/(\\d{0,3}(,?\\d{3})*)✎( +?(\\d{0,3}(,?\\d{3})*)ʬ)?")
    private fun updateMana(strippedContent: String) {
        val match = manaRegex.matchEntire(strippedContent) ?: return
        val matchGroups = match.groups
        mana.value = parseSeperatedNum(matchGroups[1]!!.value)
        mana.max = parseSeperatedNum(matchGroups[3]!!.value)
        val overflowMana = matchGroups[6]?.value
        if (overflowMana == null) mana.overflow = 0
        else mana.overflow = parseSeperatedNum(overflowMana)
    }

}

val statParser = StatParser()