plugins {
    `java-library`
}

dependencies {
    api("com.rabbitmq:amqp-client:5.20.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.0")
    api("com.fasterxml.jackson.module:jackson-module-parameter-names:2.16.0")
    api("org.apache.opennlp:opennlp-tools:2.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
