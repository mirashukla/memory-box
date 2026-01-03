import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.serialization") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

val awsLambdaCoreVersion = "1.3.0"
val awsLambdaEventsVersion = "3.16.1"
val awsSdkVersion = "2.41.0"
val serializationVersion = "1.9.0"
val javaJwtVersion = "4.5.0"

// ───────────────────────────────────────────────────────────────
// Backend module configuration
// ───────────────────────────────────────────────────────────────

project(":memory-box-backend") {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "com.gradleup.shadow")

    dependencies {
        implementation("com.amazonaws:aws-lambda-java-events:$awsLambdaEventsVersion")
        implementation("software.amazon.awssdk:dynamodb:$awsSdkVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
        implementation("com.amazonaws:aws-lambda-java-core:$awsLambdaCoreVersion")
        implementation("com.auth0:java-jwt:$javaJwtVersion")
        implementation("software.amazon.awssdk:ssm:$awsSdkVersion")

        testImplementation(kotlin("test"))
    }

    kotlin {
        jvmToolchain(21)
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.named<ShadowJar>("shadowJar") {
        archiveBaseName.set("memory-box")
        archiveClassifier.set("")
        archiveVersion.set("")
    }

    tasks.named<Jar>("jar") {
        enabled = false
    }
}
