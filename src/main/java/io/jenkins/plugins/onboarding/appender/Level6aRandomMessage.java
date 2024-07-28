package io.jenkins.plugins.onboarding.appender;

import hudson.Extension;
import io.jenkins.plugins.onboarding.Level6aLogAppender;
import org.jenkinsci.Symbol;

import java.io.PrintStream;

@Extension
@Symbol("random")
public class Level6aRandomMessage extends Level6aLogAppender {
    @Override
    public void append(PrintStream logger) {
        logger.println("Random " + (int) Math.round(Math.random() * 1000));
    }
}
