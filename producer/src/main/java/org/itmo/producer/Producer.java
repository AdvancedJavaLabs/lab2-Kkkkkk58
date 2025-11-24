package org.itmo.producer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.itmo.common.config.RabbitMQConfig;
import org.itmo.common.protocol.TaskMessage;
import org.itmo.common.utils.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    
    private final RabbitMQConfig config;
    private final String inputDir;
    private final int sentencesPerSection;
    
    public Producer(RabbitMQConfig config, String inputDir, int sentencesPerSection) {
        this.config = config;
        this.inputDir = inputDir;
        this.sentencesPerSection = sentencesPerSection;
    }
    
    public void run() throws IOException, TimeoutException {
        logger.info("Producer starting...");
        List<String> sections = readAndSplitFiles();

        if (sections.isEmpty()) {
            logger.warn("No text to process. Exit...");
            return;
        }
        
        try (Connection connection = config.createConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(RabbitMQConfig.TASKS_QUEUE, true, false, false, null);
            
            long startTime = System.nanoTime();
            String batchId = UUID.randomUUID().toString();
            
            for (int i = 0; i < sections.size(); i++) {
                String taskId = batchId + "-" + i;
                TaskMessage task = new TaskMessage(taskId, i, sections.size(), sections.get(i));
                
                byte[] message = MessageSerializer.serialize(task);
                channel.basicPublish("", RabbitMQConfig.TASKS_QUEUE, null, message);
                
                if ((i + 1) % 10 == 0) {
                    logger.info("Sent {} / {} tasks", i + 1, sections.size());
                }
            }
            
            long elapsed = (System.nanoTime() - startTime) / 1_000_000;
            logger.info("All {} tasks sent successfully in {} ms", sections.size(), elapsed);
        }
        
        logger.info("Producer finished");
    }
    
    private List<String> readAndSplitFiles() throws IOException {
        List<String> allSections = new ArrayList<>();
        File dir = new File(inputDir);
        
        if (!dir.exists() || !dir.isDirectory()) {
            logger.error("Input directory does not exist: {}", inputDir);
            return allSections;
        }
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            logger.warn("No files found in directory: {}", inputDir);
            return allSections;
        }
        
        logger.info("Found {} text files", files.length);
        
        for (File file : files) {
            logger.info("Reading file: {}", file.getName());
            String content = Files.readString(file.toPath());
            
            List<String> sections = TextSplitter.splitBySentences(content, sentencesPerSection);
            logger.info("  - Created {} sections from {}", sections.size(), file.getName());
            
            allSections.addAll(sections);
        }
        
        return allSections;
    }
    
    public static void main(String[] args) {
        String inputDir = "data";
        int sentencesPerSection = 10;
        
        for (String arg : args) {
            if (arg.startsWith("--input-dir=")) {
                inputDir = arg.substring("--input-dir=".length());
            } else if (arg.startsWith("--section-size=")) {
                sentencesPerSection = Integer.parseInt(arg.substring("--section-size=".length()));
            }
        }
        
        try {
            RabbitMQConfig config = RabbitMQConfig.fromEnvironment();
            Producer producer = new Producer(config, inputDir, sentencesPerSection);
            producer.run();
        } catch (Exception e) {
            logger.error("Producer failed with error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}

