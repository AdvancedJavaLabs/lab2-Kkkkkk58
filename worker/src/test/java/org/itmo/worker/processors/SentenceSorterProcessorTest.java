package org.itmo.worker.processors;

import java.util.List;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.SortedSentence;
import org.itmo.common.model.TextSection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SentenceSorterProcessorTest {
    
    @Test
    void testSentenceSorting() {
        SentenceSorterProcessor processor = new SentenceSorterProcessor();
        TextSection section = new TextSection(0, "Lorem. Ipsum dolor. Sit", 1);
        ProcessingResult result = new ProcessingResult(0);
        
        processor.process(section, result);
        
        List<SortedSentence> sorted = result.getSortedSentences();
        assertEquals(List.of("Sit", "Lorem.", "Ipsum dolor."), sorted.stream().map(SortedSentence::sentence).toList());
    }
}

