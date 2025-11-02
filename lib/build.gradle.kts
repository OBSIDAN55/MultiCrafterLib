plugins {
    `java-library`
    `maven-publish`
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    // Javadoc disabled - causes issues with compileOnly dependencies
    // withJavadocJar()
}

dependencies {
    compileOnly("com.github.Anuken.Arc:arc-core:v147")
    compileOnly("com.github.Anuken.Mindustry:core:v147")
}

tasks.jar {
    archiveFileName.set("MultiCrafterLib.jar")
    from("mod.hjson")
    from("icon.png")
    from("assets")
}

// Задача deploy для создания готового JAR файла
tasks.register("deploy", Copy::class.java) {
    group = "build"
    description = "Copy deployable JAR to output directory"
    
    dependsOn(tasks.jar)
    
    from(tasks.jar.get().archiveFile) {
        rename { "MultiCrafterLib.jar" }
    }
    into(layout.buildDirectory.dir("tmp/deploy"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
