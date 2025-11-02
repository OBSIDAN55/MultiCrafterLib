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

// Задача deploy для создания финального ZIP файла
tasks.register("deploy", Zip::class.java) {
    group = "build"
    description = "Create deployable mod ZIP"
    
    dependsOn(tasks.jar)
    
    archiveFileName.set("MultiCrafterLib.zip")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/deploy"))
    
    from("mod.hjson")
    from("icon.png")
    from("assets")
    
    // Используем обычный JAR - Mindustry сам обработает его для Android
    from(tasks.jar.get().archiveFile) {
        rename { "MultiCrafterLib.jar" }
    }
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
