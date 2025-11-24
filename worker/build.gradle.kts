plugins {
    java
    application
}

dependencies {
    implementation(project(":common"))
    
    // NLP libraries
    implementation("org.apache.opennlp:opennlp-tools:2.3.1")
    
    // Stanford CoreNLP for sentiment analysis
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models")
    
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("org.itmo.worker.Worker")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.itmo.worker.Worker"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { 
        if (it.isDirectory) it else zipTree(it) 
    })
}

tasks.test {
    useJUnitPlatform()
}
