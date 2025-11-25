package org.itmo.aggregator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import org.itmo.aggregator.model.AggregatedResult;
import org.itmo.common.config.RabbitMQConfig;
import org.itmo.common.protocol.ResultMessage;
import org.itmo.common.utils.MessageSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aggregator {
    private static final Integer TIMEOUT_MINUTES = 60;
    private static final Logger logger = LoggerFactory.getLogger(Aggregator.class);

    private final RabbitMQConfig config;
    private final String outputFile;
    private final int topN;
    private volatile ResultAggregator resultAggregator;
    private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();

    public Aggregator(RabbitMQConfig config, String outputFile, int topN) {
        this.config = config;
        this.outputFile = outputFile;
        this.topN = topN;
    }

    public void run() throws IOException, TimeoutException, InterruptedException {
        logger.info("Aggregator starting...");

        Connection connection = config.createConnection();
        Channel channel = connection.createChannel();
        // queue, durable, exclusive, autoDelete, arguments
        channel.queueDeclare(RabbitMQConfig.RESULTS_QUEUE, true, false, false, null);

        long startTime = System.nanoTime();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                ResultMessage msg = MessageSerializer.deserialize(delivery.getBody(), ResultMessage.class);

                if (resultAggregator == null) {
                    synchronized (this) {
                        if (resultAggregator == null) {
                            int totalSections = msg.result().getTotalSections();
                            resultAggregator = new ResultAggregator(totalSections, topN);
                            logger.info("Initialized. Expecting {} sections", totalSections);
                        }
                    }
                }

                resultAggregator.addResult(msg.result());
                logger.info(
                        "Received {}/{} ({}) from worker {}",
                        resultAggregator.getReceivedCount(),
                        resultAggregator.getExpectedCount(),
                        resultAggregator.getProgress(),
                        msg.result().getWorkerId()
                );

                if (resultAggregator.isComplete()) {
                    long elapsed = (System.nanoTime() - startTime) / 1_000_000;
                    logger.info("All results received in {} ms. Aggregating...", elapsed);

                    AggregatedResult result = resultAggregator.aggregate();
                    saveToFile(result);
                    printStatistics(result);

                    completionFuture.complete(null);
                }

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                logger.error("Error processing result", e);
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            }
        };

        channel.basicConsume(RabbitMQConfig.RESULTS_QUEUE, false, deliverCallback, tag -> {});

        try {
            completionFuture.get(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (java.util.concurrent.TimeoutException e) {
            logger.error("Timeout after {} minutes!", TIMEOUT_MINUTES);
        } catch (java.util.concurrent.ExecutionException e) {
            logger.error("Error during aggregation", e.getCause());
        }

        channel.close();
        connection.close();
    }

    private void saveToFile(AggregatedResult result) throws IOException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(MessageSerializer.toJson(result));
        }
        logger.info("Saved to {}", outputFile);
    }

    private void printStatistics(AggregatedResult result) {
        logger.info("=== RESULTS ===");
        logger.info("Total words: {} (unique: {})", result.getTotalWordCount(), result.getGlobalWordCounts().size());
        logger.info("Sentiment: {}", result.getOverallSentiment());
        logger.info("Processing time: {} ms (CPU sum)", result.getTotalProcessingTimeMs());
        logger.info("Workers: {}", result.getWorkersUsed());

        logger.info("Top {} words:", topN);
        int i = 1;
        for (var word : result.getTopWords()) {
            logger.info("  {}. {} - {} times", i++, word.word(), word.count());
        }

        logger.info("Worker stats:");
        for (var entry : result.getWorkerStats().entrySet()) {
            logger.info("  {}: {} sections", entry.getKey(), entry.getValue());
        }

        logger.info("===============");
    }

    public static void main(String[] args) {
        String outputFile = "results.json";
        int topN = 10;

        for (String arg : args) {
            if (arg.startsWith("--output=")) {
                outputFile = arg.substring("--output=".length());
            } else if (arg.startsWith("--top-n=")) {
                topN = Integer.parseInt(arg.substring("--top-n=".length()));
            }
        }

        try {
            RabbitMQConfig config = RabbitMQConfig.fromEnvironment();
            new Aggregator(config, outputFile, topN).run();
        } catch (Exception e) {
            logger.error("Aggregator failed", e);
            System.exit(1);
        }
    }
}
