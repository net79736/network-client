package org.httptest.networkclient.dto;

public enum ConfigLoadType {
    Unknown,
    Classpath,
    Filepath,
    ServletContext,
    Classes;

    private ConfigLoadType() {
    }
}
