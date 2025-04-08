package com.michal.merchant.domain.event.upcaster

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.michal.merchant.domain.event.MerchantEvent.PiiDataSubmitted
import com.michal.merchant.domain.valueobject.PiiData.Email
import org.axonframework.serialization.SimpleSerializedType
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Order(0)
class PiiDataSubmittedUpcasterV1V2(
    private val objectMapper: ObjectMapper
) : SingleEventUpcaster() {
    companion object {
        private val SOURCE_TYPE = SimpleSerializedType(PiiDataSubmitted::class.java.getTypeName(), null)
        private val TARGET_TYPE = SimpleSerializedType(PiiDataSubmitted::class.java.getTypeName(), "2")
    }

    override fun canUpcast(
        intermediateRepresentation: IntermediateEventRepresentation
    ): Boolean = intermediateRepresentation.type == SOURCE_TYPE

    override fun doUpcast(
        intermediateRepresentation: IntermediateEventRepresentation
    ): IntermediateEventRepresentation = intermediateRepresentation.upcastPayload(
        SimpleSerializedType(TARGET_TYPE.name, "2"),
        ObjectNode::class.java,
        {
            val emailNode = objectMapper.createObjectNode()
            emailNode.put("value", Email.unknown().value)
            it.putIfAbsent("email", emailNode)
            it
        },
    )
}
