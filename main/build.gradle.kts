@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.importMindustry
import io.github.liplum.mindustry.mindustry
import io.github.liplum.mindustry.mindustryAssets

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
    mods {
        worksWith {
            add fromTask ":js:jar"
            // add java "3Snake3/Sapphirium"
            // add local "$buildDir/sapphirium-erekir.zip"
            // add local "$buildDir/units-mod.zip"
        }
    }
    run {
        clearOtherMods
    }
    deploy {
        baseName = "MultiCrafterLib"
    }
}
mindustryAssets {
    root at "$projectDir/assets"
}
tasks.jar {
    dependsOn(":lib:jar")
}

// Make deploy work without Android SDK (copy desktop jar as dexed.jar if not available)
afterEvaluate {
    tasks.named("deploy").configure {
        doFirst {
            val dexedJar = file("$buildDir/tmp/dexJar/dexed.jar")
            if (!dexedJar.exists()) {
                // Copy desktop jar as dexed.jar if Android SDK is not available
                val desktopJar = tasks.named("jar").get().outputs.files.singleFile
                dexedJar.parentFile.mkdirs()
                desktopJar.copyTo(dexedJar, overwrite = true)
                logger.warn("Using desktop jar as dexed.jar (Android SDK not available)")
            }
        }
    }
}

dependencies {
    implementation(project(":lib"))
    compileOnly("com.github.Anuken.Arc:arc-core:v146")
    compileOnly("com.github.Anuken.Mindustry:core:v146")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}
