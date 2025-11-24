package org.itmo.worker;

import java.util.ArrayList;
import java.util.List;

import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;
import org.itmo.common.utils.TextUtils;
import org.itmo.worker.processors.TextProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessingPipeline {
    private static final Logger logger = LoggerFactory.getLogger(ProcessingPipeline.class);
    
    private final List<TextProcessor> processors;
    
    public ProcessingPipeline() {
        this.processors = new ArrayList<>();
    }

    public ProcessingPipeline addProcessor(TextProcessor processor) {
        processors.add(processor);
        return this;
    }

    public ProcessingResult process(TextSection section, String workerId) {
        ProcessingResult result = new ProcessingResult(section.sectionId(), section.totalSections());
        result.setWorkerId(workerId);
        result.setTokens(TextUtils.tokenize(section.text()));
        
        long startTime = System.nanoTime();
        
        for (TextProcessor processor : processors) {
            try {
                processor.process(section, result);
                logger.debug("Processor {} completed for section {}", 
                    processor.getName(), section.sectionId());
            } catch (Exception e) {
                logger.error("Processor {} failed for section {}", 
                    processor.getName(), section.sectionId(), e);
                throw new RuntimeException("Processing failed at " + processor.getName(), e);
            }
        }
        
        long elapsed = System.nanoTime() - startTime;
        result.setProcessingTimeMs(elapsed / 1_000_000);
        
        logger.debug("Section {} processed in {} ms", section.sectionId(), elapsed);
        
        return result;
    }
}

