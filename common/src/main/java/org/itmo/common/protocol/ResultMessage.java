package org.itmo.common.protocol;

import org.itmo.common.model.ProcessingResult;

public record ResultMessage(
        String taskId,
        ProcessingResult result
) {
}

