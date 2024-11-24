package com.enthusiasm.plurecore.config.v2;

public class LumenBuilder {
    private Strategy configStrategy = Strategy.TOML;

    public LumenBuilder() {}

    public LumenBuilder useToml(){
        this.configStrategy = Strategy.TOML;
        return this;
    }

    public LumenBuilder useJson(){
        this.configStrategy = Strategy.JSON;
        return this;
    }

    private enum Strategy {
        TOML,
        JSON
    }
}
