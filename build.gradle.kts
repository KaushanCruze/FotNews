// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
//    id("com.android.application")
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}




dependencies {
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
//    classpath 'com.google.gms:google-services:4.4.2' // or latest version

    // other dependencies
}