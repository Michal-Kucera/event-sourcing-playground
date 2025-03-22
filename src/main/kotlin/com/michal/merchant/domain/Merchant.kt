package com.michal.merchant.domain

import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.command.MerchantCommand.ReconcilePiiData
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent.MerchantOnboarded
import com.michal.merchant.domain.event.MerchantEvent.PiiDataReconciled
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.valueobject.AnonymizedData
import com.michal.merchant.domain.valueobject.MerchantId
import com.michal.merchant.domain.valueobject.PiiDataCollection
import com.michal.sharedkernel.valueobject.Currency
import com.michal.sharedkernel.valueobject.PlatformId
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.extensions.kotlin.applyEvent
import org.axonframework.modelling.command.AggregateCreationPolicy.ALWAYS
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateRoot
import org.axonframework.modelling.command.CreationPolicy
import org.axonframework.spring.stereotype.Aggregate

@Aggregate
@AggregateRoot
class Merchant {

    @AggregateIdentifier
    private lateinit var aggregateId: MerchantId
    private lateinit var platformId: PlatformId
    private lateinit var currency: Currency
    private lateinit var anonymizedData: AnonymizedData
    private lateinit var piiData: PiiDataCollection

    @CommandHandler
    @CreationPolicy(ALWAYS)
    fun handle(command: OnboardMerchant) {
        with(command) {
            applyEvent(MerchantOnboarded(aggregateId, platformId, legalAddress, currency, kitchenTypes))
        }
    }

    @CommandHandler
    fun handle(command: SubmitPiiData) {
        require(anonymizedData.hasSame(command.legalAddress.country)) {
            "Submitted PII data has different country (${command.legalAddress.country}) " +
                    "than anonymized data (${anonymizedData.legalAddress.country})"
        }
        applyEvent(
            PiiDataSubmitted(
                aggregateId,
                piiData.nextVersion(),
                command.name,
                command.legalEntityId,
                command.legalAddress
            )
        )
    }

    @CommandHandler
    fun handle(command: ReconcilePiiData) {
        require(piiData.hasVersion(command.olderVersion)) {
            "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled because " +
                    "the version ${command.olderVersion} of PII data has not been submitted yet"
        }
        require(piiData.hasVersion(command.newerVersion)) {
            "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled because " +
                    "the version ${command.newerVersion} of PII data has not been submitted yet"
        }
        require(!piiData.hasPendingReconciliationBefore(command.olderVersion)) {
            "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled because " +
                    "there is a previous version ${piiData.firstUnreconciledPiiData()?.version} that must be " +
                    "reconciled first"
        }
        require(!piiData.isReconciled(command.newerVersion)) {
            "PII data in version ${command.olderVersion} and ${command.newerVersion} cannot be reconciled because " +
                    "these versions are already reconciled"
        }
        applyEvent(
            PiiDataReconciled(
                aggregateId,
                command.olderVersion,
                command.newerVersion,
                command.reconciledName,
                command.reconciledLegalEntityId,
                command.reconciledLegalAddress
            )
        )
    }

    @EventSourcingHandler
    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        platformId = event.platformId
        currency = event.currency
        anonymizedData = AnonymizedData.with(event.legalAddress, event.kitchenTypes)
        piiData = PiiDataCollection.withNoPiiData()
    }

    @EventSourcingHandler
    fun on(event: PiiDataSubmitted) {
        piiData = piiData.submit(event.version, event.name, event.legalEntityId, event.legalAddress)
    }

    @EventSourcingHandler
    fun on(event: PiiDataReconciled) {
        piiData = piiData.reconcile(
            event.olderVersion,
            event.newerVersion,
            event.reconciledName,
            event.reconciledLegalEntityId,
            event.reconciledLegalAddress,
        )
    }
}
