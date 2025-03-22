package com.michal.merchant.domain.valueobject

import com.michal.merchant.domain.valueobject.PiiData.Version

data class PiiDataCollection(
    private val piiData: List<PiiData>
) {
    fun nextVersion(): Version {
        if (hasNoPiiData()) return Version.initial()
        return piiData.last().version.next()
    }

    fun add(newPiiData: PiiData) = copy(piiData = piiData + newPiiData)

    private fun hasNoPiiData() = piiData.isEmpty()

    companion object {
        fun withNoPiiData() = PiiDataCollection(emptyList())
    }
}
