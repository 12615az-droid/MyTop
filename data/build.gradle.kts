plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("androidx.room3")
}

android {
    namespace = "com.bobon.mygametop.data"

    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {

    // Подключим после создания domain-модуля
    // implementation(project(":domain"))

    val roomVersion = "3.0.1"

    implementation("androidx.room3:room3-runtime:$roomVersion")
    ksp("androidx.room3:room3-compiler:$roomVersion")
    val koinVersion = "4.2.2"

    implementation("io.insert-koin:koin-android:$koinVersion")
}