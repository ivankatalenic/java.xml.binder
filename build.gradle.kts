plugins {
	id("java-library")
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

getTasksByName("compileJava", true)
	.forEach {
		if (it !is JavaCompile) return@forEach;
		it.options.compilerArgs.add("-Xlint:unchecked")
	}
