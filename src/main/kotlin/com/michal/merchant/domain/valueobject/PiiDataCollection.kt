package com.michal.merchant.domain.valueobject

import com.michal.sharedkernel.valueobject.Email

data class PiiDataCollection(
    private val piiData: List<PiiData>,
    private val reconciledPiiData: List<ReconciledPiiData>
) {
    fun nextVersion(): PiiData.Version {
        if (!hasPiiData()) return PiiData.Version.initial()
        return piiData.last().version.next()
    }

    fun submit(
        version: PiiData.Version,
        name: PiiData.Name,
        email: Email,
        legalEntityId: PiiData.LegalEntityId,
        legalAddress: PiiData.LegalAddress
    ) = copy(piiData = piiData + PiiData.with(version, name, email, legalEntityId, legalAddress))

    fun reconcile(
        olderVersion: PiiData.Version,
        newerVersion: PiiData.Version,
        reconciledName: PiiData.Name,
        reconciledLegalEntityId: PiiData.LegalEntityId,
        reconciledLegalAddress: PiiData.LegalAddress
    ): PiiDataCollection = copy(
        reconciledPiiData = reconciledPiiData + ReconciledPiiData.of(
            olderVersion,
            newerVersion,
            reconciledName,
            reconciledLegalEntityId,
            reconciledLegalAddress,
        )
    )

    private fun hasPiiData() = piiData.isNotEmpty()

    fun hasVersion(version: PiiData.Version) = piiData.any { it.version == version }

    fun isReconciled(version: PiiData.Version) = reconciledPiiData.any { it.contains(version) }

    fun hasPendingReconciliationBefore(version: PiiData.Version): Boolean {
        if (version.isInitialVersion()) return false
        val firstUnreconciledPiiData = firstUnreconciledPiiData() ?: return false
        return firstUnreconciledPiiData.version != version.next()
    }

    fun currentPiiData(): PiiData {
        require(hasPiiData()) { "No PII data has been submitted yet" }
        val latestReconciledPiiData = latestReconciledPiiData()
        return when {
            latestReconciledPiiData != null -> piiData.single { it.version == latestReconciledPiiData.newerVersion }
            else -> piiData.first()
        }
    }

    fun firstUnreconciledPiiData(): PiiData? {
        val latestReconciledPiiData = latestReconciledPiiData() ?: return piiData.firstOrNull()
        return piiData.find { it.version == latestReconciledPiiData.newerVersion.next() }
    }

    private fun latestReconciledPiiData() = reconciledPiiData.maxByOrNull { it.newerVersion }

    companion object {
        fun withNoPiiData() = PiiDataCollection(emptyList(), emptyList())
    }
}
