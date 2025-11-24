package org.itmo.worker.processors;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;
import org.itmo.common.utils.TextUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordCountProcessorTest {
    @Test
    void countWordsCaseInsensitive() {
        WordCountProcessor processor = new WordCountProcessor();
        TextSection section = new TextSection(0, "Hello HELLO hello world", 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        processor.process(section, result);
        
        assertEquals(4, result.getWordCount());
        assertEquals(3, result.getWordCounts().get("hello"));
        assertEquals(1, result.getWordCounts().get("world"));

    }
}

