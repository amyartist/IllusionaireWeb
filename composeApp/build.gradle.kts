import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "IllusionaireWeb.js"
            }
            webpackTask {
                mainOutputFileName = "IllusionaireWeb.js"
            }
            runTask {
                val resourcesPath = project.file("src/webMain/resources")
                if (!resourcesPath.exists()) {
                    project.logger.warn(
                        "Warning: The 'src/webMain/resources' directory does not exist. " +
                                "The dev server may not find index.html."
                    )
                }
                devServerProperty.set(
                    KotlinWebpackConfig.DevServer(
                        static = mutableListOf(resourcesPath.absolutePath)
                    )
                )
            }
        }
        binaries.executable()
    }

    sourceSets {
        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:3.0.0")
            implementation("io.ktor:ktor-client-core:3.0.0")
            implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}