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
        repository.load(command.aggregateId.toString()).execute { merchant ->
            require(merchant.piiData.hasVersion(command.olderVersion)) {
                "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled " +
                        "because the version ${command.olderVersion} of PII data has not been submitted yet"
            }
            require(merchant.piiData.hasVersion(command.newerVersion)) {
                "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled " +
                        "because the version ${command.newerVersion} of PII data has not been submitted yet"
            }
            require(!merchant.piiData.hasPendingReconciliationBefore(command.olderVersion)) {
                "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled " +
                        "because there is a previous version ${merchant.piiData.firstUnreconciledPiiData()?.version} " +
                        "that must be reconciled first"
            }
            require(!merchant.piiData.isReconciled(command.newerVersion)) {
                "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled " +
                        "because these versions are already reconciled"
            }
            applyEvent(
                PiiDataReconciled(
                    command.aggregateId,
                    command.olderVersion,
                    command.newerVersion,
                    command.reconciledName,
                    command.reconciledLegalEntityId,
                    command.reconciledLegalAddress
                )
            )
        }
    }
}
