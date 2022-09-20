import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val http4kVersion = "4.29.0.0"
val junitVersion = "5.9.0"
val kotlinVersion = "1.7.0"
val logbackVersion = "1.4.0"

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
    }
}

plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
}

dependencies {

    implementation(platform("org.http4k:http4k-bom:4.30.3.0"))
    implementation("org.http4k:http4k-client-okhttp")
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.http4k:http4k-server-jetty")
    implementation("org.http4k:http4k-template-handlebars")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

application {
    mainClass.set("io.jterrier.fiprecorder.MainKt")
}

tasks {

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("fip-recorder")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "io.jterrier.fiprecorder.MainKt"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}
