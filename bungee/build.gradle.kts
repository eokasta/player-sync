plugins {
    id("net.minecrell.plugin-yml.bungee") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")

    implementation(project(":data"))
    implementation(project(":model"))
}

bungee {
    main = "com.github.eokasta.player_sync.PlayerSyncBungee"
    name = "PlayerSync"
    version = "${project.version}"
}