package io.jenkins.plugins.onboarding.modifier;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.onboarding.Level6bNameModifier;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.Locale;

public class Level6bUppercaseModifier extends Level6bNameModifier {

    @DataBoundConstructor
    public Level6bUppercaseModifier(){}

    @Override
    public String modify(String name) {
        return name.toUpperCase(Locale.ENGLISH);
    }

    @Extension
    @Symbol("uppercase")
    public static final class DescriptorImpl extends Descriptor<Level6bNameModifier> {
        @Override
        public String getDisplayName() {
            return "Transform to uppercase";
        }
    }
}
