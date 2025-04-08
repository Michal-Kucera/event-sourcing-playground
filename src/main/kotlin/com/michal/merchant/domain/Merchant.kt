package com.michal.merchant.domain

import com.michal.merchant.domain.command.MerchantCommand.OnboardMerchant
import com.michal.merchant.domain.command.MerchantCommand.ReconcilePiiData
import com.michal.merchant.domain.command.MerchantCommand.SubmitPiiData
import com.michal.merchant.domain.event.MerchantEvent
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
    lateinit var anonymizedData: AnonymizedData
    lateinit var piiData: PiiDataCollection

    @CommandHandler
    @CreationPolicy(ALWAYS)
    fun handle(command: OnboardMerchant) {
        with(command) {
            applyEvent(MerchantOnboarded(aggregateId, platformId, legalAddress, currency, kitchenTypes))
        }
    }

    @CommandHandler
    fun handle(command: SubmitPiiData) {
        with(command) {
            require(anonymizedData.hasSame(legalAddress.country)) {
                "Submitted PII data has different country (${legalAddress.country}) " +
                        "than anonymized data (${anonymizedData.legalAddress.country})"
            }
            applyEvent(PiiDataSubmitted(aggregateId, piiData.nextVersion(), name, legalEntityId, legalAddress))
        }
    }

    @CommandHandler
    fun handle(command: ReconcilePiiData) {
        with(command) {
            require(piiData.hasVersion(olderVersion)) {
                "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                        "the version $olderVersion of PII data has not been submitted yet"
            }
            require(piiData.hasVersion(newerVersion)) {
                "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                        "the version $newerVersion of PII data has not been submitted yet"
            }
            require(!piiData.hasPendingReconciliationBefore(olderVersion)) {
                "PII data in version $olderVersion and $newerVersion cannot be reconciled because " +
                        "there is a previous version ${piiData.firstUnreconciledPiiData()?.version} that must be " +
                        "reconciled first"
            }
            require(!piiData.isReconciled(newerVersion)) {
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

    @EventSourcingHandler
    fun on(event: MerchantEvent) {
        when (event) {
            is MerchantOnboarded -> on(event)
            is PiiDataReconciled -> on(event)
            is PiiDataSubmitted -> on(event)
        }
    }

    fun on(event: MerchantOnboarded) {
        aggregateId = event.aggregateId
        platformId = event.platformId
        currency = event.currency
        anonymizedData = AnonymizedData.with(event.legalAddress, event.kitchenTypes)
        piiData = PiiDataCollection.withNoPiiData()
    }

    fun on(event: PiiDataSubmitted) {
        piiData = piiData.submit(event.version, event.name, event.legalEntityId, event.legalAddress)
    }

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
