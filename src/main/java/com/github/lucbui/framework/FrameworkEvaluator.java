package com.github.lucbui.framework;

import java.util.regex.Matcher;

public class FrameworkEvaluator {

    private PkmnFramework pkmnFramework;
    private long startPosition;

    public FrameworkEvaluator(PkmnFramework pkmnFramework, long startPosition){
        this.pkmnFramework = pkmnFramework;
        this.startPosition = startPosition;
    }

    public int evaluate(String evalString){
        //TODO: Evaluate this bad boy.
        return 0;
    }
}
