@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.importMindustry
import io.github.liplum.mindustry.mindustryAssets

plugins {
    java
    id("io.github.liplum.mgpp")
}
sourceSets {
    main {
        java.srcDirs("src")
    }
    test {
        java.srcDir("test")
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
mindustry {
    isLib = true
}
mindustryAssets {
    root at "$projectDir/assets"
}
tasks.jar {
    dependsOn(":lib:jar")
    from(projectDir.resolve("assets")) {
        include("scripts/lib.js")
    }
}
dependencies {
    implementation(project(":lib"))
    compileOnly("com.github.Anuken.Arc:arc-core:v146")
    compileOnly("com.github.Anuken.Mindustry:core:v146")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}
