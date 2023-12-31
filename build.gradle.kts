plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "org.psajd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val koinVersion = "3.4.2"
    val exposedVersion = "0.41.1"
    val h2Version = "2.1.210"
    val hikariCpVersion = "5.0.1"
    val flywayVersion = "9.17.0"
    val logbackVersion = "1.4.5"
    val postgresVersion = "42.6.0"

    implementation("dev.inmo:tgbotapi:7.0.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.insert-koin:koin-core:$koinVersion")

    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.9")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}