package io.jenkins.plugins.onboarding;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

public class Level1aRandomValueBuilder extends Builder {
    @DataBoundConstructor
    public Level1aRandomValueBuilder() {
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        int randomValue = (int) Math.round(Math.random() * 1000);
        listener.getLogger().println("Random = " + randomValue);
        return true;
    }

    @Extension
    @Symbol("level1a")
    public static final class DescriptorImpl extends Descriptor<Builder> {
    }
}
