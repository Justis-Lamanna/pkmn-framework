package com.github.lucbui.evaluator;

/**
 * A default evaluator which simply returns whatever is provided.
 */
public class IdentityEvaluator implements Evaluator {
    @Override
    public String evaluate(String evaluation) {
        return evaluation;
    }
}
