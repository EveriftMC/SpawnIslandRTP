plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "net.strokkur"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21.5-R0.1-SNAPSHOT")
    compileOnly("net.strokkur", "betterrtp", "3.6.13") // <-- Yes, this is a manual upload because their repo went down
}

tasks {
    runServer {
        minecraftVersion("1.21.5")
        
        // BetterRTP plugin
        downloadPlugins.hangar("BetterRTP", "3.6.8")
        
        jvmArgs("-Xmx2G", "-Xms2G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to version)
        }
    }
}