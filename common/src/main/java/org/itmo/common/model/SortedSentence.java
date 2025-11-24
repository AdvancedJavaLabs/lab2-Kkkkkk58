package org.itmo.common.model;

public record SortedSentence(
        String sentence
) implements Comparable<SortedSentence> {
    public int getLength() {
        return sentence.length();
    }
    
    @Override
    public int compareTo(SortedSentence other) {
        return Integer.compare(getLength(), other.getLength());
    }
    
    @Override
    public String toString() {
        return String.format("[%d] %s", getLength(), sentence);
    }
}
