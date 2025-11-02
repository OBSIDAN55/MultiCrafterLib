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

// Для Android сборки библиотека использует стандартный JAR
// Android версия создается через dexJar задачу, которая выполняется автоматически
// в GitHub Actions, если Android SDK установлен

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
