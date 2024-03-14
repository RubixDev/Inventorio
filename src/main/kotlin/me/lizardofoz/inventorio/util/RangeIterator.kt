package me.lizardofoz.inventorio.util

/**
 * This adds the ability to iterate over an IntProgression with both relative and absolute indices at the same time
 */
fun IntProgression.withRelativeIndex(): IntProgressionIterator {
    return IntProgressionIterator(this)
}

class IntProgressionIterator(intProgression: IntProgression) : Iterator<IntProgressionIndices> {
    private val iterator = intProgression.iterator()
    private var absoluteStart = intProgression.first
    override fun hasNext() = iterator.hasNext()
    override fun next() = makeNext()

    private fun makeNext(): IntProgressionIndices {
        val offset = iterator.nextInt()
        return IntProgressionIndices(offset, offset - absoluteStart)
    }
}

data class IntProgressionIndices(@JvmField val absoluteIndex: Int, @JvmField val relativeIndex: Int)

infix fun Int.expandBy(addition: Int): IntRange {
    if (addition < 0) return IntRange.EMPTY
    return this until this + addition
}
