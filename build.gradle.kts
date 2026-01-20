import java.io.IOException
import java.util.concurrent.TimeoutException

plugins {
	java
	id("com.gradleup.shadow") version "9.3.1"
}

group = "com.technicjelle"
//No version, so the jar filename is clean
//And we put the git hash into the manifest

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.spongepowered:configurate-core:4.2.0")
	implementation("org.spongepowered:configurate-hocon:4.2.0")
	implementation("org.spongepowered:configurate-jackson:4.2.0")
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "com.technicjelle.HOCONReader"
		attributes["git-repo"] = "https://github.com/TechnicJelle/HOCONReader"
		attributes["git-hash"] = gitHash() + if (!gitClean()) "-dirty" else ""
	}
}

tasks.shadowJar {
	//Do not append `-all` after the jar name to keep the jar filename clean
	archiveClassifier = ""
}


// The following three functions are copied from BlueMap's source code, under the MIT Licence
// https://github.com/BlueMap-Minecraft/BlueMap/blob/6c59ab44e506028a99efe1a49eab4531425971ad/buildSrc/src/main/kotlin/versioning.kt

fun Project.gitHash(): String {
	return runCommand("git rev-parse --verify HEAD", "-")
}

fun Project.gitClean(): Boolean {
	if (runCommand("git update-index --refresh", "NOT-CLEAN") == "NOT-CLEAN") return false
	return runCommand("git diff-index HEAD --", "NOT-CLEAN").isEmpty()
}

private fun Project.runCommand(cmd: String, fallback: String? = null): String {
	ProcessBuilder(cmd.split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
		.directory(projectDir)
		.redirectOutput(ProcessBuilder.Redirect.PIPE)
		.redirectError(ProcessBuilder.Redirect.PIPE)
		.start()
		.apply {
			if (!waitFor(10, TimeUnit.SECONDS))
				throw TimeoutException("Failed to execute command: '$cmd'")
		}
		.run {
			val exitCode = waitFor()
			if (exitCode == 0) return inputStream.bufferedReader().readText().trim()

			val error = errorStream.bufferedReader().readText().trim()
			logger.warn("Failed to execute command '$cmd': $error")
			if (fallback != null) return fallback
			throw IOException(error)
		}
}
