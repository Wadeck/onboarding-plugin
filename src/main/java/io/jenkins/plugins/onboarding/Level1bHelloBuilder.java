package io.jenkins.plugins.onboarding;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class Level1bHelloBuilder extends Builder {
    private final String name;

    @DataBoundConstructor
    public Level1bHelloBuilder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        listener.getLogger().println("Hello " + name);
        return true;
    }

    @Extension
    @Symbol("level1b")
    public static final class DescriptorImpl extends Descriptor<Builder> {}
}
