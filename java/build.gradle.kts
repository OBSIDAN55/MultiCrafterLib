@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.*

plugins {
    java
    id("io.github.liplum.mgpp")
}
sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("test")
        resources.srcDir("resources")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
mindustry {
    meta = ModMeta(
        name = "java",
        displayName = "Java",
        main = "WithJsonMod",
        minGameVersion = "136",
        version = "0.1",
        java = true,
    )
}
mindustryAssets {
    root at "$projectDir/assets"
}
tasks.jar {
    dependsOn(":lib:jar")
}
dependencies {
    implementation(project(":lib"))
    compileOnly("com.github.Anuken.Arc:arc-core:v146")
    compileOnly("com.github.Anuken.Mindustry:core:v146")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}
