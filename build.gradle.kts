import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.9.25"
    id("com.gradleup.shadow") version "9.1.0"
    kotlin("kapt") version "1.9.25"
}

repositories {
    mavenCentral()
}

val awsLambdaCoreVersion = "1.3.0"
val awsLambdaEventsVersion = "3.16.1"
val awsSdkVersion = "2.33.9"
val daggerVersion = "2.51"

dependencies {
    implementation("com.amazonaws:aws-lambda-java-events:$awsLambdaEventsVersion")
    implementation("software.amazon.awssdk:dynamodb:$awsSdkVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    implementation("com.amazonaws:aws-lambda-java-core:$awsLambdaCoreVersion")
    testImplementation(kotlin("test"))
}


tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName = "memory-box"
        archiveClassifier = ""
        archiveVersion = ""
    }

    named<Jar>("jar") {
        enabled = false
    }

    test {
        useJUnitPlatform()
    }
}
kotlin {
    jvmToolchain(21)
}