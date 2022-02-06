package com.example.mobileappws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Class used to read properties from the file
 */
@Component
public class AppProperties {

    @Autowired
    private Environment env;

    public String getTokenSecret() {
        return env.getProperty("tokenSecret");
    }
}
