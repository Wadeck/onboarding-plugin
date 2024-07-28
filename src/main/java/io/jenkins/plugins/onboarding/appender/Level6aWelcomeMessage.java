package io.jenkins.plugins.onboarding.appender;

import hudson.Extension;
import io.jenkins.plugins.onboarding.Level6aLogAppender;
import org.jenkinsci.Symbol;

import java.io.PrintStream;

@Extension
@Symbol("welcome")
public class Level6aWelcomeMessage extends Level6aLogAppender {
    @Override
    public void append(PrintStream logger) {
        logger.println("Welcome");
    }
}
