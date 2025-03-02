package com.michal.application.domain.merchant

import com.michal.application.domain.merchant.Merchant.Id
import com.michal.application.domain.merchant.MerchantCommand.ChangeMerchantName
import com.michal.application.domain.merchant.MerchantCommand.OnboardMerchant
import com.michal.application.domain.merchant.MerchantEvent.MerchantNameChanged
import com.michal.application.domain.merchant.MerchantEvent.MerchantOnboarded
import com.michal.application.domain.sharedkernel.eventsourcing.EventSourcedAggregate
import java.util.UUID

data class Merchant private constructor(
    override val aggregateId: Id,
    val name: Name,
) : EventSourcedAggregate<Id, MerchantEvent>(aggregateId) {

    // command handler impl #1
    // enforce invariants, validate business rules and apply the correct events in case all rules apply
    fun handle(command: ChangeMerchantName): Merchant {
        if (!name.isSameAs(command.newName)) {
            append(MerchantNameChanged(aggregateId, command.newName))
        }
        return this
    }

    // command handler impl #2
    fun handle(command: MerchantCommand): Merchant {
        when (command) {
            is OnboardMerchant -> error("${command::class.java.simpleName} command cannot be handled")
            is ChangeMerchantName -> if (!name.isSameAs(command.newName)) {
                append(MerchantNameChanged(aggregateId, command.newName))
            }
        }
        return this
    }

    // function call instead of command handler
    fun changeName(newName: Name): Merchant {
        if (!name.isSameAs(newName)) {
            append(MerchantNameChanged(aggregateId, newName))
        }
        return this
    }

    // event-sourcing handler impl #1
    // change the state of the aggregate
    fun on(event: MerchantNameChanged): Merchant = Merchant(aggregateId, event.newName)

    // Impl #2
    fun on(event: MerchantEvent): Merchant = when (event) {
        is MerchantOnboarded -> error("${event::class.java.simpleName} event cannot be applied")
        is MerchantNameChanged -> Merchant(aggregateId, event.newName)
    }

    companion object {
        // Command handler
        fun handle(command: OnboardMerchant): Merchant = Merchant(command.id, command.name)
            .apply { append(MerchantOnboarded(command.id, command.name)) }

        // function call instead of command handler
        fun onboard(id: Id, name: Name): Merchant = Merchant(id, name)
            .apply { append(MerchantOnboarded(id, name)) }

        fun on(event: MerchantOnboarded): Merchant = Merchant(event.aggregateId, event.name)
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
