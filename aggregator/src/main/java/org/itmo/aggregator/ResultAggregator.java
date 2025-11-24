package org.itmo.aggregator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.itmo.aggregator.model.AggregatedResult;
import org.itmo.aggregator.model.WordFrequency;
import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.SentimentScore;
import org.itmo.common.model.SortedSentence;

public class ResultAggregator {
    private final Map<Integer, ProcessingResult> results;
    private final int expectedSections;
    private final int topN;
    
    public ResultAggregator(int expectedSections, int topN) {
        this.results = new TreeMap<>();
        this.expectedSections = expectedSections;
        this.topN = topN;
    }

    public synchronized void addResult(ProcessingResult result) {
        results.put(result.getSectionId(), result);
    }

    public synchronized boolean isComplete() {
        return results.size() == expectedSections;
    }

    public double getProgress() {
        return (results.size() * 100.0) / expectedSections;
    }

    public int getReceivedCount() {
        return results.size();
    }
    
    public int getExpectedCount() {
        return expectedSections;
    }

    public synchronized AggregatedResult aggregate() {
        AggregatedResult aggregated = new AggregatedResult();
        aggregated.setTotalSections(expectedSections);
        
        Map<String, Integer> globalWordCounts = new HashMap<>();
        int totalWords = 0;
        int totalPositive = 0;
        int totalNegative = 0;
        long totalProcessingTime = 0;
        Map<String, Integer> workerStats = new HashMap<>();
        List<SortedSentence> allSentences = new ArrayList<>();
        StringBuilder completeText = new StringBuilder();
        
        for (ProcessingResult result : results.values()) {
            totalWords += result.getWordCount();
            for (Map.Entry<String, Integer> entry : result.getWordCounts().entrySet()) {
                globalWordCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
            }
            
            if (result.getSentiment() != null) {
                totalPositive += result.getSentiment().getPositiveCount();
                totalNegative += result.getSentiment().getNegativeCount();
            }
            
            totalProcessingTime += result.getProcessingTimeMs();
            
            String workerId = result.getWorkerId();
            workerStats.put(workerId, workerStats.getOrDefault(workerId, 0) + 1);
            
            allSentences.addAll(result.getSortedSentences());
            
            completeText.append(result.getModifiedText()).append("\n\n");
        }
        
        aggregated.setTotalWordCount(totalWords);
        aggregated.setGlobalWordCounts(globalWordCounts);
        
        List<WordFrequency> topWords = globalWordCounts.entrySet().stream()
            .map(e -> new WordFrequency(e.getKey(), e.getValue()))
            .sorted()
            .limit(topN)
            .collect(Collectors.toList());
        aggregated.setTopWords(topWords);
        
        SentimentScore overallSentiment = new SentimentScore(totalPositive, totalNegative, totalWords);
        aggregated.setOverallSentiment(overallSentiment);
        
        aggregated.setCompleteModifiedText(completeText.toString().trim());
        
        Collections.sort(allSentences);
        aggregated.setAllSortedSentences(allSentences);
        
        aggregated.setTotalProcessingTimeMs(totalProcessingTime);
        aggregated.setWorkersUsed(workerStats.size());
        aggregated.setWorkerStats(workerStats);
        
        return aggregated;
    }
}

