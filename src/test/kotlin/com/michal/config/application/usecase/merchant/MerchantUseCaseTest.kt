package com.michal.config.application.usecase.merchant

import com.michal.adapter.merchant.InMemoryMerchantEventStore
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.merchant.event.MerchantNameChangedEvent
import com.michal.application.domain.merchant.event.MerchantOnboardedEvent
import com.michal.application.domain.sharedkernel.eventsourcing.EventStore.AggregateNotFound
import com.michal.application.usecase.merchant.ChangeMerchantNameUseCase
import com.michal.application.usecase.merchant.OnboardMerchantUseCase
import com.michal.application.usecase.merchant.OnboardMerchantUseCase.Method
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class MerchantUseCaseTest {

    private val eventStore = InMemoryMerchantEventStore()
    private val changeMerchantNameUseCase = ChangeMerchantNameUseCase(eventStore)
    private val onboardMerchantUseCase = OnboardMerchantUseCase(eventStore, Method.METHOD_CALL)

    @Test
    fun `onboards a new merchant`() {
        onboardMerchant()

        eventStore.eventsFor(merchantId()) shouldBe listOf(MerchantOnboardedEvent(merchantId(), merchantName()))
    }

    @Test
    fun `changes name of an existing merchant`() {
        onboardMerchant()
        changeNameOfMerchant()

        eventStore.eventsFor(merchantId()) shouldBe listOf(
            MerchantOnboardedEvent(merchantId(), merchantName()),
            MerchantNameChangedEvent(newMerchantName())
        )
    }

    @Test
    fun `fails when trying to change name of a non-existing merchant`() {
        shouldThrowWithMessage<AggregateNotFound>("Merchant ${merchantId()} not found") { changeNameOfMerchant() }
    }

    private fun onboardMerchant() = onboardMerchantUseCase(OnboardMerchantUseCase.Command(merchantId(), merchantName()))

    private fun changeNameOfMerchant() = changeMerchantNameUseCase(
        ChangeMerchantNameUseCase.Command(merchantId(), newMerchantName())
    )

    private fun merchantName() = Name.of("Muhammad Sow")

    private fun newMerchantName() = Name.of("Arun Murmu")

    private fun merchantId() = Id.of(UUID.fromString("c9978564-b770-43c2-9017-3e2be3e4d875"))
}
