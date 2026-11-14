
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    // Android
    android {
        namespace = "com.example.compaishared"
        compileSdk = 37
        minSdk = 26
    }

    // iOS
    val iosTargets = listOf(
        iosArm64(),
        iosSimulatorArm64()
    )

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "CompAIShared"
            isStatic = true
        }
    }

    // JVM target for running shared tests on Windows
    jvm()

    // Shared test dependencies
    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
