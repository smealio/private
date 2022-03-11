package com.myctca.model;

import java.util.List;

public class ExternalLink {
    private String title;
    private String url;
    private boolean isDownload;
    private List<ExternalLink> children;

    public String getTitle() {
        return title;
    }

    public boolean getIsDownload() {
        return isDownload;
    }

    public String getUrl() {
        return url;
    }

    public List<ExternalLink> getChildren() {
        return children;
    }
}
