val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val exposed_version: String by project
val mysql_version: String by project
val koin_ktor: String by project
val hikaricp_version: String by project


tasks.withType<Jar> {
    manifest {
        attributes["Main-Module"] = application.mainModule
    }
}

plugins {
    kotlin("jvm") version "1.9.24"
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.24"
     id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.poulastaa"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    implementation("io.ktor:ktor-server-auth-jwt-jvm")

    // koin
    implementation("io.insert-koin:koin-ktor:$koin_ktor")

    // db
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    //connection pooling
    implementation("com.zaxxer:HikariCP:$hikaricp_version")
    implementation("mysql:mysql-connector-java:$mysql_version")

    // send mail
    implementation("com.sun.mail:jakarta.mail:2.0.1")

    // Google Client API Library
    implementation("com.google.api-client:google-api-client:2.2.0")

    // pdf
    implementation("org.apache.pdfbox:pdfbox:2.0.26")

    // pdf
    implementation("com.itextpdf:itext7-core:8.0.5")
    implementation("com.itextpdf:html2pdf:5.0.5")
}
