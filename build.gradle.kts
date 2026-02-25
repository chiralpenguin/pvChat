plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
}

group = "com.purityvanilla"
version = "1.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.purityvanilla", "pvLib", "1.0")
    compileOnly("com.purityvanilla", "pvCore", "1.0")
    compileOnly("com.purityvanilla", "pvFilter", "1.0")
}

tasks.shadowJar {
    archiveClassifier.set("") // This removes the default "-all" classifier
    archiveFileName.set("pvChat.jar")
}

val testServerPluginsPath: String by project
tasks {
    val copyToServer by registering(Copy::class, fun Copy.() {
            dependsOn("shadowJar")
            from(layout.buildDirectory.file("libs"))
            include("pvChat.jar") // Change to "plugin-version.jar" if no shadowing
            into(file(testServerPluginsPath)) // Use the externalized path here
        })
}
