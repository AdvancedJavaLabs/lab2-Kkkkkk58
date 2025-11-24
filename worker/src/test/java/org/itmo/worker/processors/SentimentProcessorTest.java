package org.itmo.worker.processors;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.SentimentScore;
import org.itmo.common.model.TextSection;
import org.itmo.common.utils.TextUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SentimentProcessorTest {
    
    @Test
    void processSentimentPositiveStatement() {
        SentimentProcessor processor = new SentimentProcessor();
        TextSection section = new TextSection(0, "I love this amazing wonderful fantastic university", 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        processor.process(section, result);
        
        SentimentScore sentiment = result.getSentiment();
        assertNotNull(sentiment);
        assertTrue(sentiment.getPositiveCount() > 0);
        assertEquals("POSITIVE", sentiment.getSentiment());
    }
    
    @Test
    void processSentimentNegativeStatement() {
        SentimentProcessor processor = new SentimentProcessor();
        TextSection section = new TextSection(0, "I hate this terrible awful horrible university", 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        processor.process(section, result);
        
        SentimentScore sentiment = result.getSentiment();
        assertNotNull(sentiment);
        assertTrue(sentiment.getNegativeCount() > 0);
        assertEquals("NEGATIVE", sentiment.getSentiment());
    }
    
    @Test
    void processSentimentNeutralStatement() {
        SentimentProcessor processor = new SentimentProcessor();
        TextSection section = new TextSection(0, "The university is in Saint-P", 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        processor.process(section, result);
        
        SentimentScore sentiment = result.getSentiment();
        assertNotNull(sentiment);
        assertEquals("NEUTRAL", sentiment.getSentiment());
    }
    
    @Test
    void processSentimentEmptyStatement() {
        SentimentProcessor processor = new SentimentProcessor();
        TextSection section = new TextSection(0, "", 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        processor.process(section, result);
        
        SentimentScore sentiment = result.getSentiment();
        assertNotNull(sentiment);
        assertEquals("NEUTRAL", sentiment.getSentiment());
    }
}

