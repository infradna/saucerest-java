package com.saucelabs.rest;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class UpdateJob {

    public String name;
    @JsonProperty("public") public boolean _public;
    public List<String> tags;
    public int build;
    public boolean passed;
    @JsonProperty("custom-data") public Map<String, Object> customData;

    public UpdateJob(String name, boolean _public, List<String> tags, int build, boolean passed, Map<String, Object> customData) {
        this.name = name;
        this._public = _public;
        this.tags = tags;
        this.build = build;
        this.passed = passed;
        this.customData = customData;
    }

    public UpdateJob() {};

    @Override
    public String toString() {
        return "UpdateJob{" +
                "name='" + name + '\'' +
                ", public=" + _public +
                ", tags=" + tags +
                ", build=" + build +
                ", passed=" + passed +
                ", customData=" + customData +
                '}';
    }
}
