default:
    @just --list

build:
    ./gradlew assemble

test:
    ./gradlew test

style:
    just editorconfig

editorconfig:
    editorconfig-checker -config .ecrc
