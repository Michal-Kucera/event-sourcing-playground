package com.michal.application.usecase.merchant

import com.michal.adapter.merchant.InMemoryMerchantEventStore
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.merchant.MerchantEvent
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.application.domain.sharedkernel.eventsourcing.EventStore.AggregateNotFound
import com.michal.application.usecase.merchant.OnboardMerchantUseCase.Method
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainInOrder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class MerchantUseCaseTest {

    private val eventStore = InMemoryMerchantEventStore()
    private val changeMerchantNameUseCase = ChangeMerchantNameUseCase(eventStore)
    private val onboardMerchantUseCase = OnboardMerchantUseCase(eventStore, Method.METHOD_CALL)

    @Nested
    inner class Onboard {

        @Test
        fun `onboards a new merchant`() {
            onboardMerchant()

            shouldContainInOrder(MerchantOnboarded(merchantId(), merchantName()))
        }
    }

    @Nested
    inner class ChangeName {

        @Test
        fun `changes name of an existing merchant`() {
            onboardMerchant()

            changeNameOfMerchant()

            shouldContainInOrder(
                MerchantOnboarded(merchantId(), merchantName()),
                MerchantNameChanged(merchantId(), newMerchantName())
            )
        }

        @Test
        fun `fails when trying to change name of a non-existing merchant`() {
            shouldThrowWithMessage<AggregateNotFound>("Merchant ${merchantId()} not found") { changeNameOfMerchant() }

            shouldContainNoEvents()
        }
    }

    private fun onboardMerchant() = onboardMerchantUseCase(OnboardMerchantUseCase.Command(merchantId(), merchantName()))

    private fun changeNameOfMerchant() = changeMerchantNameUseCase(
        ChangeMerchantNameUseCase.Command(merchantId(), newMerchantName())
    )

    private fun shouldContainInOrder(vararg events: MerchantEvent) =
        eventStore.eventsFor(merchantId()) shouldContainInOrder events.toList()

    private fun shouldContainNoEvents() = eventStore.eventsFor(merchantId()).shouldBeEmpty()

    private fun merchantName() = Name.of("Muhammad Sow")

    private fun newMerchantName() = Name.of("Arun Murmu")

    private fun merchantId() = Id.of(UUID.fromString("c9978564-b770-43c2-9017-3e2be3e4d875"))
}
