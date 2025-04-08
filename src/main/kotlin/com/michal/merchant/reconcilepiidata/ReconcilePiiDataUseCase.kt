package com.michal.merchant.reconcilepiidata

import com.michal.merchant.domain.Merchant
import com.michal.merchant.domain.command.MerchantCommand.ReconcilePiiData
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.Repository

//@Component
class ReconcilePiiDataUseCase(
    private val repository: Repository<Merchant>
) {

    @CommandHandler
    fun handle(command: ReconcilePiiData) {
        with(command) {
            repository.load(aggregateId.toString()).execute {
                require(it.piiData.hasVersion(olderVersion)) {
                    "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                            "the version $olderVersion of PII data has not been submitted yet"
                }
                require(it.piiData.hasVersion(newerVersion)) {
                    "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                            "the version $newerVersion of PII data has not been submitted yet"
                }
                require(!it.piiData.hasPendingReconciliationBefore(olderVersion)) {
                    "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                            "there is a previous version ${it.piiData.firstUnreconciledPiiData()?.version} that must " +
                            "be reconciled first"
                }
                require(!it.piiData.isReconciled(newerVersion)) {
                    "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                            "these versions are already reconciled"
                }
                applyEvent(
                    PiiDataReconciled(
                        aggregateId,
                        olderVersion,
                        newerVersion,
                        reconciledName,
                        reconciledLegalEntityId,
                        reconciledLegalAddress
                    )
                )
            }
        }
    }
}
