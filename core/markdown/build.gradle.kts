plugins {
    alias(libs.plugins.kite.android.library)
    alias(libs.plugins.kite.android.compose)
    alias(libs.plugins.kite.android.hilt)
}

android {
    namespace = "com.potatosheep.kite.core.markdown"
}

dependencies {
    implementation(projects.core.designsystem)

    implementation(libs.markwon.core)
    implementation(libs.markwon.html)
    implementation(libs.markwon.image.coil)
    implementation(libs.markwon.strikethrough)
    implementation(libs.markwon.tables)

    implementation(libs.flexmark.html2md.converter)

    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}