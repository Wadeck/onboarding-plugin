package io.jenkins.plugins.onboarding;

import hudson.model.Action;

public class Level2aRandomValueAction implements Action {
    private final int randomValue;

    public Level2aRandomValueAction(int randomValue) {
        this.randomValue = randomValue;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }
}
