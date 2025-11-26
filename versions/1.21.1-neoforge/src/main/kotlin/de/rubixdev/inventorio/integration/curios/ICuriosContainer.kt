package de.rubixdev.inventorio.integration.curios

@Suppress("FunctionName", "PropertyName")
interface ICuriosContainer {
    fun `inventorio$resetSlots`()
    fun `inventorio$setPage`(page: Int)
    fun `inventorio$toggleCosmetics`()
    fun `inventorio$nextPage`()
    fun `inventorio$prevPage`()
    fun `inventorio$checkQuickMove`()
    val `inventorio$currentPage`: Int
    val `inventorio$totalPages`: Int
    val `inventorio$grid`: List<Int>
    val `inventorio$hasCosmetics`: Boolean
    val `inventorio$isViewingCosmetics`: Boolean
    val `inventorio$panelWidth`: Int
}
