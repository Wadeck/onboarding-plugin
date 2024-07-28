package io.jenkins.plugins.onboarding;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.*;
import hudson.model.*;
import hudson.model.queue.Tasks;
import hudson.security.ACL;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

// [Tip] SimpleBuildStep is required to be called from within a pipeline as step([$class: 'Level4aConnectionBuildStep'])
public class Level4aConnectionBuildStep extends Builder implements SimpleBuildStep {
    // level 4b
    private String targetUrl;
    // level 4c
    private String username;
    private Secret password;
    // level 4d
    private String credentialId;

    @DataBoundConstructor
    public Level4aConnectionBuildStep() {
    }

    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher, @NonNull TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Level4aConnectionBuildStep calling");

        // level 4b
        FormValidation formValidation = getDescriptor().sendRequest(run.getParent(), targetUrl, username, getPasswordPlainText(), credentialId);

        listener.getLogger().println("Level4aConnectionBuildStep called, result: " + formValidation.kind.name());

        if (formValidation.kind != FormValidation.Kind.OK) {
            run.setResult(Result.FAILURE);
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        // [Tip] this method allows us to set the type of the descriptor to easier usage in perform method
        return (DescriptorImpl) super.getDescriptor();
    }

    // level 4b
    public String getTargetUrl() {
        return targetUrl;
    }

    // [Tip] We can also use setter instead of the constructor to bind data
    @DataBoundSetter
    public void setTargetUrl(String targetUrl) throws Descriptor.FormException {
        // level 5b
        String regex = Level5bUrlRestriction.get().getRegex();
        if (regex != null) {
            if (!targetUrl.matches(regex)) {
                throw new Descriptor.FormException("Target URL does not match the globally configured regex: [" + regex + "]", "targetUrl");
            }
        }
        this.targetUrl = targetUrl;
    }

    // level 4c
    public String getUsername() {
        return username;
    }

    @DataBoundSetter
    public void setUsername(String username) {
        this.username = username;
    }

    public Secret getPassword() {
        return password;
    }

    public String getPasswordPlainText() {
        return password == null ? null : password.getPlainText();
    }

    @DataBoundSetter
    public void setPassword(String password) {
        this.password = Secret.fromString(password);
    }

    // level 4d
    public String getCredentialId() {
        return credentialId;
    }

    @DataBoundSetter
    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    @Extension
    // [Tip] Symbol allows you to call this step as `level4a()` in the pipeline in addition to step([$class: 'Level4aConnectionBuildStep'])
    @Symbol("level4a")
    // [Tip] BuildStepDescriptor is not required to be used as a pipeline step but provide additional capabilities
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        // level 4b
        public FormValidation doCheckTargetUrl(@QueryParameter String value) {
            if (value.isBlank()) {
                return FormValidation.warning("Target URL should not be blank");
            }

            try {
                new URI(value);
            } catch (URISyntaxException e) {
                return FormValidation.error(e, "Target URL is malformed");
            }

            // level 5b
            String regex = Level5bUrlRestriction.get().getRegex();
            if (regex != null) {
                if (!value.matches(regex)) {
                    return FormValidation.error("Target URL does not match the globally configured regex: " + regex + "");
                }
            }

            return FormValidation.ok();
        }

        // [Tip] Using separate method to reuse the logic in perform and doValidate
        private FormValidation sendRequest(Item item, String targetUrl, String username, String password, String credentialId) {
            // level 4c
            String authenticationHeaderName = "Authorization";
            String authenticationHeaderValue;
            if (credentialId != null) {
                // level 4d
                UsernamePasswordCredentialsImpl credentials = CredentialsMatchers.firstOrNull(
                        CredentialsProvider.lookupCredentialsInItem(
                                UsernamePasswordCredentialsImpl.class,
                                item,
                                item instanceof Queue.Task
                                        ? Tasks.getAuthenticationOf2((Queue.Task)item)
                                        : ACL.SYSTEM2
                        ),
                        CredentialsMatchers.withId(credentialId)
                );
                if (credentials == null) {
                    return FormValidation.error("Credentials not found");
                }

                authenticationHeaderValue = Base64.getEncoder().encodeToString((credentials.getUsername() + ":" + credentials.getPassword().getPlainText()).getBytes(StandardCharsets.UTF_8));
            } else if (username != null && password != null) {
                authenticationHeaderValue = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
            } else {
                authenticationHeaderValue = null;
            }

            HttpClient httpClient = ProxyConfiguration.newHttpClient();
            HttpRequest httpRequest;
            try {
                URI uri = new URI(targetUrl);
                HttpRequest.Builder builder = ProxyConfiguration.newHttpRequestBuilder(uri)
                        .method("POST", HttpRequest.BodyPublishers.noBody());
                if (authenticationHeaderValue != null) {
                    builder.header(authenticationHeaderName, authenticationHeaderValue);
                }
                httpRequest = builder.build();
            } catch (IllegalArgumentException | URISyntaxException e) {
                return FormValidation.error(e, "Target URL is malformed");
            }

            try {
                HttpResponse<Void> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
                if (httpResponse.statusCode() == HttpURLConnection.HTTP_OK) {
                    return FormValidation.ok();
                }
                return FormValidation.error("Server rejected connection");
            } catch (IOException | InterruptedException e) {
                return FormValidation.error(e, "Could not connect to target URL");
            }
        }

        // level 4b
        @POST
        public FormValidation doValidateTargetUrl(@AncestorInPath Item item,
                                                  @QueryParameter String targetUrl,
                                                  // level 4c
                                                  @QueryParameter(fixEmpty = true) String username, @QueryParameter(fixEmpty = true) String password,
                                                  // level 4d
                                                  @QueryParameter(fixEmpty = true) String credentialId) throws InterruptedException {
            if (item == null) {
                Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            } else {
                item.checkPermission(Job.CONFIGURE);
            }

            return sendRequest(item, targetUrl, username, password, credentialId);
        }

        // level 4d
        // Inspired by https://github.com/jenkinsci/credentials-plugin/blob/master/docs/consumer.adoc
        public ListBoxModel doFillCredentialIdItems(
                @AncestorInPath Item item,
                @QueryParameter String credentialId
        ) {
            StandardListBoxModel result = new StandardListBoxModel();
            if (item == null) {
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return result.includeCurrentValue(credentialId);
                }
            } else {
                if (!item.hasPermission(Item.EXTENDED_READ)
                        && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return result.includeCurrentValue(credentialId);
                }
            }

            result.includeEmptyValue();
            result.includeMatchingAs(
                    item instanceof Queue.Task
                            ? Tasks.getAuthenticationOf2((Queue.Task)item)
                            : ACL.SYSTEM2,
                    item,
                    UsernamePasswordCredentialsImpl.class,
                    Collections.emptyList(),
                    CredentialsMatchers.always()
            );
            return result;
        }

        // level 4d
        public FormValidation doCheckCredentialId(@AncestorInPath Item item, @QueryParameter(fixEmpty = true) String value) {
            if (item == null) {
                if (!Jenkins.get().hasPermission(Jenkins.ADMINISTER)) {
                    return FormValidation.ok();
                }
            } else {
                if (!item.hasPermission(Item.EXTENDED_READ)
                        && !item.hasPermission(CredentialsProvider.USE_ITEM)) {
                    return FormValidation.ok();
                }
            }
            if (value == null) {
                return FormValidation.ok();
            }
            if (value.startsWith("${") && value.endsWith("}")) {
                return FormValidation.warning("Cannot validate expression based credentials");
            }

            UsernamePasswordCredentialsImpl credentials = CredentialsMatchers.firstOrNull(
                    CredentialsProvider.lookupCredentialsInItem(
                            UsernamePasswordCredentialsImpl.class,
                            item,
                            item instanceof Queue.Task
                                    ? Tasks.getAuthenticationOf2((Queue.Task)item)
                                    : ACL.SYSTEM2
                    ),
                    CredentialsMatchers.withId(value)
            );
            if (credentials == null) {
                return FormValidation.error("Cannot find currently selected credentials");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
