plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.shobdodaily.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:core-model"))
    implementation(project(":core:core-database"))

    // Supabase
    api(platform(libs.supabase.bom))
    api(libs.supabase.postgrest)
    api(libs.supabase.auth)
    api(libs.supabase.storage)
    api(libs.supabase.functions)
    implementation(libs.ktor.client.okhttp)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Hilt DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    
    // Testing
    testImplementation(libs.junit)
}
