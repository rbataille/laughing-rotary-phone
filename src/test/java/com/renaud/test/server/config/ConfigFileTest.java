package com.renaud.test.server.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ConfigFileTest {

    @Test
    public void testConfigFile(){
        final Map<String, String> properties = new HashMap<>();
        properties.put("key1", "az");
        final ConfigFile configFile = new ConfigFile(properties);
        Assert.assertEquals(-1, configFile.getInt("key1", -1));
        Assert.assertNull(configFile.getString("key2", null));
    }
}
