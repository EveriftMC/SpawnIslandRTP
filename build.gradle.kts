plugins {
  id("java")
  id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "net.strokkur"
version = "1.0.1"

repositories {
  mavenCentral()
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
  compileOnly("io.papermc.paper", "paper-api", "1.21.5-R0.1-SNAPSHOT")
  compileOnly("net.strokkur", "betterrtp", "3.6.13") // <-- Yes, this is a manual upload because their repo went down
}

java {
  toolchain.languageVersion = JavaLanguageVersion.of(21)
  targetCompatibility = JavaVersion.VERSION_21
  sourceCompatibility = JavaVersion.VERSION_21
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