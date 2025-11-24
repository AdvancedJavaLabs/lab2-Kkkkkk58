package org.itmo.worker.processors;

import java.util.Arrays;
import java.util.regex.Pattern;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;
import org.itmo.common.model.ProcessingResult;
import org.itmo.common.model.TextSection;
import org.itmo.common.utils.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameReplacementProcessor implements TextProcessor {
    private static final Logger log = LoggerFactory.getLogger(NameReplacementProcessor.class);

    private static final String NAME_REPLACEMENT = "[NAME]";
    private static final Pattern NAME_PATTERN = Pattern.compile("\\b[A-ZА-ЯЁ][a-zа-яё]{2,}\\b");
    private final NameFinderME nameFinder;

    public NameReplacementProcessor() {
        NameFinderME finder = null;
        try (var stream = getClass().getResourceAsStream("/en-ner-person.bin")) {
            if (stream != null) {
                TokenNameFinderModel model = new TokenNameFinderModel(stream);
                finder = new NameFinderME(model);
                log.debug("Will use opennlp model for names");
            }
        } catch (Exception e) {
            log.error("Failed to load opennlp model", e);
        }

        this.nameFinder = finder;
    }

    @Override
    public void process(TextSection section, ProcessingResult result) {
        String text = section.text();

        if (nameFinder != null) {
            String[] tokens = TextUtils.tokenizeRaw(text);
            Span[] names = nameFinder.find(tokens);

            for (int i = names.length - 1; i >= 0; i--) {
                String fullName = String.join(
                        " ",
                        Arrays.copyOfRange(tokens, names[i].getStart(), names[i].getEnd())
                );
                text = text.replaceFirst(Pattern.quote(fullName), NAME_REPLACEMENT);
            }
            nameFinder.clearAdaptiveData();
        } else {
            text = NAME_PATTERN.matcher(text).replaceAll(NAME_REPLACEMENT);
        }

        result.setModifiedText(text);
    }
    
    @Override
    public String getName() {
        return "NameReplacement";
    }
}
