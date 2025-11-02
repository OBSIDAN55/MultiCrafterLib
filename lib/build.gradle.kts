buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
        maven { url = uri("https://www.jitpack.io") }
    }
    dependencies {
        classpath("com.github.Anuken:mgpp:1.0.0")
    }
}

plugins {
    `java-library`
    `maven-publish`
}

apply(plugin = "com.github.Anuken.mgpp")

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

// Плагин mgpp автоматически создает задачу dexJar и deploy для Android
// Настройка modCore
extensions.configure<com.github.anuken.mgpp.MindustryExtension> {
    version.set("v147")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
