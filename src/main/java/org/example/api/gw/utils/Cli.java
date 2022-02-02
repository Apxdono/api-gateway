package org.example.api.gw.utils;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class Cli {
    private static final Logger LOG = LoggerFactory.getLogger(Cli.class);
    private static final String SERVER_ARG = "start";

    public static Action getAction(String[] args) {
        if (args == null || args.length < 2) {
            printHelpAndExit();
        }
        var actionArg = args[0];
        var action = Action.fromString(actionArg);

        if (action == null) {
            LOG.error("Unrecognised run argument '" + actionArg + "'");
            printHelpAndExit();
        }
        return action;
    }

    /**
     * Converts 2nd run argument into config file URL.
     * MUST BE called after {@link #getAction(String[])}.
     * @param args - to get config file location
     * @return URL of config file
     */
    public static URL toConfigUrl(@NotNull  String[] args) {
        var fileString = args[1];
        if (fileString.startsWith(".")) {
            try {
                return new File(fileString).toURI().toURL();
            }
            catch (MalformedURLException e) {
                LOG.error("Unable to retrieve file '{}'. {}", fileString, e.getMessage());
                throw new RuntimeException("No valid configuration. Cannot proceed");
            }
        }
        else {
            return Cli.class.getClassLoader().getResource(fileString);
        }
    }

    private static void printHelpAndExit() {
        LOG.error("Add help in here");
        System.exit(1);
    }

    public enum Action {
        START_SERVER("start"),
        CHECK_CONFIG("check");

        private final String command;

        Action(String command) {
            this.command = command;
        }

        public boolean isCorrect(String command) {
            return this.command.equals(command);
        }

        public static Action fromString(String command) {
            return Arrays.stream(Action.values()).filter(a -> a.isCorrect(command)).findFirst().orElse(null);
        }
    }
}
