@file:Suppress("Unused")

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("duckee.android.library")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            dependencies {
                "implementation"(libs.findLibrary("kotlin.coroutines").get())
                "implementation"(libs.findLibrary("navigation.materialMotion.compose").get())
            }
        }
    }
}
