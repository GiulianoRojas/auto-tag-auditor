plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.autotagauditor"
version = "0.0.1"

application {
    mainClass = "com.autotagauditor.ApplicationKt"
}

dependencies {
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation("org.seleniumhq.selenium:selenium-java:4.39.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.9.2")
    implementation("com.google.firebase:firebase-admin:9.7.0")

    implementation("io.ktor:ktor-server-cors-jvm")
    //Ktor Client (Talk to Gemini API)
    implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
    implementation("io.ktor:ktor-client-core:3.0.0")
    implementation("io.ktor:ktor-client-cio:3.0.0")
}

tasks.withType<JavaExec> {
    standardOutput = System.out
    errorOutput = System.err
}