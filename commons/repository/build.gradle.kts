plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.quality")
    `java-test-fixtures`
}

dependencies {
    api(libs.arrowCore)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinxCoroutinesCore)
}
