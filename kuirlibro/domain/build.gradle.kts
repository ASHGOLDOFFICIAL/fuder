plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("buildsrc.convention.quality")
}

dependencies {
    api(project(":eventsourcing-core"))
    api(libs.arrowCore)
    testImplementation(kotlin("test"))
}
