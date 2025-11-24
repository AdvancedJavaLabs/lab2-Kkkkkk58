package org.itmo.common.model;

public class SentimentScore {
    private int positiveCount;
    private int negativeCount;
    private double score;
    private String sentiment;
    
    public SentimentScore(int positiveCount, int negativeCount, int totalWords) {
        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.score = totalWords > 0 ? (double)(positiveCount - negativeCount) / totalWords : 0.0;
        
        if (score > 0.05) {
            this.sentiment = "POSITIVE";
        } else if (score < -0.05) {
            this.sentiment = "NEGATIVE";
        } else {
            this.sentiment = "NEUTRAL";
        }
    }
    
    public int getPositiveCount() {
        return positiveCount;
    }
    
    public void setPositiveCount(int positiveCount) {
        this.positiveCount = positiveCount;
    }
    
    public int getNegativeCount() {
        return negativeCount;
    }
    
    public void setNegativeCount(int negativeCount) {
        this.negativeCount = negativeCount;
    }
    
    public double getScore() {
        return score;
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    
    public String getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    
    @Override
    public String toString() {
        return String.format("%s (score: %.3f, pos: %d, neg: %d)", 
            sentiment, score, positiveCount, negativeCount);
    }
}

