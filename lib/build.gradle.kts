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
    
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    
    onlyIf {
        androidHome != null && java.io.File(androidHome).exists()
    }
    
    doLast {
        if (androidHome == null) {
            throw GradleException("ANDROID_HOME not set. Cannot create DEX JAR.")
        }
        
        // Находим dx инструмент
        val buildToolsDir = java.io.File(androidHome, "build-tools")
        val buildTools = buildToolsDir.listFiles()?.filter { 
            it.isDirectory && it.name.matches(Regex("\\d+\\.\\d+\\.\\d+"))
        }?.maxByOrNull { 
            it.name.split('.').map { it.toIntOrNull() ?: 0 }
        }
        
        val dxPath = if (buildTools != null) {
            val dx = java.io.File(buildTools, "dx")
            val dxBat = java.io.File(buildTools, "dx.bat")
            when {
                dx.exists() -> dx.absolutePath
                dxBat.exists() -> dxBat.absolutePath
                else -> throw GradleException("dx not found in $buildTools")
            }
        } else {
            // Попробуем стандартный путь
            val dx = java.io.File(androidHome, "build-tools/34.0.0/dx")
            val dxBat = java.io.File(androidHome, "build-tools/34.0.0/dx.bat")
            when {
                dx.exists() -> dx.absolutePath
                dxBat.exists() -> dxBat.absolutePath
                else -> throw GradleException("dx not found. Please ensure Android SDK build-tools are installed.")
            }
        }
        
        val jarFile = jarTask.archiveFile.get().asFile
        val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
        dexDir.mkdirs()
        val dexFile = java.io.File(dexDir, "classes.dex")
        
        if (dexFile.exists()) {
            dexFile.delete()
        }
        
        // Запускаем dx для конвертации JAR в DEX
        val isWindows = System.getProperty("os.name").lowercase().contains("win")
        val command = if (isWindows) {
            listOf(dxPath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
        } else {
            listOf(dxPath, "--dex", "--output=${dexFile.absolutePath}", jarFile.absolutePath)
        }
        
        exec {
            commandLine(command)
        }
    }
}

// Задача deploy для создания финального ZIP файла с DEX версией
tasks.register("deploy", Zip::class.java) {
    group = "build"
    description = "Create deployable mod ZIP for Android"
    
    dependsOn(tasks.jar)
    
    val androidHome = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHome != null && java.io.File(androidHome).exists()) {
        dependsOn(dexJar)
    }
    
    archiveFileName.set("MultiCrafterLib.zip")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/deploy"))
    
    from("mod.hjson")
    from("icon.png")
    from("assets")
    
    // Добавляем JAR файл (DEX или обычный)
    val androidHomeValue = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
    if (androidHomeValue != null && java.io.File(androidHomeValue).exists()) {
        // Проверяем наличие DEX файла после выполнения dexJar
        doFirst {
            val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
            val dexFile = java.io.File(dexDir, "classes.dex")
            if (dexFile.exists()) {
                from(dexDir) {
                    include("classes.dex")
                    rename("classes.dex", "MultiCrafterLib.jar")
                }
            }
        }
    }
    
    // Добавляем обычный JAR если DEX не доступен
    from(tasks.jar.get().archiveFile) {
        into("/")
        rename { fileName ->
            val androidHomeCheck = System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT")
            val dexDir = layout.buildDirectory.dir("tmp/dex").get().asFile
            val dexFile = java.io.File(dexDir, "classes.dex")
            if (androidHomeCheck != null && java.io.File(androidHomeCheck).exists() && dexFile.exists()) {
                // Пропускаем обычный JAR, если DEX доступен
                null
            } else {
                "MultiCrafterLib.jar"
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
