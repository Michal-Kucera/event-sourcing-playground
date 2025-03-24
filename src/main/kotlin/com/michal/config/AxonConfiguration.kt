package com.michal.config

import org.axonframework.commandhandling.CommandBus
import org.axonframework.commandhandling.CommandMessage
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.commandhandling.gateway.DefaultCommandGateway
import org.axonframework.config.ConfigurerModule
import org.axonframework.eventhandling.LoggingErrorHandler
import org.axonframework.messaging.MessageDispatchInterceptor
import org.axonframework.messaging.MessageHandlerInterceptor
import org.axonframework.messaging.MetaData
import org.axonframework.messaging.deadletter.Decisions
import org.axonframework.messaging.deadletter.EnqueuePolicy
import org.axonframework.messaging.interceptors.BeanValidationInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

@Configuration
class AxonConfiguration {

    @Bean
    fun commandGateway(
        commandBus: CommandBus,
        dispatchInterceptors: List<MessageDispatchInterceptor<CommandMessage<*>>>,
        handlerInterceptor: List<MessageHandlerInterceptor<CommandMessage<*>>>
    ): CommandGateway {
        handlerInterceptor.forEach { commandBus.registerHandlerInterceptor(it) }
        return DefaultCommandGateway.builder()
            .commandBus(commandBus)
            .dispatchInterceptors(*dispatchInterceptors.toTypedArray())
            .build()
    }

    @Bean
    fun errorHandlerConfigurerModule() = ConfigurerModule {
        it.eventProcessing().registerDefaultListenerInvocationErrorHandler { LoggingErrorHandler() }
    }

    @Bean
    fun deadLetterEnqueuePolicyConfigurerModule() = ConfigurerModule {
        it.eventProcessing().registerDefaultDeadLetterPolicy { _ ->
            EnqueuePolicy { letter, cause ->
                val retries = letter.diagnostics()["retries"] as? Int
                println("Attempt: ${retries ?: "-"} for ${letter.message().payloadType.name}")
                when {
                    retries == null -> Decisions.requeue(cause) { MetaData.with("retries", 0) }
                    retries <= 3 -> Decisions.requeue(cause) { it.diagnostics().and("retries", retries + 1) }
                    else -> Decisions.evict()
                }
            }
        }
    }

    @Bean
    fun localValidatorFactoryBean() = LocalValidatorFactoryBean()

    @Bean
    fun beanValidationInterceptor() = BeanValidationInterceptor<CommandMessage<*>>(localValidatorFactoryBean())
}
