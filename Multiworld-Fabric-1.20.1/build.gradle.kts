import net.fabricmc.loom.task.RemapJarTask

plugins {
    id ("fabric-loom") version "1.6-SNAPSHOT"
    id ("maven-publish")
	id ("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

base {
    archivesBaseName = "Multiworld-Fabric"
    version = "1.20.1"
    group = "me.isaiah.mods"
}

repositories {
    flatDir {
        dirs("libs")
    }

}

dependencies {

		annotationProcessor("com.pkware.jabel:jabel-javac-plugin:1.0.1-1")
		compileOnly("com.pkware.jabel:jabel-javac-plugin:1.0.1-1")

    // 1.19.2
    //minecraft("com.mojang:minecraft:1.19.2") 
    //mappings("net.fabricmc:yarn:1.19.2+build.28:v2")
    //modImplementation("net.fabricmc:fabric-loader:0.14.9")
	
	// 1.20
    minecraft("com.mojang:minecraft:1.20.1") 
    mappings("net.fabricmc:yarn:1.20.1+build.10")
    modImplementation("net.fabricmc:fabric-loader:0.15.11")
	modImplementation(project(":Multiworld-Common"))
	include("xyz.nucleoid:fantasy:0.4.11+1.20-rc1")
	modImplementation("xyz.nucleoid:fantasy:0.4.11+1.20-rc1")
	modImplementation("curse.maven:cyber-permissions-407695:4640544")
	modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")
	modImplementation("net.fabricmc.fabric-api:fabric-api-deprecated:0.83.0+1.20")
    modCompileOnly("name:fabric-4.0-SNAPSHOT")
	
	setOf(
		"fabric-api-base",
		//"fabric-command-api-v1",
		"fabric-lifecycle-events-v1",
		"fabric-networking-api-v1"
	).forEach {
		// Add each module as a dependency
		modImplementation(fabricApi.module(it, "0.83.0+1.20"))
	}
}


sourceSets {
    main {
        java {
            srcDir("${rootProject.projectDir}/Multiworld-Common/src/main/java/com")
            //srcDir("${rootProject.projectDir}/Multiworld-Fabric-1.17/src/main/java")

            // Needs fixing for 1.18:
            //exclude("**/MixinWorld.java")
            
            srcDir("src/main/java")
        }
        resources {
            srcDir("${rootProject.projectDir}/Multiworld-Common/src/main/resources")
        }
    }
}

// Jabel
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString() // for the IDE support
    options.release.set(8)

    javaCompiler.set(
        javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    )
}

/*configure([tasks.compileJava]) {
    sourceCompatibility = 16 // for the IDE support
    options.release = 8

    javaCompiler = javaToolchains.compilerFor {
        languageVersion = JavaLanguageVersion.of(16)
    }
}*/

//tasks.getByName("compileJava") {
    //sourceCompatibility = 16
    //options.release = 8
//}


tasks.withType<Jar> { duplicatesStrategy = DuplicatesStrategy.INHERIT }

val remapJar = tasks.getByName<RemapJarTask>("remapJar")

tasks.named("build") { finalizedBy("copyReport2") }

tasks.register<Copy>("copyReport2") {
    from(remapJar)
    into("${project.rootDir}/output")
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name.toLowerCase()
            version = project.version.toString()
            
            pom {
                name.set(project.name.toLowerCase())
                description.set("A concise description of my library")
                url.set("http://www.example.com/")
            }

            artifact(remapJar)
        }
    }

    repositories {
        val mavenUsername: String? by project
        val mavenPassword: String? by project
        mavenPassword?.let {
            maven(url = "https://repo.codemc.io/repository/maven-releases/") {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }
}