plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.terista.space.native"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35

        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}
