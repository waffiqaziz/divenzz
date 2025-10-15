import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ksp)
}

android {
  namespace = "com.waffiq.divenzz"
  compileSdk = libs.versions.compileSdk.get().toInt()

  defaultConfig {
    applicationId = "com.waffiq.divenzz"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = libs.versions.versionCode.get().toInt()
    versionName = libs.versions.versionName.get()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  buildFeatures {
    viewBinding = true
  }
  kotlin {
    compilerOptions {
      jvmTarget = JvmTarget.JVM_21

      // https://youtrack.jetbrains.com/issue/KT-73255/Change-defaulting-rule-for-annotations#focus=Comments-27-11972724.0-0
      freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
  }
}

dependencies {

  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.lifecycle.livedata.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.navigation.fragment.ktx)
  implementation(libs.androidx.navigation.ui.ktx)
  implementation(libs.androidx.swiperefreshlayout)
  implementation(libs.material)

  implementation(libs.markwon.core)
  implementation(libs.markwon.html)
  implementation(libs.markwon.image.glide)

  implementation(libs.bundles.retrofit)
  ksp(libs.moshi.kotlin.codegen)

  implementation(libs.glide)
  ksp(libs.glide.compiler)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}