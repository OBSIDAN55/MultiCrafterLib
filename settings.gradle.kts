rootProject.name = "MultiCrafter"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://raw.githubusercontent.com/Zelaux/MindustryRepo/master/repository") }
        maven { url = uri("https://www.jitpack.io") }
    }
}
include("lib")