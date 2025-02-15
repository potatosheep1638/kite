plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.potatosheep.kite.core.datastore.proto"
}

protobuf {
    protoc {
        artifact = libs.protobuf.protoc.get().toString()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                register("java") {
                    option("lite")
                }
                register("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    api(libs.protobuf.kotlin.lite)
}