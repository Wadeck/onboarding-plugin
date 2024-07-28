package io.jenkins.plugins.onboarding;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Describable;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

/**
 * Responsible to modify the name that is passed to the build log in
 * {@link Level1bHelloBuilder#perform(AbstractBuild, Launcher, BuildListener)}
 */
public abstract class Level6bNameModifier implements ExtensionPoint, Describable<Level6bNameModifier> {
    public abstract String modify(String name);

    public static ExtensionList<Level6bNameModifier> all() {
        return ExtensionList.lookup(Level6bNameModifier.class);
    }

    @Override
    public Descriptor<Level6bNameModifier> getDescriptor() {
        return Jenkins.get().getDescriptorOrDie(getClass());
    }
}
