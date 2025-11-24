package org.itmo.worker.processors;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.SentimentScore;
import org.itmo.common.model.TextSection;

public class SentimentProcessor implements TextProcessor {
    
    private static final StanfordCoreNLP pipeline;
    
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
        props.setProperty("tokenize.language", "en");
        pipeline = new StanfordCoreNLP(props);
    }
    
    @Override
    public void process(TextSection section, ProcessingResult result) {
        String text = section.text();
        if (text == null || text.isEmpty()) {
            result.setSentiment(new SentimentScore(0, 0, 0));
            return;
        }
        
        Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        
        int veryPositive = 0;
        int positive = 0;
        int negative = 0;
        int veryNegative = 0;
        
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            switch (sentiment) {
                case "Very positive" -> veryPositive++;
                case "Positive" -> positive++;
                case "Negative" -> negative++;
                case "Very negative" -> veryNegative++;
            }
        }
        
        int positiveCount = veryPositive + positive;
        int negativeCount = veryNegative + negative;
        int totalSentences = sentences.size();

        SentimentScore sentimentScore = new SentimentScore(positiveCount, negativeCount, totalSentences);

        result.setSentiment(sentimentScore);
    }

    @Override
    public String getName() {
        return "StanfordSentiment";
    }
}
