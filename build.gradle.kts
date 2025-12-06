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
                    stagingRepository("target/staging-deploy")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.named("sourcesJar"))
            artifact(tasks.named("javadocJar"))
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
            url = layout.buildDirectory.dir("staging-repo").get().asFile.toURI()
        }
    }
}