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
    
    val jarTask = tasks.jar.get()
    
    onlyIf {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        androidHome != null && file(androidHome).exists()
    }
    
    doLast {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
            ?: throw GradleException("ANDROID_HOME not set. Cannot create DEX JAR.")
        
        // Находим dx инструмент
        val buildToolsDir = file("$androidHome/build-tools")
        val buildTools = if (buildToolsDir.exists() && buildToolsDir.isDirectory) {
            buildToolsDir.listFiles()?.filter { 
                it.isDirectory && it.name.matches(Regex("\\d+\\.\\d+\\.\\d+"))
            }?.maxWithOrNull(compareBy { dir ->
                dir.name.split('.').mapNotNull { it.toIntOrNull() }.let {
                    // Сравниваем версии: major * 1000000 + minor * 1000 + patch
                    if (it.size >= 3) it[0] * 1000000 + it[1] * 1000 + it[2]
                    else if (it.size == 2) it[0] * 1000000 + it[1] * 1000
                    else if (it.size == 1) it[0] * 1000000
                    else 0
                }
            })
        } else {
            null
        }
        
        val dxPath = if (buildTools != null) {
            val dx = file("$buildTools/dx")
            val dxBat = file("$buildTools/dx.bat")
            when {
                dx.exists() -> dx.absolutePath
                dxBat.exists() -> dxBat.absolutePath
                else -> throw GradleException("dx not found in $buildTools")
            }
        } else {
            // Попробуем стандартный путь
            val dx = file("$androidHome/build-tools/34.0.0/dx")
            val dxBat = file("$androidHome/build-tools/34.0.0/dx.bat")
            when {
                dx.exists() -> dx.absolutePath
                dxBat.exists() -> dxBat.absolutePath
                else -> throw GradleException("dx not found. Please ensure Android SDK build-tools are installed.")
            }
        }
        
        val jarFile = jarTask.archiveFile.get().asFile
        val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
        dexDir.mkdirs()
        val dexFile = file("$dexDir/classes.dex")
        
        if (dexFile.exists()) {
            dexFile.delete()
        }
        
        // Запускаем dx для конвертации JAR в DEX
        exec {
            commandLine(dxPath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
        }
    }
}

// Задача для создания JAR из DEX для Android
val dexJarToJar = tasks.register("dexJarToJar", Copy::class.java) {
    group = "build"
    description = "Copy DEX file as JAR for Android"
    
    dependsOn(dexJar)
    
    onlyIf {
        val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
        androidHome != null && file(androidHome).exists()
    }
    
    val dexDir = layout.buildDirectory.dir("tmp/dex")
    val dexFile = file("${dexDir.get().asFile}/classes.dex")
    val outputJar = file("${dexDir.get().asFile}/MultiCrafterLib.jar")
    
    from(dexFile) {
        rename { "MultiCrafterLib.jar" }
    }
    into(dexDir)
}

// Задача deploy для создания финального ZIP файла с DEX версией
tasks.register("deploy", Zip::class.java) {
    group = "build"
    description = "Create deployable mod ZIP for Android"
    
    dependsOn(tasks.jar)
    
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHome != null && file(androidHome).exists()) {
        dependsOn(dexJarToJar)
    }
    
    archiveFileName.set("MultiCrafterLib.zip")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/deploy"))
    
    from("mod.hjson")
    from("icon.png")
    from("assets")
    
    // Добавляем JAR файл (DEX или обычный)
    val androidHomeCheck = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHomeCheck != null && file(androidHomeCheck).exists()) {
        val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
        val dexJarFile = file("$dexDir/MultiCrafterLib.jar")
        if (dexJarFile.exists()) {
            from(dexJarFile)
        } else {
            from(tasks.jar.get().archiveFile) {
                rename { "MultiCrafterLib.jar" }
            }
        }
    } else {
        // Добавляем обычный JAR если DEX не доступен
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
