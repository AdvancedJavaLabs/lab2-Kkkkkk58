package org.itmo.common.protocol;


public record TaskMessage(
        String taskId,
        int sectionId,
        int totalSections,
        String text
) {
}
