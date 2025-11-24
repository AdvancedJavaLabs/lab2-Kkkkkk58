package org.itmo.worker.processors;

import java.util.List;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.SortedSentence;
import org.itmo.common.model.TextSection;
import org.itmo.common.utils.TextUtils;

public class SentenceSorterProcessor implements TextProcessor {
    
    @Override
    public void process(TextSection section, ProcessingResult result) {
        List<String> sentences = TextUtils.splitIntoSentences(section.text());

        var sortedSentences = sentences.stream()
                .map(SortedSentence::new)
                .sorted()
                .toList();

        result.setSortedSentences(sortedSentences);
    }
    
    @Override
    public String getName() {
        return "SentenceSorter";
    }
}

