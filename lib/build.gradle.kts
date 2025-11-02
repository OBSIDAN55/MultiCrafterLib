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

// Задача для создания DEX версии JAR для Android
val dexJar = tasks.register("dexJar") {
    group = "build"
    description = "Create Android DEX JAR"
    
    dependsOn(tasks.jar)
    
    onlyIf {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        androidHome != null && file(androidHome).exists()
    }
    
    doLast {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
            ?: return@doLast
        
        val jarFile = tasks.jar.get().archiveFile.get().asFile
        val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
        dexDir.mkdirs()
        
        // Ищем любую версию build-tools
        val buildToolsDir = file("$androidHome/build-tools")
        val buildTools = buildToolsDir.listFiles()?.firstOrNull { it.isDirectory }
            ?: return@doLast
        
        // Пробуем d8 (новый) или dx (старый)
        val d8 = file("$buildTools/d8")
        val d8Bat = file("$buildTools/d8.bat")
        val dx = file("$buildTools/dx")
        val dxBat = file("$buildTools/dx.bat")
        
        val dexFile = file("$dexDir/classes.dex")
        val success = when {
            d8.exists() -> {
                try {
                    // d8 создает classes.dex в указанной директории
                    exec {
                        workingDir = dexDir
                        commandLine(d8.absolutePath, "--output", dexDir.absolutePath, jarFile.absolutePath)
                    }
                    true
                } catch (e: Exception) {
                    logger.warn("d8 failed: ${e.message}")
                    false
                }
            }
            d8Bat.exists() -> {
                try {
                    exec {
                        workingDir = dexDir
                        commandLine(d8Bat.absolutePath, "--output", dexDir.absolutePath, jarFile.absolutePath)
                    }
                    true
                } catch (e: Exception) {
                    logger.warn("d8 failed: ${e.message}")
                    false
                }
            }
            dx.exists() -> {
                try {
                    exec {
                        commandLine(dx.absolutePath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
                    }
                    true
                } catch (e: Exception) {
                    logger.warn("dx failed: ${e.message}")
                    false
                }
            }
            dxBat.exists() -> {
                try {
                    exec {
                        commandLine(dxBat.absolutePath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
                    }
                    true
                } catch (e: Exception) {
                    logger.warn("dx failed: ${e.message}")
                    false
                }
            }
            else -> {
                logger.warn("Neither d8 nor dx found in $buildTools")
                false
            }
        }
        
        if (!success) {
            logger.warn("DEX conversion failed, will use regular JAR")
        }
    }
}

// Задача deploy для создания готового JAR файла с DEX для Android
tasks.register("deploy") {
    group = "build"
    description = "Copy deployable JAR to output directory (DEX for Android)"
    
    dependsOn(tasks.jar)
    
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHome != null && file(androidHome).exists()) {
        dependsOn(dexJar)
    }
    
    doLast {
        val outputDir = layout.buildDirectory.dir("tmp/deploy").get().asFile
        outputDir.mkdirs()
        val outputJar = file("$outputDir/MultiCrafterLib.jar")
        
        val androidHomeCheck = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        if (androidHomeCheck != null && file(androidHomeCheck).exists()) {
            // Используем DEX версию если доступна
            val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
            val dexFile = file("$dexDir/classes.dex")
            
            if (dexFile.exists()) {
                // Копируем DEX как JAR для Android
                dexFile.copyTo(outputJar, overwrite = true)
                logger.lifecycle("Using DEX version for Android: ${outputJar.absolutePath}")
                return@doLast
            }
        }
        
        // Используем обычный JAR для десктопа
        val jarFile = tasks.jar.get().archiveFile.get().asFile
        jarFile.copyTo(outputJar, overwrite = true)
        logger.lifecycle("Using regular JAR: ${outputJar.absolutePath}")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
