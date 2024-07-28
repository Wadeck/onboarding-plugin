package io.jenkins.plugins.onboarding;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.ManagementLink;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.verb.POST;

import javax.servlet.ServletException;
import java.io.IOException;

@Extension
@Symbol("level5a")
public class Level5aUrlRestrictionLink extends ManagementLink {
    @Override
    public String getIconFileName() {
        return "symbol-environment";
    }

    @Override
    public String getDisplayName() {
        return "URL Restriction for level4a";
    }

    @Override
    public String getUrlName() {
        return "url-restriction";
    }

    @Override
    public String getDescription() {
        return "Configure the regex to restrict the URLs that can be used in level4a step.";
    }

    @NonNull
    @Override
    public Category getCategory() {
        return Category.CONFIGURATION;
    }

    public String getRegex() {
        return Level5bUrlRestriction.get().getRegex();
    }

    // level 5b
    @POST
    public void doConfigure(StaplerRequest req, StaplerResponse rsp) throws ServletException, IOException {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);

        JSONObject json = req.getSubmittedForm();
        Level5bUrlRestriction.get().setRegex(json.getString("regex"));

        rsp.sendRedirect("..");
    }
}
