plugins {
	id("com.gradleup.shadow")
}

architectury {
	platformSetupLoomIde()
	neoForge()
}

val common: Configuration by configurations.creating
// Kept separate from the shadow plugin's own configuration so IDEs don't index it.
val shadowBundle: Configuration by configurations.creating

configurations {
	compileClasspath { extendsFrom(common) }
	runtimeClasspath { extendsFrom(common) }
	named("developmentNeoForge") { extendsFrom(common) }
}

dependencies {
	"neoForge"("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

	common(project(":common")) { isTransitive = false }
	shadowBundle(project(":common", "transformProductionNeoForge")) { isTransitive = false }
}

tasks.processResources {
	val version = project.version
	inputs.property("version", version)

	filesMatching("META-INF/neoforge.mods.toml") {
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
	exclude("fabric.mod.json", "architectury.common.json")
}

tasks.assemble {
	dependsOn(tasks.shadowJar)
}
