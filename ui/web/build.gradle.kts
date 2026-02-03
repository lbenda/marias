tasks.register<Exec>("npmInstall") {
    workingDir = projectDir
    commandLine("npm.cmd", "ci")
}

tasks.register<Exec>("dev") {
    workingDir = projectDir
    commandLine("npm.cmd", "run", "dev")
}

tasks.register<Exec>("buildWeb") {
    workingDir = projectDir
    commandLine("npm.cmd", "run", "build")
}
