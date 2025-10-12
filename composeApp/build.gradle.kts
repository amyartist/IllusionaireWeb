import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {

    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "IllusionaireWeb.js"
            }
            // Use the 'webpack' variant for more detailed configuration
            webpackTask {
                mainOutputFileName = "IllusionaireWeb.js"
            }
            runTask {
                // Define the path to your resources folder
                val resourcesPath = project.file("src/webMain/resources")

                // Simple check to see if the directory exists, to help with debugging.
                if (!resourcesPath.exists()) {
                    // This will print a helpful error during the Gradle sync/build if the path is wrong.
                    project.logger.warn("Warning: The 'jsMain/resources' directory does not exist. The dev server may not find index.html.")
                }

                // Configure the development server using the recommended property
                devServerProperty.set(
                    KotlinWebpackConfig.DevServer(
                        // The 'static' property takes a list of directories to serve files from.
                        // The server will automatically look for 'index.html' within these directories.
                        static = mutableListOf(resourcesPath.absolutePath)
                    )
                )
            }
        binaries.executable()
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}


