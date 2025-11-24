package org.itmo.worker;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.itmo.common.config.RabbitMQConfig;
import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;
import org.itmo.common.protocol.ResultMessage;
import org.itmo.common.protocol.TaskMessage;
import org.itmo.common.utils.MessageSerializer;
import org.itmo.worker.processors.NameReplacementProcessor;
import org.itmo.worker.processors.SentenceSorterProcessor;
import org.itmo.worker.processors.SentimentProcessor;
import org.itmo.worker.processors.WordCountProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Worker {
    private static final Logger logger = LoggerFactory.getLogger(Worker.class);
    
    private final RabbitMQConfig config;
    private final String workerId;
    private final ProcessingPipeline handlers;
    private final AtomicInteger processedCount;
    
    public Worker(RabbitMQConfig config, String workerId) {
        this.config = config;
        this.workerId = workerId;
        this.processedCount = new AtomicInteger(0);
        
        this.handlers = new ProcessingPipeline()
                .addProcessor(new WordCountProcessor())
                .addProcessor(new SentimentProcessor())
                .addProcessor(new NameReplacementProcessor())
                .addProcessor(new SentenceSorterProcessor());
    }
    
    public void run() throws IOException, TimeoutException {
        logger.info("Worker {} starting...", workerId);
        
        Connection connection = config.createConnection();
        Channel channel = connection.createChannel();
        
        channel.queueDeclare(RabbitMQConfig.TASKS_QUEUE, true, false, false, null);
        channel.queueDeclare(RabbitMQConfig.RESULTS_QUEUE, true, false, false, null);
        
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                TaskMessage task = MessageSerializer.deserialize(
                    delivery.getBody(), TaskMessage.class);
                
                logger.info("Worker {} processing section {} / {}", workerId, task.sectionId() + 1, task.totalSections());
                
                TextSection section = new TextSection(
                    task.sectionId(),
                    task.text(),
                    task.totalSections()
                );
                
                ProcessingResult result = handlers.process(section, workerId);
                
                ResultMessage resultMsg = new ResultMessage(task.taskId(), result);
                byte[] resultBytes = MessageSerializer.serialize(resultMsg);
                channel.basicPublish("", RabbitMQConfig.RESULTS_QUEUE, null, resultBytes);
                
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                
                int count = processedCount.incrementAndGet();
                logger.info("Worker {} completed section {} in {} ms (total: {})",
                        workerId, task.sectionId() + 1, result.getProcessingTimeMs(), count);
            } catch (Exception e) {
                logger.error("Worker {} error processing message", workerId, e);
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
            }
        };
        
        channel.basicConsume(RabbitMQConfig.TASKS_QUEUE, false, deliverCallback, tag -> {});
        
        logger.info("Worker {} is ready and listening", workerId);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Worker {} shutting down. Processed {} sections total.", workerId, processedCount.get());
            try {
                if (channel.isOpen()) {
                    channel.close();
                }
                if (connection.isOpen()) {
                    connection.close();
                }
            } catch (Exception e) {
                logger.error("Error closing connection", e);
            }
        }));
        
        try {
            while (channel.isOpen()) {
                // wait
            }
            logger.info("Worker {} stopping...", workerId);
            Thread.sleep(500);
            channel.close();
            connection.close();
            logger.info("Worker {} stopped", workerId);
        } catch (InterruptedException e) {
            logger.info("Worker {} interrupted", workerId);
            Thread.currentThread().interrupt();
        }
    }
    
    public static void main(String[] args) {
        String workerId = "worker-" + System.currentTimeMillis();

        for (String arg : args) {
            if (arg.startsWith("--worker-id=")) {
                workerId = arg.substring("--worker-id=".length());
            }
        }
        
        try {
            RabbitMQConfig config = RabbitMQConfig.fromEnvironment();
            Worker worker = new Worker(config, workerId);
            worker.run();
        } catch (Exception e) {
            logger.error("Worker failed with error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}

