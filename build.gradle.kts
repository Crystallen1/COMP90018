// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    alias(libs.plugins.android.application) apply false
    id("com.android.application") version "7.4.2" apply false
    id("com.android.library") version "7.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.1")  // 升级到 8.x 或更高版本
    }
}