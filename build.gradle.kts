plugins {
    `maven-publish`
}
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
        maven { url = uri("https://www.jitpack.io") }
    }
}
allprojects {
    group = "net.liplum"
    version = "2.0.0"
    buildscript {
        repositories {
            maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
            maven { url = uri("https://www.jitpack.io") }
        }
    }
    repositories {
        mavenCentral()
        maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
        maven { url = uri("https://www.jitpack.io") }
    }

    //force arc version
    configurations.all {
        resolutionStrategy {
            eachDependency {
                if(this.requested.group == "com.github.Anuken.Arc") {
                    this.useVersion("v147")
                }
            }
        }
    }

    // Tests disabled - library doesn't have test dependencies
}
