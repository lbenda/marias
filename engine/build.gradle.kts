plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinxCoroutines)

    testImplementation(libs.junitJupiter)
    testImplementation(libs.kotlinTest)
}
