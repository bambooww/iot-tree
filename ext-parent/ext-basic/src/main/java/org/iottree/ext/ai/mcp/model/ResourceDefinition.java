package org.iottree.ext.ai.mcp.model;

public class ResourceDefinition {
    public String uri;
    public String name;
    public String description;
    public String mimeType;

    public ResourceDefinition(String uri, String name, String description, String mimeType) {
        this.uri = uri;
        this.name = name;
        this.description = description;
        this.mimeType = mimeType;
    }
}
