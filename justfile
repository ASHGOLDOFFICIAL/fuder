default:
    @just --list

build:
    ./gradlew assemble

test:
    ./gradlew test

fmt:
    ./gradlew spotlessApply

style:
    ./gradlew spotlessCheck detekt && just editorconfig

editorconfig:
    editorconfig-checker -config .ecrc
