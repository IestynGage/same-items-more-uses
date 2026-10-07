plugins {
	id("architectury-plugin") version "3.5.170"
	id("dev.architectury.loom-no-remap") version "1.17.493" apply false
	id("com.gradleup.shadow") version "9.4.3" apply false
}

architectury {
	minecraft = providers.gradleProperty("minecraft_version").get()
}

allprojects {
	group = providers.gradleProperty("maven_group").get()
	version = providers.gradleProperty("mod_version").get()
}

subprojects {
	apply(plugin = "java")
	apply(plugin = "architectury-plugin")
	apply(plugin = "dev.architectury.loom-no-remap")

	extensions.configure<BasePluginExtension> {
		archivesName = "${rootProject.property("archives_name")}-${project.name}"
	}

	repositories {
		maven {
			name = "NeoForged"
			url = uri("https://maven.neoforged.net/releases/")
		}
	}

	dependencies {
		"minecraft"("com.mojang:minecraft:${rootProject.property("minecraft_version")}")
	}

	tasks.withType<JavaCompile>().configureEach {
		options.encoding = "UTF-8"
		options.release = 25
	}

	extensions.configure<JavaPluginExtension> {
		withSourcesJar()

		sourceCompatibility = JavaVersion.VERSION_25
		targetCompatibility = JavaVersion.VERSION_25
	}

	tasks.named<Jar>("jar") {
		from(rootProject.file("LICENSE")) {
			rename { "${it}_${rootProject.property("archives_name")}" }
		}
	}
}
