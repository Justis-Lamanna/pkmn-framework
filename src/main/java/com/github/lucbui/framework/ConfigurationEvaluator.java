package com.github.lucbui.framework;

import com.github.lucbui.config.Configuration;
import com.github.lucbui.utility.ParseUtils;

import java.util.Objects;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An evaluator which evaluates based on a configuration.
 */
public class ConfigurationEvaluator implements Evaluator{

    private Configuration configuration;
    private static final Pattern matchPattern = Pattern.compile("\\$\\{([A-Za-z0-9.]+)(?:\\|([A-Za-z0-9.]+))?}");

    ConfigurationEvaluator(Configuration configuration){
        this.configuration = configuration;
    }

    /**
     * Evaluates a raw configuration string
     * @param evalString
     * @return
     */
    @Override
    public String evaluate(String evalString){
        Matcher matcher = matchPattern.matcher(evalString);
        if(matcher.matches()){
            String configurationKey = matcher.group(1);
            if(matcher.groupCount() > 1){
                String defaultValue = matcher.group(2);
                return configuration.get(configurationKey).orElse(defaultValue);
            } else {
                return configuration.get(configurationKey).orElseThrow(RuntimeException::new);
            }
        } else {
            return evalString;
        }
    }

    /**
     * Evaluate a string as a long.
     * If the string starts with 0x, it is treated as hex. If the string begins with 0b, it is treated
     * as binary. Else, it is treated as decimal.
     * @param evalString
     * @return
     */
    @Override
    public OptionalLong evaluateLong(String evalString){
        Objects.requireNonNull(evalString);
        String evaluated = evaluate(evalString);
        if(evaluated.startsWith("0x")){
            return ParseUtils.parseLong(evaluated.substring(2), 16);
        } else if(evalString.startsWith("0b")){
            return ParseUtils.parseLong(evaluated.substring(2), 2);
        }
        return ParseUtils.parseLong(evaluated, 10);
    }

    /**
     * Evaluate a string as an int.
     * If the string starts with 0x, it is treated as hex. If the string begins with 0b, it is treated
     * as binary. Else, it is treated as decimal.
     * @param evalString
     * @return
     */
    @Override
    public OptionalInt evaluateInt(String evalString){
        Objects.requireNonNull(evalString);
        String evaluated = evaluate(evalString);
        if(evaluated.startsWith("0x")){
            return ParseUtils.parseInt(evaluated.substring(2), 16);
        } else if(evalString.startsWith("0b")){
            return ParseUtils.parseInt(evaluated.substring(2), 2);
        }
        return ParseUtils.parseInt(evaluated, 10);
    }
}
