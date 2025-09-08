import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.1.0"
}

repositories {
    mavenCentral()
}

val awsLambdaCoreVersion = "1.3.0"
val awsLambdaEventsVersion = "3.16.1"

dependencies {
    implementation("com.amazonaws:aws-lambda-java-events:$awsLambdaEventsVersion")

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