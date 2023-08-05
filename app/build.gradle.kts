plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.hyouteki.oasis"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.hyouteki.oasis"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    // UI
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.firebaseui:firebase-ui-firestore:8.0.1")
    implementation("com.google.firebase:firebase-auth:22.1.1")
    implementation("com.google.firebase:firebase-firestore:24.7.0")

    // testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
//    api "org.jetbrains.kotlinx:kotlinx-coroutines-core:$rootProject.coroutines"
//    api "org.jetbrains.kotlinx:kotlinx-coroutines-android:$rootProject.coroutines"

    // coroutines support for firebase operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")

    // lifecyle viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.android.gms:play-services-auth:20.6.0")

    implementation("com.github.bumptech.glide:glide:4.13.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
}