package io.jenkins.plugins.onboarding;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Level1bHelloBuilder extends Builder {
    private static final Logger LOGGER = Logger.getLogger(Level1bHelloBuilder.class.getName());

    private final String name;

    // level 6b
    private List<Level6bNameModifier> nameModifiers = new ArrayList<>();

    @DataBoundConstructor
    public Level1bHelloBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // level 6b
    public List<Level6bNameModifier> getNameModifiers() {
        return nameModifiers;
    }

    @DataBoundSetter
    public void setNameModifiers(List<Level6bNameModifier> nameModifiers) {
        this.nameModifiers = nameModifiers;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        var logger = listener.getLogger();

        logger.println("Hello " + name);

        // Level 6a
        for (Level6aLogAppender appender : Level6aLogAppender.all()) {
            try {
                appender.append(logger);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Unexpected exception during append", e);
            }
        }

        // level 6b
        String tempName = name;
        for (Level6bNameModifier nameModifier : nameModifiers) {
            tempName = nameModifier.modify(tempName);
        }

        logger.println("Transformed => hello " + tempName);

        return true;
    }

    @Extension
    @Symbol("level1b")
    public static final class DescriptorImpl extends Descriptor<Builder> {
        // level 6b
        public ExtensionList<Descriptor<Level6bNameModifier>> getAllNameModifiers() {
            return Jenkins.get().getDescriptorList(Level6bNameModifier.class);
        }
    }
}
