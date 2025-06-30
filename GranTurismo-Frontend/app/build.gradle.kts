plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("kotlin-kapt") //Agregado
    id("dagger.hilt.android.plugin") //Agregado
    id("androidx.room")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "pe.edu.upeu.granturismojpc"
    compileSdk = 35

    defaultConfig {
        applicationId = "pe.edu.upeu.granturismojpc"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)



    //Navegacion
    implementation("androidx.navigation:navigation-compose:2.8.9")
//Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation ("com.squareup.retrofit2:converter-moshi:2.11.0")
//Formularios
    implementation ("com.github.k0shk0sh:compose-easyforms:0.2.0")
//Agregados Dagger - Hilt
    implementation ("com.google.dagger:hilt-android:2.52") //old 2.47
    kapt ("com.google.dagger:hilt-compiler:2.52") //old 2.47
//Agregado Dagger - Hilt Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") //old 1.0.0
    implementation("com.valentinilk.shimmer:compose-shimmer:1.3.2") //old 1.0.5, 1.3.1
//implementation ("io.coil-kt:coil-compose:2.7.0") //old 2.4.0
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
//Agregado LiveData compose//implementation ("androidx.compose.ui:uitooling")
    implementation ("androidx.compose.foundation:foundation:1.3.0")
    implementation ("androidx.compose.runtime:runtime-livedata")
//App Compact para detectar modo dia noche
    val appcompat_version = "1.7.0" //old 1.6.1
    implementation("androidx.appcompat:appcompat:$appcompat_version")//Agrega do recien


    implementation ("androidx.compose.material:material-icons-extended:1.6.7")

    //Coil Compose, ideal para cargar imágenes desde URLs.
    //implementation("io.coil-kt:coil-compose:2.4.0")

    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("com.mapbox.maps:android:11.11.0")
    implementation ("com.mapbox.extension:maps-compose:11.11.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.accompanist:accompanist-permissions:0.37.2")
    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")

    // If this project uses any Kotlin source, use Kotlin Symbol Processing (KSP)
    // See Add the KSP plugin to your project
    ksp("androidx.room:room-compiler:$room_version")

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")

    //WebSockets

    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.33.0-alpha")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Usa la última versión compatible

    // CameraX para el scanner QR
    val camerax_version = "1.3.4"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-video:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")
    implementation("androidx.camera:camera-extensions:$camerax_version")

    // ML Kit para detección de códigos QR
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // Kotlinx Serialization para DTOs
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Accompanist para permisos
    implementation("com.google.accompanist:accompanist-permissions:0.37.2")

    // ZXing para generación de QR (opcional, si quieres generar QRs en el cliente)
    implementation("com.google.zxing:core:3.5.2")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    // Para manejo de URIs y enlaces externos
    implementation("androidx.browser:browser:1.8.0")

    // ViewModel y LiveData
    val lifecycle_version_explicit = "2.8.7" // Use the version seen in the transitive dependency
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version_explicit")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version_explicit")

    // Kotlinx Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-Xlint:deprecation")
}
