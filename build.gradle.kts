import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    `java-library`
    `maven-publish`
    application
    kotlin("jvm") version "2.1.20"
    id("org.jetbrains.dokka") version "2.0.0"
}

group = "com.hemanth"
version = "1.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dokka {
    moduleName.set("chess-core")
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
        failOnWarning.set(true)
        outputDirectory.set(rootDir.resolve("build/dokka"))
    }

    dokkaSourceSets {
        configureEach {
            documentedVisibilities.set(
                setOf(
                    VisibilityModifier.Public,
                    VisibilityModifier.Internal,
                    VisibilityModifier.Protected,
                )
            )
        }

        named("main") {
            includes.from("dokka/dokka.md")

        }
    }

    pluginsConfiguration.html {
        customStyleSheets.from("dokka/assets/custom.css")
        customAssets.from("dokka/assets/logo-icon.svg")
        footerMessage.set("chess-core")
    }

}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            artifactId = "chess-core"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/alluhemanth/chess-core")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
    systemProperty("junit.jupiter.execution.parallel.config.strategy", "fixed")
    systemProperty(
        "junit.jupiter.execution.parallel.config.fixed.parallelism",
        Runtime.getRuntime().availableProcessors() / 2
    )
    testLogging {
        events("skipped", "failed")
    }
}

tasks.withType<Jar> {
    archiveBaseName.set("chess-core")
    archiveVersion.set(project.version.toString())
}


dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.12.2")
}

