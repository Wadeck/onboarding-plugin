package io.jenkins.plugins.onboarding;

import hudson.model.Action;

public class Level2bEvenValueAction implements Action {
    private final int randomValue;

    public Level2bEvenValueAction(int randomValue) {
        this.randomValue = randomValue;
    }

    // Only used by Jelly
    public int getRandomValue() {
        return randomValue;
    }

    @Override
    public String getIconFileName() {
        return "symbol-ribbon";
    }

    @Override
    public String getDisplayName() {
        return "Even: " + randomValue;
    }

    @Override
    public String getUrlName() {
        return "even";
    }
}
