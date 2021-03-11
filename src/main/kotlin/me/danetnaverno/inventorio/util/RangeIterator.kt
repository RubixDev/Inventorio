package me.danetnaverno.inventorio.util

fun IntProgression.indicesAndOffsets(): IntProgressionIterator
{
    return IntProgressionIterator(this)
}

class IntProgressionIterator(intProgression: IntProgression) : Iterator<IntProgressionIndices>
{
    private val iterator = intProgression.iterator()
    private var absoluteStart = intProgression.first
    override fun hasNext() = iterator.hasNext()
    override fun next() = makeNext()

    private fun makeNext(): IntProgressionIndices
    {
        val offset = iterator.nextInt()
        return IntProgressionIndices(offset, offset - absoluteStart)
    }
}

data class IntProgressionIndices(val absoluteIndex: Int, val relativeIndex: Int)