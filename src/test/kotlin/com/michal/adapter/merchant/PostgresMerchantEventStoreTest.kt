package com.michal.adapter.merchant

import com.michal.application.domain.merchant.Merchant
import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.Merchant.Name
import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.config.EventSourcingApplication
import com.michal.config.TestcontainersConfiguration
import com.michal.jooq.public.tables.references.EVENT_STORE
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestConstructor.AutowireMode.ALL
import java.util.UUID

@Import(TestcontainersConfiguration::class)
@SpringBootTest(classes = [EventSourcingApplication::class], webEnvironment = RANDOM_PORT)
@TestConstructor(autowireMode = ALL)
class PostgresMerchantEventStoreTest(
    private val postgresMerchantEventStore: PostgresMerchantEventStore,
    private val dslContext: DSLContext
) {

    @BeforeEach
    fun setUp() {
        dslContext.truncate(EVENT_STORE).execute()
    }

    @Test
    fun test() {
        postgresMerchantEventStore.storeEventsFor(
            Merchant.handle(OnboardMerchant(merchantId(), merchantName()))
                .handle(ChangeMerchantName(newMerchantName()))
        )
        postgresMerchantEventStore.getBy(merchantId()) shouldBe Merchant
            .on(MerchantOnboarded(merchantId(), merchantName()))
            .on(MerchantNameChanged(merchantId(), newMerchantName()))
    }

    private fun merchantName() = Name.of("Muhammad Sow")

    private fun newMerchantName() = Name.of("Arun Murmu")

    private fun merchantId() = Id.of(UUID.fromString("c9978564-b770-43c2-9017-3e2be3e4d875"))
}
