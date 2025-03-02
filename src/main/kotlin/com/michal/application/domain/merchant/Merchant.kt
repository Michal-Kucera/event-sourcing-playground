package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.command.ChangeMerchantNameCommand
import com.michal.application.domain.merchant.command.OnboardMerchantCommand
import com.michal.application.domain.merchant.event.MerchantEvent
import com.michal.application.domain.merchant.event.MerchantNameChangedEvent
import com.michal.application.domain.merchant.event.MerchantOnboardedEvent
import com.michal.application.domain.sharedkernel.eventsourcing.EventSourcedAggregate
import java.util.UUID

data class Merchant private constructor(
    val id: Id,
    val name: Name,
) : EventSourcedAggregate<Id, MerchantEvent>(id) {
    fun handle(command: ChangeMerchantNameCommand): Merchant {
        if (!name.isSameAs(command.newName)) {
            append(MerchantNameChangedEvent(command.newName))
        }
        return this
    }

    fun on(event: MerchantNameChangedEvent): Merchant = Merchant(id, event.newName)

    companion object {
        fun handle(command: OnboardMerchantCommand): Merchant = Merchant(command.id, command.name).apply {
            append(MerchantOnboardedEvent(command.id, command.name))
        }

        fun onboard(id: Id, name: Name): Merchant = Merchant(id, name).apply {
            append(MerchantOnboardedEvent(id, name))
        }

        fun on(event: MerchantOnboardedEvent): Merchant = Merchant(event.id, event.name)
    }

    data class Id private constructor(
        val id: UUID
    ) {
        override fun toString(): String = id.toString()

        companion object {
            fun of(id: UUID): Id = Id(id)
        }
    }

    data class Name private constructor(
        val name: String
    ) {
        override fun toString(): String = name

        fun isSameAs(other: Name): Boolean = name == other.name

        companion object {
            fun of(name: String): Name {
                require(name.isNotBlank()) { "Name cannot be blank" }
                return Name(name)
            }
        }
    }
}
