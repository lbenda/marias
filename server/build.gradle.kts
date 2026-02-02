plugins {
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)
    application
}

dependencies {
    implementation(project(":engine"))
    implementation(libs.bundles.ktorServer)
    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinxCoroutines)
    implementation(libs.logback)

    testImplementation(libs.junitJupiter)
    testImplementation(libs.kotlinTest)
    testImplementation(libs.ktorServerTestHost)
}

application {
    mainClass = "cz.lbenda.games.marias.server.ApplicationKt"
}
