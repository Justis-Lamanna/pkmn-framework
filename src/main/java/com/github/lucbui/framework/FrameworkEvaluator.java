package com.github.lucbui.framework;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FrameworkEvaluator {

    private PkmnFramework pkmnFramework;
    private static final Pattern matchPattern = Pattern.compile("\\$\\{([A-Za-z0-9.]+)(?:\\|([A-Za-z0-9.]+))?}");

    FrameworkEvaluator(PkmnFramework pkmnFramework){
        this.pkmnFramework = pkmnFramework;
    }

    /**
     * Evaluates a raw configuration string
     * @param evalString
     * @return
     */
    protected String evaluate(String evalString){
        Matcher matcher = matchPattern.matcher(evalString);
        if(matcher.matches()){
            String configurationKey = matcher.group(1);
            if(matcher.groupCount() > 1){
                String defaultValue = matcher.group(2);
                return pkmnFramework.getFromConfig(configurationKey).orElse(defaultValue);
            } else {
                return pkmnFramework.getFromConfig(configurationKey).orElseThrow(RuntimeException::new);
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
    public long evaluateLong(String evalString){
        Objects.requireNonNull(evalString);
        if(evalString.startsWith("0x")){
            return Long.parseLong(evalString, 16);
        } else if(evalString.startsWith("0b")){
            return Long.parseLong(evalString, 2);
        }
        return Long.parseLong(evalString);
    }
}
