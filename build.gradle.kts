plugins {
    id("java")
}

subprojects {
    apply(plugin = "java-library")

    group = "com.github.eokasta"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()

        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")

        implementation("org.jetbrains:annotations:23.0.0")

        compileOnly("org.apache.logging.log4j:log4j-api:2.17.2")
        compileOnly("org.apache.logging.log4j:log4j-core:2.17.2")

        implementation("com.zaxxer:HikariCP:5.0.1")

        compileOnly("org.projectlombok:lombok:1.18.22")
        annotationProcessor("org.projectlombok:lombok:1.18.22")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}