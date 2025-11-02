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

// Задача для создания DEX версии JAR для Android (использует d8/dx)
val dexJar = tasks.register("dexJar") {
    group = "build"
    description = "Create Android DEX JAR"
    
    val jarTask = tasks.jar.get()
    
    onlyIf {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        androidHome != null && file(androidHome).exists()
    }
    
    doLast {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
            ?: throw GradleException("ANDROID_HOME not set. Cannot create DEX JAR.")
        
        val jarFile = jarTask.archiveFile.get().asFile
        val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
        dexDir.mkdirs()
        val dexFile = file("$dexDir/classes.dex")
        
        if (dexFile.exists()) {
            dexFile.delete()
        }
        
        // Пробуем найти d8 (новый инструмент) или dx (старый)
        val buildToolsPath = "$androidHome/build-tools"
        val buildToolsDir = file(buildToolsPath)
        
        if (!buildToolsDir.exists()) {
            throw GradleException("Android build-tools not found at $buildToolsPath")
        }
        
        // Ищем любую версию build-tools
        val buildTools = buildToolsDir.listFiles()?.firstOrNull { it.isDirectory }
            ?: throw GradleException("No build-tools version found")
        
        // Пробуем d8 сначала (новые версии Android SDK)
        val d8 = file("$buildTools/d8")
        val d8Bat = file("$buildTools/d8.bat")
        val dx = file("$buildTools/dx")
        val dxBat = file("$buildTools/dx.bat")
        
        when {
            d8.exists() -> {
                exec {
                    commandLine(d8.absolutePath, "--output=$dexDir", jarFile.absolutePath)
                }
            }
            d8Bat.exists() -> {
                exec {
                    commandLine(d8Bat.absolutePath, "--output=$dexDir", jarFile.absolutePath)
                }
            }
            dx.exists() -> {
                exec {
                    commandLine(dx.absolutePath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
                }
            }
            dxBat.exists() -> {
                exec {
                    commandLine(dxBat.absolutePath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
                }
            }
            else -> {
                throw GradleException("Neither d8 nor dx found in $buildTools")
            }
        }
    }
}

// Задача deploy для создания финального ZIP файла
tasks.register("deploy", Zip::class.java) {
    group = "build"
    description = "Create deployable mod ZIP"
    
    dependsOn(tasks.jar)
    
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHome != null && file(androidHome).exists()) {
        dependsOn(dexJar)
    }
    
    archiveFileName.set("MultiCrafterLib.zip")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/deploy"))
    
    from("mod.hjson")
    from("icon.png")
    from("assets")
    
    // Добавляем JAR файл
    val androidHomeCheck = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHomeCheck != null && file(androidHomeCheck).exists()) {
        // Используем DEX версию для Android
        val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
        val dexFile = file("$dexDir/classes.dex")
        if (dexFile.exists()) {
            from(dexFile) {
                rename { "MultiCrafterLib.jar" }
            }
        } else {
            from(tasks.jar.get().archiveFile) {
                rename { "MultiCrafterLib.jar" }
            }
        }
    } else {
        // Используем обычный JAR для десктопа
        from(tasks.jar.get().archiveFile) {
            rename { "MultiCrafterLib.jar" }
        }
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
