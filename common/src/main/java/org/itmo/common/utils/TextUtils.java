package org.itmo.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class TextUtils {
    private static final TokenizerME tokenizer;
    private static final SentenceDetectorME sentenceDetector;
    private static final Pattern NON_LETTER_PATTERN = Pattern.compile("[^\\p{L}]+");
    
    static {
        try {
            tokenizer = loadTokenizer();
            sentenceDetector = loadSentenceDetector();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load OpenNLP models", e);
        }
    }
    
    private static TokenizerME loadTokenizer() throws IOException {
        try (InputStream stream = TextUtils.class.getResourceAsStream("/en-token.bin")) {
            if (stream == null) {
                throw new IOException("Tokenizer model not found: /en-token.bin");
            }
            return new TokenizerME(new TokenizerModel(stream));
        }
    }
    
    private static SentenceDetectorME loadSentenceDetector() throws IOException {
        try (InputStream stream = TextUtils.class.getResourceAsStream("/en-sent.bin")) {
            if (stream == null) {
                throw new IOException("Sentence detector model not found: /en-sent.bin");
            }
            return new SentenceDetectorME(new SentenceModel(stream));
        }
    }
    
    public static String[] tokenizeRaw(String text) {
        if (text == null || text.isEmpty()) {
            return new String[0];
        }
        return tokenizer.tokenize(text);
    }
    
    public static List<String> tokenize(String text) {
        String[] tokens = tokenizeRaw(text);
        List<String> words = new ArrayList<>(tokens.length);
        
        for (String token : tokens) {
            String cleaned = NON_LETTER_PATTERN.matcher(token).replaceAll("").toLowerCase();
            if (cleaned.length() > 1) {
                words.add(cleaned);
            }
        }
        
        return words;
    }
    
    public static List<String> splitIntoSentences(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        
        return Arrays.asList(sentenceDetector.sentDetect(text));
    }
}
