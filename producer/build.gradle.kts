plugins {
    java
    application
}

dependencies {
    implementation(project(":common"))
}

application {
    mainClass.set("org.itmo.producer.Producer")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.itmo.producer.Producer"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { 
        if (it.isDirectory) it else zipTree(it) 
    })
}
