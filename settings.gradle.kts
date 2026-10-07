pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven {
			name = "Architectury"
			url = uri("https://maven.architectury.dev/")
		}
		maven {
			name = "NeoForged"
			url = uri("https://maven.neoforged.net/releases/")
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

rootProject.name = "same-bucket-more-uses"

include("common")
include("fabric")
include("neoforge")
