package com.renaud.larp;

import com.renaud.larp.handlers.AppHandler;
import com.renaud.larp.handlers.MetricsHandler;
import com.renaud.larp.handlers.StatsHandler;
import com.renaud.larp.server.RouteHandler;
import com.renaud.larp.server.config.ConfigFile;
import com.renaud.larp.server.config.EnumConfig;
import com.renaud.larp.server.storage.FileStorage;
import com.renaud.larp.server.storage.AbstractStorage;
import com.renaud.larp.server.storage.InMemoryStorage;
import com.renaud.larp.server.storage.SqliteStorage;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * The application object.
 */
public class Application {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    /**
     * The config file.
     */
    private final ConfigFile configFile;

    /**
     *
     */
    private final AbstractStorage storage;

    /**
     * Basic constructor.
     * @param args some command line arguments.
     * @throws ParseException If something goes wrong while parsing command line arguments.
     */
    public Application(final String[] args) throws ParseException {
        this(new ConfigFile(parseCommandLine(args).getOptionValue("config", "")), null);
    }

    /**
     * Create CommandLine object from array of Strings.
     * @param args Some args.
     * @return A CommandLine object.
     * @throws ParseException If something goes wrong while parsing command line arguments.
     */
    static CommandLine parseCommandLine(final String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption(new Option("config", true, "The config file path"));

        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    /**
     * Read config file and initialize storage.
     *
     * @param configFile The incoming config file.
     * @param storage A storage object (can be null)
     */
    Application(final ConfigFile configFile, final AbstractStorage storage){
        this.configFile = configFile;
        LOG.debug("Start to read configuration file");
        this.configFile.processRead();
        if(storage == null) {
            if(EnumConfig.EnumStorageType.SQLITE.toString().equals(this.configFile.getString(EnumConfig.STORAGE_TYPE))){
                // We use the sqlite mode.
                this.storage = new SqliteStorage(this.configFile);
            } else if(EnumConfig.EnumStorageType.FILE.toString().equals(this.configFile.getString(EnumConfig.STORAGE_TYPE))){
                // Here, we use the file mode
                final String storageFilePath = this.configFile.getString(EnumConfig.STORAGE_FILE);
                this.storage = new FileStorage(storageFilePath);
            } else {
                // By default, we use the InMemory mode
                this.storage = new InMemoryStorage();
            }
        } else {
            this.storage = storage;
        }
    }

    /**
     * Launch the http server and registers all handlers.
     * @see com.renaud.larp.server.AbstractHandler
     * @throws IOException if the Http server cant be created.
     */
    public void run() throws IOException {
        final int targetPort = this.configFile.getInt(EnumConfig.SERVER_PORT);
        LOG.debug("Start HTTP server on port '{}'", targetPort);
        final HttpServer server = HttpServer.create(new InetSocketAddress(targetPort), 0);

        final RouteHandler routeHandler = new RouteHandler();
        // App endpoint
        routeHandler.registerHandler(new AppHandler(this.storage, "/app"));
        // Metrics endpoint
        routeHandler.registerHandler(new MetricsHandler(this.storage, "/metrics"));
        // Stats endpoint
        routeHandler.registerHandler(new StatsHandler(this.storage, "/stats"));

        server.createContext("/", routeHandler);
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    /**
     * Used by tests.
     * @return Selected storage object.
     */
    AbstractStorage getStorage(){
        return this.storage;
    }
}
