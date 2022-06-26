plugins {
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

dependencies {
    implementation(project(":data"))
    implementation(project(":model"))
}

bukkit {
    name = "PlayerSync"
    main = "com.github.eokasta.player_sync.PlayerSyncPlugin"
    version = "${project.version}"
    apiVersion = "1.17"
}