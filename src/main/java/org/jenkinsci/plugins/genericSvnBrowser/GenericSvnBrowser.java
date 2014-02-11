package org.jenkinsci.plugins.genericSvnBrowser;

import hudson.*;
import hudson.model.*;
import hudson.scm.*;
import hudson.scm.SubversionChangeLogSet.*;
import org.kohsuke.stapler.*;

import java.io.*;
import java.net.*;

@SuppressWarnings("UnusedDeclaration")
public class GenericSvnBrowser extends SubversionRepositoryBrowser {
    private static final long serialVersionUID = 1L;

    @Extension
    public static class DescriptorImpl extends Descriptor<RepositoryBrowser<?>> {
        public String getDisplayName () {
            return "(Generic)";
        }
    }

    public final String fileUrlPattern;
    public final String diffUrlPattern;
    public final String setUrlPattern;

    @DataBoundConstructor
    public GenericSvnBrowser (String fileUrlPattern, String diffUrlPattern, String setUrlPattern) throws MalformedURLException {
        this.fileUrlPattern = fileUrlPattern;
        this.diffUrlPattern = diffUrlPattern;
        this.setUrlPattern  = setUrlPattern;
    }

    @Override
    public URL getFileLink (Path path) throws IOException {
        return new URL(replaceAll(fileUrlPattern, path.getPath(), path.getLogEntry().getRevision()));
    }

    @Override
    public URL getDiffLink (Path path) throws IOException {
        if (path.getEditType() !=EditType.EDIT) {
            return null; // no diff if this is not an edit change
        }else {
            return new URL(replaceAll(diffUrlPattern, path.getPath(), path.getLogEntry().getRevision()));
        }
    }

    @Override
    public URL getChangeSetLink (LogEntry changeSet) throws IOException {
        return new URL(replaceAll(setUrlPattern, null, changeSet.getRevision()));
    }

    private String replaceAll (String s, String path, int rev) throws UnsupportedEncodingException {
        if (s ==null) {
            return "";
        }else {
            s = s.replaceAll("<encPath>", path ==null ? "<null>" : URLEncoder.encode(trimHeadSlash(path), "UTF-8"));
            s = s.replaceAll("<path>",    path ==null ? "<null>" : trimHeadSlash(path)                            );
            s = s.replaceAll("<prev>",    "" +(rev -1)                                                            );
            s = s.replaceAll("<rev>",     "" +rev                                                                 );
            return s;
        }
    }
}
