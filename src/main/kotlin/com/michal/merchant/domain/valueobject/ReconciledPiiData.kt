package com.michal.merchant.domain.valueobject

import com.michal.merchant.domain.valueobject.PiiData.LegalAddress
import com.michal.merchant.domain.valueobject.PiiData.LegalEntityId
import com.michal.merchant.domain.valueobject.PiiData.Name
import com.michal.merchant.domain.valueobject.PiiData.Version

data class ReconciledPiiData private constructor(
    val olderVersion: Version,
    val newerVersion: Version,
    val reconciledName: Name,
    val reconciledLegalEntityId: LegalEntityId,
    val reconciledLegalAddress: LegalAddress
) {
    fun contains(version: Version) = olderVersion == version || newerVersion == version

    companion object {
        fun of(
            olderVersion: Version,
            newerVersion: Version,
            reconciledName: Name,
            reconciledLegalEntityId: LegalEntityId,
            reconciledLegalAddress: LegalAddress
        ): ReconciledPiiData {
            require(olderVersion.canBeReconciledWith(newerVersion)) {
                "Cannot reconcile $olderVersion version with $newerVersion because they are not consequent versions"
            }
            return ReconciledPiiData(
                olderVersion,
                newerVersion,
                reconciledName,
                reconciledLegalEntityId,
                reconciledLegalAddress
            )
        }
    }
}
