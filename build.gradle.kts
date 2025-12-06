import org.jreleaser.model.Active

plugins {
    id("java")
    `java-library`
    `maven-publish`
    signing
    id("org.jreleaser") version "1.21.0"
}

group = "io.github.shitlime"
version = "0.1"
description = "A Java library written with reference to the Python library playwright_stealth."

val stagingDirectory = layout.buildDirectory.dir("staging-deploy").get()

repositories {
    mavenCentral()
}

dependencies {
    // playwright
    implementation("com.microsoft.playwright:playwright:1.50.0")
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

jreleaser {
    signing {
        active = Active.RELEASE
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(stagingDirectory.asFile.relativeTo(projectDir).path)
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/shitlime/playwright-stealth-java")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/shitlime/playwright-stealth-java/LICENSE.txt")
                    }
                }
                developers {
                    developer {
                        id.set("shitlime")
                        name.set("shitlime")
                        email.set("fwshitlime@outlook.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/shitlime/playwright-stealth-java.git")
                    developerConnection.set("scm:git:ssh://github.com:shitlime/playwright-stealth-java.git")
                    url.set("https://github.com/shitlime/playwright-stealth-java")
                }
            }
        }
    }
    repositories {
        maven {
            name = "stagingRepo"
            url = stagingDirectory.asFile.toURI()
        }
    }
}