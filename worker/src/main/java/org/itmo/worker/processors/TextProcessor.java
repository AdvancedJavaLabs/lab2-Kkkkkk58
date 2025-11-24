package org.itmo.worker.processors;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;

public interface TextProcessor {
    void process(TextSection section, ProcessingResult result);
    String getName();
}

