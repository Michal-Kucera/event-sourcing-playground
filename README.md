# Event Sourcing playground

This is my space to play with event sourcing, in Kotlin + Axon + Spring Boot.

For testing, use [HTTP scripts](./testing.http) and [SQL scripts](./testing.sql).

# Features

### Commands

```
### Onboard merchant
POST /merchants

### Submit PII data
POST /merchants/{merchant_id}/pii-data

### Reconcile PII data
POST /merchants/{merchant_id}/pii-data/reconcile
```

### Queries

```
### Find all merchants
GET /merchants

### Find merchant by ID
GET /merchants/{merchant_id}

### Find specific version of merchant's PII data
GET /merchants/{merchant_id}/pii-data/{pii_data_version}
```

### Internal

```
### Get log of events
GET /internal/events/{aggregate_name}/{merchant_id}

### Reprocess DLQ
POST /internal/events/dead-letter/{aggregate_name}/{aggregate_id}/retry
```

<details>
<summary>Details</summary>

```mermaid
flowchart TB
    A[fa:fa-terminal OnboardMerchant -> fa:fa-envelope MerchantOnboarded] --> B["fa:fa-terminal SubmitPiiData (v1) -> fa:fa-envelope PiiDataSubmitted (v1)"]
    B --> C["fa:fa-terminal SubmitPiiData (v2) -> fa:fa-envelope PiiDataSubmitted (v2)"]
    C --> D["fa:fa-terminal ReconcilePiiData (v1 - v2) -> fa:fa-envelope PiiDataReconciled (v1 - v2)"]
    D --> E["fa:fa-terminal SubmitPiiData (v3) -> fa:fa-envelope PiiDataSubmitted (v3)"]
    E --> F["fa:fa-terminal ReconcilePiiData (v2 - v3) -> fa:fa-envelope PiiDataReconciled (v2 - v3)"]
```

</details>

1. Aggregate ([Merchant](src/main/kotlin/com/michal/merchant/domain/Merchant.kt))
2. Commands ([MerchantCommand](src/main/kotlin/com/michal/merchant/domain/command/MerchantCommand.kt))
3. Events ([MerchantEvent](src/main/kotlin/com/michal/merchant/domain/event/MerchantEvent.kt))
4. Command handler ([Merchant](src/main/kotlin/com/michal/merchant/domain/Merchant.kt))
5. Event handler ([Merchant](src/main/kotlin/com/michal/merchant/domain/Merchant.kt))
6. Eventsourcing handler ([MerchantReadModelProjector](src/main/kotlin/com/michal/merchant/merchants/internal/MerchantReadModelProjector.kt))
7. Projections
    - Queries ([Query](src/main/kotlin/com/michal/sharedkernel/eventsourcing/Query.kt))
    - Live read
      model ([PiiDataSubmissionReadModelQueryHandler](src/main/kotlin/com/michal/merchant/piidatasubmissions/internal/PiiDataSubmissionReadModelQueryHandler.kt))
    - Database projected read model (async) ([MerchantReadModelProjector](src/main/kotlin/com/michal/merchant/merchants/internal/MerchantReadModelProjector.kt))
8. Dead Letter Queue + retries ([MerchantReadModelProjector](src/main/kotlin/com/michal/merchant/merchants/internal/MerchantReadModelProjector.kt))
9. Testing
    - Unit tests ([MerchantTest](src/test/kotlin/com/michal/merchant/MerchantTest.kt))
    - E2E tests ([MerchantE2eTest](src/test/kotlin/com/michal/merchant/MerchantE2eTest.kt))
10. Vertical slices architecture

# TODO

1. [ ] Closing books - extend `Merchant` with `BusinessScreening` and demonstrate how to keep streams short (one aggregate does not need to be single stream)
   <details>
   <summary>Details</summary>

   ```mermaid
    flowchart TD
     A[MerchantOnboarded] --> B(PiiDataSubmitted)
     B --> C(OnboardingRiskAssessmentStarted)
     C -->|Onboarding Risk Assessment Stream| D[RiskAssessmentStarted]
     D --> E[FundingOffersCalculated]
     E --> F[BusinessKnockoutRulesPassed]
     F --> G[InitialBusinessScreeningPassed]
     G --> H[RiskAssessmentPassed]
     H --> I["OnboardingRiskAssessmentPassed (contains funding offers)"]
     C --> I
     I --> J[FundingOffersSentToCapital]
     J --> K[FundingOfferAccepted]
     K --> L(BeforePayoutBusinessScreeningStarted)
     L -->|Before Payout Business Screening Stream| M[BusinessScreeningStarted]
     M --> N[CreditSafeReportDownloaded]
     N --> O[BusinessKnockoutRulesPassed]
     O --> P[OpsTeamReviewPassed]
     P --> Q[RiskTeamReviewPassed]
     Q --> R[BusinessScreeningPassed]
     R --> S[BeforePayoutBusinessScreeningPassed]
     L --> S
     S --> T[PayoutApprovalSentToCapital]
   ```
   </details>
2. [ ] Retry mechanism on `@CommandHandler` with Spring or Axon's `RetryScheduler`
3. [ ] Replay mechanism, for instance when rebuilding projection ([article](https://www.axoniq.io/blog/axon-framework-4.6.0-replay-context-propagation))
4. [ ] Database projected read model (sync) - when projection fails, command handler fails too
5. [ ] Versioning - Upcasters - add some new change to event and demonstrate how to upcast the old events
6. [ ] Versioning - Breaking change - add some breaking change to event and demonstrate how to handle this
7. [ ] Unique constraint - extend onboarding of merchants and ensure that on each platform external IDs are unique
8. [ ] Show different types of aggregates
   <details>
   <summary>Details</summary>

    1. state object
       ```kotlin
       class Merchant {
         private val state: MerchantState
       }
 
       data class MerchantState(
         val aggregateId: MerchantId,
         val legalAddress: LegalAddress
       )
       ```
    2. everything in the aggregate
       ```kotlin
       class Merchant {
         private val aggregateId: MerchantId,
         private val legalAddress: LegalAddress
       }
       ```
    3. state being created by aggregating list of events
      ```kotlin
       class Merchant {
         private val events: List<MerchantEvent>
         
         private fun currentLegalAddress() = events.filterIsInstance<LegalAddressChanged>().last()
       }
      ```

   </details>
9. [ ] Spring Modulith for context separation + ArchUnit tests for architecture
10. [ ] GDPR and handling of sensitive data - add some field that must be anonymized when asked for
11. [ ] Migration strategies - CRUD to event sourcing
