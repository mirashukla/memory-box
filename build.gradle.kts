import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.1.0"
}

repositories {
    mavenCentral()
}

val awsVersion = "1.3.0"

dependencies {
    implementation("com.amazonaws:aws-lambda-java-core:$awsVersion")
    testImplementation(kotlin("test"))
}


tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName = "lambda"
        archiveClassifier = ""
        archiveVersion = ""
    }

    test {
        useJUnitPlatform()
    }
}
kotlin {
    jvmToolchain(21)
}