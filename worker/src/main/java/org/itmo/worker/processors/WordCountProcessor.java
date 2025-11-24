package org.itmo.worker.processors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;

public class WordCountProcessor implements TextProcessor {
    
    @Override
    public void process(TextSection section, ProcessingResult result) {
        List<String> tokens = result.getTokens();
        
        Map<String, Integer> wordCounts = new HashMap<>();
        for (String token : tokens) {
            wordCounts.merge(token, 1, Integer::sum);
        }
        
        result.setWordCount(tokens.size());
        result.setWordCounts(wordCounts);
    }
    
    @Override
    public String getName() {
        return "WordCount";
    }
}
