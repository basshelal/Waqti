import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("io.objectbox")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "uk.whitecrescent.waqti"
        minSdkVersion(24)
        targetSdkVersion(29)
        multiDexEnabled = true
        versionCode = 3
        versionName = "1.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val kotlinVersion = KotlinCompilerVersion.VERSION

    fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar")))
    /*================================= Kotlin =================================*/
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx2:1.3.2")

    /*================================= AndroidX ================================*/
    implementation("androidx.core:core-ktx:1.2.0-beta02")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta3")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.1.0")
    implementation("androidx.paging:paging-runtime-ktx:2.1.0")
    implementation("androidx.paging:paging-rxjava2-ktx:2.1.0")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")

    /*================================= Testing =================================*/
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")

    /*================================= External =================================*/
    implementation("io.objectbox:objectbox-kotlin:2.4.0")

    implementation("io.reactivex.rxjava2:rxjava:2.2.14")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")
    testImplementation("org.threeten:threetenbp:1.4.0")

    implementation("com.google.code.gson:gson:2.8.6") // replace GSON with moshi

    implementation("com.squareup.moshi:moshi:1.9.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.9.1")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.1")

    implementation("org.jetbrains.anko:anko:0.10.8")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.6.3")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:1.6.3")

    implementation("com.google.android.material:material:1.2.0-alpha01")

    implementation("com.mikhaellopez:circularimageview:3.2.0")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("com.github.medyo:android-about-page:1.2.5")

    implementation("com.afollestad.material-dialogs:core:3.0.1")
    implementation("com.afollestad.material-dialogs:input:3.0.1")
    implementation("com.afollestad.material-dialogs:files:3.0.1")
    implementation("com.afollestad.material-dialogs:color:3.0.1")
    implementation("com.afollestad.material-dialogs:datetime:3.0.1")
    implementation("com.afollestad.material-dialogs:bottomsheets:3.0.1")

    implementation("com.andrognito.flashbar:flashbar:1.0.3")

    implementation("com.github.takusemba:spotlight:1.8.0")

    implementation("com.infideap.drawerbehavior:drawer-behavior:0.2.2")

    implementation("com.github.arcadefire:nice-spinner:1.4.3")

    implementation("com.github.warkiz.widget:indicatorseekbar:2.1.2")

    implementation("com.github.basshelal:UnsplashPhotoPicker:master-SNAPSHOT")

    implementation("com.facebook.shimmer:shimmer:0.5.0")

    implementation("me.everything:overscroll-decor-android:1.0.4")
}
