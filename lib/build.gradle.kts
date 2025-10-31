import io.github.liplum.mindustry.genModHjson
import io.github.liplum.mindustry.importMindustry

plugins {
    `java-library`
    `maven-publish`
    id("io.github.liplum.mgpp")
}

sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDirs("resources")
    }
    test {
        java.srcDirs("test")
        resources.srcDirs("resources")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    importMindustry()
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}

mindustryAssets {
    // no icon - icon is in root
}

tasks.genModHjson {
    // no mod.hjson
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
