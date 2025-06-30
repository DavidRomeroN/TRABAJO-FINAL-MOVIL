// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("androidx.room") version "2.7.1" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}
buildscript{
    repositories {
        google()
        mavenCentral()
    }
    dependencies{
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.52") //cambiado 2.52 old 2.47 old 2.45
        // ADD THIS LINE for the Kotlin Serialization Gradle Plugin
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.3")
    }
}
