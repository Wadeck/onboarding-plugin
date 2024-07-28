package io.jenkins.plugins.onboarding;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.PrintStream;

/**
 * Responsible to generate a message in the log during
 * {@link Level1bHelloBuilder#perform(AbstractBuild, Launcher, BuildListener)}
 */
public abstract class Level6aLogAppender implements ExtensionPoint {
    public abstract void append(PrintStream logger);

    public static ExtensionList<Level6aLogAppender> all() {
        return ExtensionList.lookup(Level6aLogAppender.class);
    }
}
