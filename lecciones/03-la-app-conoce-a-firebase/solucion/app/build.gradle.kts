plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}
android {
    namespace = "mx.habil.ejemplo.tienda"
    compileSdk = 37
    defaultConfig { applicationId = "mx.habil.ejemplo.tienda"; minSdk = 26; targetSdk = 37 }
    buildFeatures { compose = true }
    testOptions { unitTests.isIncludeAndroidResources = true }
}
kotlin { jvmToolchain(25) }
dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.junit)
    debugImplementation(libs.compose.ui.test.manifest)
}
