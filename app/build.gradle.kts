plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.tagai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tagai"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlin {
        jvmToolchain(21)
    }
    buildFeatures {
        compose = true
    }

    lint {
        abortOnError = true
        fatal.add("UnusedResources")
        fatal.add("NewApi")
        checkDependencies = true
        htmlReport = true
        htmlOutput = layout.buildDirectory.file("reports/lint-report.html").get().asFile
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
        implementation(libs.navigation.compose)

        // Icons
        implementation(libs.androidx.compose.material.icons.core)
        implementation(libs.androidx.compose.material.icons.extended)

        // Koin
        implementation(libs.koin.android)
        implementation(libs.koin.androidx.compose)

        // Room
        implementation(libs.room.runtime)
        implementation(libs.room.ktx)
        implementation(libs.androidx.room3.external.antlr)
        ksp(libs.room.compiler)

        // DataStore
        implementation(libs.androidx.datastore.preferences)

        // AI
        implementation(libs.google.generativeai)
        implementation(libs.mediapipe.tasks.genai)

        // Testing
        testImplementation(libs.junit)
        testImplementation(libs.mockk)
        testImplementation(libs.kotlinx.coroutines.test)
        testImplementation(libs.turbine)

        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)

        debugImplementation(libs.androidx.compose.ui.tooling)
        debugImplementation(libs.androidx.compose.ui.test.manifest)

        detektPlugins(libs.detekt.formatting)
        detektPlugins(libs.detekt.compose.rules)
    }
}
detekt {
    toolVersion = libs.versions.detekt.get()
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = false
}

tasks.register<io.gitlab.arturbosch.detekt.Detekt>("detektAll") {
    description = "Custom TagAI task: Forced rule overrides for Compose."

    setSource(files("src/main/java", "src/test/java"))
    include("**/*.kt")
    exclude("**/*.kts")

    config.setFrom(project.rootProject.file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true

    disableDefaultRuleSets = false

    reports {
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.file("reports/detekt-all.html"))
    }
}
