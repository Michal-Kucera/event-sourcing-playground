package com.michal.merchant.domain.valueobject

data class PiiDataCollection(
    private val piiData: List<PiiData>
) {
    fun hasNoPiiData() = piiData.isEmpty()

    fun add(newPiiData: PiiData) = copy(piiData = piiData + newPiiData)

    companion object {
        fun withNoPiiData() = PiiDataCollection(emptyList())
    }
}
