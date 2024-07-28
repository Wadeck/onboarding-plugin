package io.jenkins.plugins.onboarding.modifier;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.onboarding.Level6bNameModifier;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class Level6bRandomModifier extends Level6bNameModifier {

    @DataBoundConstructor
    public Level6bRandomModifier(){}

    @Override
    public String modify(String name) {
        return name + " " + (int) Math.round(Math.random() * 1000);
    }

    @Extension
    @Symbol("random")
    public static final class DescriptorImpl extends Descriptor<Level6bNameModifier> {
        @Override
        public String getDisplayName() {
            return "Add a random suffix";
        }
    }
}
