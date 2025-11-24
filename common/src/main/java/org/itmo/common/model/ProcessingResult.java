package org.itmo.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProcessingResult {
    private int sectionId;
    private int totalSections;
    private String workerId;
    private int wordCount;
    private Map<String, Integer> wordCounts;
    private SentimentScore sentiment;
    private String modifiedText;
    private List<SortedSentence> sortedSentences;
    private long processingTimeMs;
    
    @JsonIgnore
    private List<String> tokens;
    
    public ProcessingResult() {
        this.wordCounts = new HashMap<>();
        this.sortedSentences = new ArrayList<>();
    }
    
    public ProcessingResult(int sectionId) {
        this();
        this.sectionId = sectionId;
    }
    
    public ProcessingResult(int sectionId, int totalSections) {
        this();
        this.sectionId = sectionId;
        this.totalSections = totalSections;
    }
    
    public int getSectionId() {
        return sectionId;
    }
    
    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
    
    public int getTotalSections() {
        return totalSections;
    }
    
    public void setTotalSections(int totalSections) {
        this.totalSections = totalSections;
    }
    
    public String getWorkerId() {
        return workerId;
    }
    
    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }
    
    public int getWordCount() {
        return wordCount;
    }
    
    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }
    
    public Map<String, Integer> getWordCounts() {
        return wordCounts;
    }
    
    public void setWordCounts(Map<String, Integer> wordCounts) {
        this.wordCounts = wordCounts;
    }
    
    public SentimentScore getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(SentimentScore sentiment) {
        this.sentiment = sentiment;
    }
    
    public String getModifiedText() {
        return modifiedText;
    }
    
    public void setModifiedText(String modifiedText) {
        this.modifiedText = modifiedText;
    }
    
    public List<SortedSentence> getSortedSentences() {
        return sortedSentences;
    }
    
    public void setSortedSentences(List<SortedSentence> sortedSentences) {
        this.sortedSentences = sortedSentences;
    }
    
    public long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }
    
    @JsonIgnore
    public List<String> getTokens() {
        return tokens;
    }
    
    @JsonIgnore
    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
}

