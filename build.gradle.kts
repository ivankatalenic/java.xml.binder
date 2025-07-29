import com.github.spotbugs.snom.SpotBugsTask

plugins {
	id("java-library")
	id("com.github.spotbugs-base") version "6.2.2"
}

group = "com.ivankatalenic"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
	useJUnitPlatform()
}
tasks.register<SpotBugsTask>("spotbugsMain") {
	sourceDirs.setFrom(sourceSets.main.get().allSource.sourceDirectories)
	classDirs.setFrom(sourceSets.main.get().output)
	auxClassPaths.setFrom(sourceSets.main.get().compileClasspath)
	description = "Run SpotBugs analysis for the main source set "
}
tasks.check {
	dependsOn("spotbugsMain")
}
tasks.named<JavaCompile>("compileJava") {
	options.compilerArgs.add("-Xlint:unchecked")
}
