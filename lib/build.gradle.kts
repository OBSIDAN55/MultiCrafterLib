import io.github.liplum.mindustry.genModHjson
import io.github.liplum.mindustry.importMindustry

plugins {
    java
    `maven-publish`
    id("io.github.liplum.mgpp")
}
sourceSets {
    main {
        java.srcDir("src")
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
dependencies {
    compileOnly "com.github.Anuken.Arc:arc-core:$mindustryVersion"
    compileOnly "com.github.Anuken.Mindustry:core:$mindustryVersion"

    annotationProcessor "com.github.Anuken:jabel:$jabelVersion"
}

configurations.all{
    resolutionStrategy.eachDependency { details ->
        if(details.requested.group == 'com.github.Anuken.Arc'){
            details.useVersion "$mindustryVersion"
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}
mindustryAssets{
    // no icon
    icon at "$projectDir/icon.png"
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