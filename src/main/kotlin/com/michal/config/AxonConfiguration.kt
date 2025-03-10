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
                val retries = letter.diagnostics().getOrDefault("retries", 0) as Int
                println("Attempt: $retries")
                when {
                    retries == 0 -> Decisions.enqueue(cause) { MetaData.with("retries", 0) }
                    retries > 5 -> Decisions.evict()
                    else -> Decisions.requeue(cause) { it.diagnostics().and("retries", retries + 1) }
                }
            }
        }
    }

    @Bean
    fun localValidatorFactoryBean() = LocalValidatorFactoryBean()

    @Bean
    fun beanValidationInterceptor() = BeanValidationInterceptor<CommandMessage<*>>(localValidatorFactoryBean())
}
