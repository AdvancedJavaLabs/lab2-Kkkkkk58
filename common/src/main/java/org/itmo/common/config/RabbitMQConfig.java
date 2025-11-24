package org.itmo.common.config;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMQConfig {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    
    public static final String TASKS_QUEUE = "text_tasks";
    public static final String RESULTS_QUEUE = "text_results";

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 5672;
    private static final String DEFAULT_USERNAME = "guest";
    private static final String DEFAULT_PASSWORD = "guest";
    
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public RabbitMQConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public Connection createConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        
        logger.info("Connecting to RabbitMQ at {}:{}", host, port);
        Connection connection = factory.newConnection();
        logger.info("Connected to RabbitMQ successfully");
        
        return connection;
    }

    public static RabbitMQConfig fromEnvironment() {
        var environmentProperties = System.getenv();
        String host = environmentProperties.getOrDefault("RABBITMQ_HOST", DEFAULT_HOST);
        int port = Integer.parseInt(environmentProperties.getOrDefault("RABBITMQ_PORT", String.valueOf(DEFAULT_PORT)));
        String username = environmentProperties.getOrDefault("RABBITMQ_USERNAME", DEFAULT_USERNAME);
        String password = environmentProperties.getOrDefault("RABBITMQ_PASSWORD", DEFAULT_PASSWORD);
        
        return new RabbitMQConfig(host, port, username, password);
    }
}
