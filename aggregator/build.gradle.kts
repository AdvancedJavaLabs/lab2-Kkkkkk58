plugins {
    java
    application
}

dependencies {
    implementation(project(":common"))
    implementation("com.google.guava:guava:32.1.2-jre")
}

application {
    mainClass.set("org.itmo.aggregator.Aggregator")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.itmo.aggregator.Aggregator"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { 
        if (it.isDirectory) it else zipTree(it) 
    })
}
