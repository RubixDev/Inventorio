package de.rubixdev.inventorio.integration.curios

import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler

@Suppress("FunctionName", "PropertyName")
interface ICuriosContainer {
    fun `inventorio$resetSlots`()
    fun `inventorio$scrollTo`(pos: Float)
    fun `inventorio$scrollToIndex`(indexIn: Int)
    val `inventorio$hasCosmeticColumn`: Boolean
    val `inventorio$canScroll`: Boolean
    val `inventorio$curiosHandler`: ICuriosItemHandler?
}
