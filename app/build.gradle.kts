import com.android.build.api.dsl.ApkSigningConfig

plugins {
    id("com.android.application")
    id("com.sidneysimmons.gradle-plugin-external-properties")
}

externalProperties {
    propertiesFileResolver(file("signing.properties"))
}

android {
    namespace = "de.buttercookie.rocketplayerdefuser"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.buttercookie.rocketplayerdefuser"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packaging {
        resources {
            // Not needed as long as we don't use reflection with Kotlin
            excludes.add("**/*.kotlin_builtins")
            excludes.add("**/*.kotlin_module")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    signingConfigs {
        named("debug") {
            if (checkExternalSigningConfig()) {
                applyExternalSigningConfig()
            } else {
                defaultConfig.signingConfig
            }
        }
        create("release") {
            if (checkExternalSigningConfig()) {
                applyExternalSigningConfig()
                android.buildTypes.getByName("release").signingConfig = this
            }
        }
    }
}

dependencies {
    implementation("androidx.annotation:annotation:1.7.1")
    compileOnly("de.robv.android.xposed:api:82")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

fun ApkSigningConfig.checkExternalSigningConfig(): Boolean {
    return props.exists("$name.keyStore") &&
            file(props.get("$name.keyStore")).exists() &&
            props.exists("$name.storePassword") &&
            props.exists("$name.keyAlias") &&
            props.exists("$name.keyPassword")
}

fun ApkSigningConfig.applyExternalSigningConfig() {
    storeFile = file(props.get("$name.keyStore"))
    storePassword = props.get("$name.storePassword")
    keyAlias = props.get("$name.keyAlias")
    keyPassword = props.get("$name.keyPassword")
}
