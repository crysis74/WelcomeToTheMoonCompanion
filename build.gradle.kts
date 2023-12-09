buildscript {
    dependencies {
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.5")
    }
}
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21" apply false
}