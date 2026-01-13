plugins {
	java
	id("com.gradleup.shadow") version "9.3.1"
}

group = "com.technicjelle"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.spongepowered:configurate-core:4.2.0")
	implementation("org.spongepowered:configurate-hocon:4.2.0")
	implementation("org.spongepowered:configurate-jackson:4.2.0")
	implementation("com.jayway.jsonpath:json-path:2.10.0")
	implementation("org.slf4j:slf4j-nop:2.0.17") //to shut up jsonpath's desire for a logger
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "com.technicjelle.HOCONReader"
	}
}
