package org.itmo.worker.processors;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;
import org.itmo.common.utils.TextUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameReplacementProcessorTest {
    
    @Test
    void nameReplacementWithName() {
        NameReplacementProcessor processor = new NameReplacementProcessor();
        TextSection section = new TextSection(0, "Oh hi Mark Wiseau", 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        processor.process(section, result);
        
        String modifiedText = result.getModifiedText();
        assertEquals("Oh hi [NAME]", modifiedText);
    }
    
    @Test
    void nameReplacementWithoutNames() {
        NameReplacementProcessor processor = new NameReplacementProcessor();
        String originText = "the quick brown fox jumps over the lazy dog";
        TextSection section = new TextSection(0, originText, 1);
        ProcessingResult result = new ProcessingResult(0);
        result.setTokens(TextUtils.tokenize(originText));
        
        processor.process(section, result);
        
        String modifiedText = result.getModifiedText();
        assertEquals(originText, modifiedText);
    }
}

