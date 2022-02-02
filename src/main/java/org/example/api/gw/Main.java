package org.example.api.gw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import okhttp3.HttpUrl;
import org.example.api.gw.config.Config;
import org.example.api.gw.jackson.HttpUrlDeserializer;
import org.example.api.gw.utils.Cli;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Cli.Action action = Cli.getAction(args);
        var configFile = Cli.toConfigUrl(args);
        var config = Config.prepare();
        var mapper = initJsonMapper();
        Supplier<Config> configSupplier = () -> ConfigLoader.loadConfig(mapper, config, configFile);

        if (Cli.Action.CHECK_CONFIG == action) {
            LOG.info("Checking configuration");
            configSupplier.get();
            LOG.info("Config file is valid");
        }
        if (Cli.Action.START_SERVER == action) {
            LOG.info("Starting application");
            Entrypoint.runServer(mapper, configSupplier.get());
        }

    }

    private static ObjectMapper initJsonMapper() {
        var ymlMapper = new YAMLFactory();
        var objectMapper = new ObjectMapper(ymlMapper);
        var module = new SimpleModule();
        module.addDeserializer(HttpUrl.class, new HttpUrlDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
