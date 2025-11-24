package org.itmo.aggregator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.itmo.common.model.SentimentScore;
import org.itmo.common.model.SortedSentence;

public class AggregatedResult {
    /**
     * Всего секций в корпусе
     */
    private int totalSections;
    /**
     * Всего слов
     */
    private int totalWordCount;
    /**
     * Счётчик по словам
     */
    private Map<String, Integer> globalWordCounts;
    /**
     * Топ слов
     */
    private List<WordFrequency> topWords;
    /**
     * Оценка тональности
     */
    private SentimentScore overallSentiment;
    /**
     * Модифицированный текст
     */
    private String completeModifiedText;
    /**
     * Отсортированные предложения
     */
    private List<SortedSentence> allSortedSentences;
    /**
     * Общее время обработки
     */
    private long totalProcessingTimeMs;
    /**
     * Количество задействованных обработчиков
     */
    private int workersUsed;
    /**
     * Статистика исполненных заданий по обработчикам
     */
    private Map<String, Integer> workerStats;

    public AggregatedResult() {
        this.globalWordCounts = new HashMap<>();
        this.topWords = new ArrayList<>();
        this.allSortedSentences = new ArrayList<>();
        this.workerStats = new HashMap<>();
    }

    public int getTotalSections() {
        return totalSections;
    }

    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    public void setTotalWordCount(int totalWordCount) {
        this.totalWordCount = totalWordCount;
    }

    public Map<String, Integer> getGlobalWordCounts() {
        return globalWordCounts;
    }

    public void setGlobalWordCounts(Map<String, Integer> globalWordCounts) {
        this.globalWordCounts = globalWordCounts;
    }

    public List<WordFrequency> getTopWords() {
        return topWords;
    }

    public void setTopWords(List<WordFrequency> topWords) {
        this.topWords = topWords;
    }

    public SentimentScore getOverallSentiment() {
        return overallSentiment;
    }

    public void setOverallSentiment(SentimentScore overallSentiment) {
        this.overallSentiment = overallSentiment;
    }

    public String getCompleteModifiedText() {
        return completeModifiedText;
    }

    public void setCompleteModifiedText(String completeModifiedText) {
        this.completeModifiedText = completeModifiedText;
    }

    public List<SortedSentence> getAllSortedSentences() {
        return allSortedSentences;
    }

    public void setAllSortedSentences(List<SortedSentence> allSortedSentences) {
        this.allSortedSentences = allSortedSentences;
    }

    public long getTotalProcessingTimeMs() {
        return totalProcessingTimeMs;
    }

    public void setTotalProcessingTimeMs(long totalProcessingTimeMs) {
        this.totalProcessingTimeMs = totalProcessingTimeMs;
    }

    public int getWorkersUsed() {
        return workersUsed;
    }

    public void setWorkersUsed(int workersUsed) {
        this.workersUsed = workersUsed;
    }

    public Map<String, Integer> getWorkerStats() {
        return workerStats;
    }

    public void setWorkerStats(Map<String, Integer> workerStats) {
        this.workerStats = workerStats;
    }
}

