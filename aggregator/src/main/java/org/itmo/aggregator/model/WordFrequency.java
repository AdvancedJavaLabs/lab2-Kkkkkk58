package org.itmo.aggregator.model;

public record WordFrequency(
        String word,
        int count
) implements Comparable<WordFrequency> {
    @Override
    public int compareTo(WordFrequency other) {
        return Integer.compare(other.count, this.count);
    }

    @Override
    public String toString() {
        return word + ":" + count;
    }
}

