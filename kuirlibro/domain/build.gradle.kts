plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.quality")
}

dependencies {
    api(project(":commons:repository"))
    api(libs.arrowCore)
    testImplementation(kotlin("test"))
}
