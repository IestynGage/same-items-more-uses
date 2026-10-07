plugins {
	id("com.gradleup.shadow")
}

architectury {
	platformSetupLoomIde()
	fabric()
}

val common: Configuration by configurations.creating
// Kept separate from the shadow plugin's own configuration so IDEs don't index it.
val shadowBundle: Configuration by configurations.creating

configurations {
	compileClasspath { extendsFrom(common) }
	runtimeClasspath { extendsFrom(common) }
	named("developmentFabric") { extendsFrom(common) }
}

dependencies {
	implementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
	implementation("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

	common(project(":common")) { isTransitive = false }
	shadowBundle(project(":common", "transformProductionFabric")) { isTransitive = false }
}

fabricApi {
	configureDataGeneration {
		client = false
		// Generated data is shared by both loaders, so it lives in the common module.
		outputDirectory = project(":common").file("src/main/generated")
	}
}

tasks.processResources {
	val version = project.version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.jar {
	archiveClassifier = "raw"
}

tasks.shadowJar {
	dependsOn(tasks.jar)
	configurations = listOf(shadowBundle)
	archiveClassifier = null
	from(zipTree(tasks.jar.flatMap { it.archiveFile }))
	exclude("architectury.common.json")
}

tasks.assemble {
	dependsOn(tasks.shadowJar)
}
