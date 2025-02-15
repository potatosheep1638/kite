import com.potatosheep.kite.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("kite.android.hilt")
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.lifecycle.viewModel").get())
                "implementation"(libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
            }
        }
    }
}