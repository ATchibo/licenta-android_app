// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false

    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"

    id("com.google.gms.google-services") version "4.4.0" apply false
}