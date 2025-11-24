package org.itmo.producer;

import java.util.ArrayList;
import java.util.List;

import org.itmo.common.utils.TextUtils;

public class TextSplitter {
    public static List<String> splitBySentences(String text, int sentencesPerSection) {
        List<String> sentences = TextUtils.splitIntoSentences(text);
        List<String> sections = new ArrayList<>();
        
        StringBuilder currentSection = new StringBuilder();
        int count = 0;
        
        for (String sentence : sentences) {
            currentSection.append(sentence).append(" ");
            count++;
            
            if (count >= sentencesPerSection) {
                sections.add(currentSection.toString().trim());
                currentSection = new StringBuilder();
                count = 0;
            }
        }
        
        if (!currentSection.isEmpty()) {
            sections.add(currentSection.toString().trim());
        }
        
        return sections;
    }
}

