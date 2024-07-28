package io.jenkins.plugins.onboarding.modifier;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.onboarding.Level6bNameModifier;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class Level6bAddCustomModifier extends Level6bNameModifier {
    private String customMessage;

    @DataBoundConstructor
    public Level6bAddCustomModifier(){}

    public String getCustomMessage() {
        return customMessage;
    }

    @DataBoundSetter
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }

    @Override
    public String modify(String name) {
        return name + " " + customMessage;
    }

    @Extension
    @Symbol("customMessage")
    public static final class DescriptorImpl extends Descriptor<Level6bNameModifier> {
        @Override
        public String getDisplayName() {
            return "Add a custom message";
        }
    }
}
