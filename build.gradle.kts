plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jooq.jooq-codegen-gradle") version "3.20.1"
}

group = "com.michal"
version = "1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

ext["spring-cloud.version"] = "2024.0.0"
ext["jooq.version"] = "3.20.1"
ext["jooq-flyway-plugin.version"] = "1.2.0"
ext["kotest.version"] = "5.9.1"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    jooqCodegen("com.github.sabomichal:jooq-meta-postgres-flyway:${property("jooq-flyway-plugin.version")}")
    jooqCodegen("org.flywaydb:flyway-database-postgresql")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("io.kotest:kotest-assertions-core:${property("kotest.version")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("spring-cloud.version")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xconsistent-data-class-copy-visibility")
    }
    sourceSets {
        main {
            kotlin.srcDirs("src/jooq/kotlin")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}

tasks.named("jooqCodegen") {
    inputs.files(fileTree("src/main/resources/db/migration"))
}

jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "com.github.sabomichal.jooq.PostgresDDLDatabase"
                excludes = "pg_catalog.*|information_schema.*|flyway_schema_history"
                properties {
                    property {
                        key = "locations"
                        value = "src/main/resources/db/migration"
                    }
                    property {
                        key = "dockerImage"
                        value = "postgres:17"
                    }
                }
            }
            generate {
                target {
                    packageName = "com.michal.jooq"
                    directory = "src/jooq/kotlin"
                }
            }
        }
    }
}
