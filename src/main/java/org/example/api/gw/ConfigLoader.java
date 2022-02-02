package org.example.api.gw;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookup;
import org.example.api.gw.config.Config;
import org.example.api.gw.config.ConfigDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.text.lookup.StringLookupFactory.*;

public class ConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Map<String, StringLookup> STRING_LOOKUPS = new HashMap<>();

    static {
        STRING_LOOKUPS.put("env", INSTANCE.environmentVariableStringLookup());
        STRING_LOOKUPS.put("prop", INSTANCE.systemPropertyStringLookup());
    }

    public static Config loadConfig(ObjectMapper mapper, Config owner, URL configFile) {
        try {
            var varsSubstitution = new StringSubstitutor(INSTANCE.interpolatorStringLookup(STRING_LOOKUPS, null, false));
            var content = varsSubstitution.replace(readConfig(configFile));
            var delegateConfig = mapper.readValue(content, ConfigDelegate.class);
            return owner.updateDelegate(delegateConfig);
        }
        catch (JsonMappingException jme) {
            printDeserializationError(configFile, jme);
            System.exit(1);
        }
        catch (IOException | URISyntaxException e) {
            LOG.error("Unable to parse config file", e);
            System.exit(1);
        }
        return null;
    }

    private static String readConfig(URL configFile) throws IOException, URISyntaxException {
        return Files.readString(Paths.get(configFile.toURI()), Charset.defaultCharset());
    }

    private static void printDeserializationError(URL configFile, JsonMappingException jme) {
        LOG.error("configuration error: {} in {} [{}:{}]", jme.getOriginalMessage(), configFile,
                jme.getLocation().getLineNr(), jme.getLocation().getColumnNr());
        LOG.debug("Debug message", jme);
    }
}
