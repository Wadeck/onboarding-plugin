package io.jenkins.plugins.onboarding;

import hudson.XmlFile;
import hudson.init.Initializer;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import jenkins.model.Jenkins;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsible to restrict which URLs are accepted by
 * {@link Level4aConnectionBuildStep#setTargetUrl(String)}
 */
public class Level5bUrlRestriction implements Saveable {
    private static final Logger LOGGER = Logger.getLogger(Level5bUrlRestriction.class.getName());

    private String regex;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) throws IOException {
        this.regex = regex;
        save();
    }

    private XmlFile getConfigFile() {
        return new XmlFile(new File(Jenkins.get().getRootDir(), this.getClass().getName() + ".xml"));
    }

    @Initializer
    public static void init(Jenkins h) throws IOException {
        SINGLETON.load();
    }

    public void load() throws IOException {
        try {
            getConfigFile().unmarshal(this);
        } catch (Exception e) {
            LOGGER.log(Level.FINER, "URL restriction not yet configured", e);
        }
    }

    @Override
    public void save() throws IOException {
        XmlFile configFile = getConfigFile();
        try {
            configFile.write(this);
            SaveableListener.fireOnChange(this, configFile);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to save " + configFile, e);
        }
    }

    public static Level5bUrlRestriction get() {
        return SINGLETON;
    }

    private static final Level5bUrlRestriction SINGLETON = new Level5bUrlRestriction();
}
