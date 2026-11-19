plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Práctica 02-B. KSP corre el generador de Showkase al compilar; Roborazzi agrega las tareas
    // recordRoborazziDebug y verifyRoborazziDebug, que corren las pruebas de captura.
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
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
    // @Preview y @PreviewParameter (2B.2). ui-tooling solo en depuración: es lo que dibuja el panel.
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
    // El catálogo (2B.3): solo en la app de depuración, nunca en la que se publica.
    debugImplementation(libs.showkase)
    kspDebug(libs.showkase.processor)
    // Showkase 1.0.5 dibuja sus íconos con material-icons-core, que las versiones de Compose de esta
    // serie ya no traen solas: sin esta línea el catálogo se cierra al abrirlo (medido el 19-nov-2026).
    debugImplementation(libs.compose.material.icons.core)
    testImplementation(platform(libs.compose.bom))
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.junit)
    // Las pruebas de captura (2B.4).
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    debugImplementation(libs.compose.ui.test.manifest)
}
