package io.jenkins.plugins.onboarding;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;

import java.io.IOException;

import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class Level1aRandomValueBuilder extends Builder {
    @DataBoundConstructor
    public Level1aRandomValueBuilder() {
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        // Level 3c
        var descriptor = getDescriptor();
        int min = descriptor.getMinimumValue();
        int maxIncrement = descriptor.getMaximumValue() - descriptor.getMinimumValue();
        int step = descriptor.getStep();
        int maxIncrementByStep = maxIncrement / step;

        int randomValue = min + step * (int) Math.round(Math.random() * maxIncrementByStep);
        build.addAction(new Level2aRandomValueAction(randomValue));
        if (randomValue % 2 == 0) {
            build.addAction(new Level2bEvenValueAction(randomValue));
        }
        listener.getLogger().println("Random = " + randomValue);
        return true;
    }

    // Level 3c
    @Override
    public DescriptorImpl getDescriptor() {
        // [Tip] this method allows us to set the type of the descriptor to easier usage in perform method
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    @Symbol("level1a")
    public static final class DescriptorImpl extends Descriptor<Builder> {
        // [Tip] default value so that even without configuration the plugin is working
        private int step = 1;
        private int minimumValue = 0;
        private int maximumValue = 1000;

        // [Tip] without a public getter, the jelly view will not include the current value
        public int getStep() {
            return step;
        }

        // [Tip] without a public setter, the bindJSON will not apply the change
        public void setStep(int step) {
            this.step = step;
        }

        // level 3b
        public FormValidation doCheckStep(@QueryParameter int value) {
            if (value <= 0) {
                return FormValidation.error("Step must be strictly positive");
            }

            return FormValidation.ok();
        }

        public int getMinimumValue() {
            return minimumValue;
        }

        public void setMinimumValue(int minimumValue) {
            this.minimumValue = minimumValue;
        }

        // level 3b
        public FormValidation doCheckMinimumValue(@QueryParameter int value, @QueryParameter int maximumValue) {
            if (minimumValue > maximumValue) {
                return FormValidation.error("Minimum must be smaller than maximum");
            }
            if (minimumValue == maximumValue) {
                return FormValidation.warning("There is no randomness if the range is empty");
            }

            return FormValidation.ok();
        }

        public int getMaximumValue() {
            return maximumValue;
        }

        public void setMaximumValue(int maximumValue) {
            this.maximumValue = maximumValue;
        }

        // level 3b
        public FormValidation doCheckMaximumValue(@QueryParameter int minimumValue, @QueryParameter int value) {
            if (minimumValue > maximumValue) {
                return FormValidation.error("Maximum must be greater than minimum");
            }
            if (minimumValue == maximumValue) {
                return FormValidation.warning("There is no randomness if the range is empty");
            }

            return FormValidation.ok();
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject data) throws FormException {
            // [Tip] by default the configure method does nothing

            // level 3b
            int step = data.getInt("step");
            FormValidation validation = doCheckStep(step);
            if (validation.kind != FormValidation.Kind.OK) {
                throw new FormException(validation, "step");
            }

            int minimum = data.getInt("minimumValue");
            int maximum = data.getInt("maximumValue");
            validation = doCheckMaximumValue(minimum, maximum);
            if (validation.kind != FormValidation.Kind.OK) {
                throw new FormException(validation, "maximumValue");
            }

            req.bindJSON(this, data);
            // [Tip] Not calling save will not write the descriptor to its file,
            // meaning that after a restart the data is lost
            save();
            return true;
        }
    }
}
