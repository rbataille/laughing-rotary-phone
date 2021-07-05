package com.renaud.test;

import com.renaud.test.handlers.AppHandler;
import com.renaud.test.handlers.MetricsHandler;
import com.renaud.test.server.RouteHandler;
import com.renaud.test.server.config.ConfigFile;
import com.renaud.test.server.config.EnumConfig;
import com.renaud.test.server.storage.FileStorage;
import com.renaud.test.server.storage.IStorage;
import com.renaud.test.server.storage.InMemoryStorage;
import com.renaud.test.server.storage.SqliteStorage;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
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
     * The service executor of the HttpServer
     */
    private final ExecutorService executorService;

    /**
     *
     */
    private final IStorage storage;

    /**
     * Basic constructor.
     * Create all needed objects.
     *
     * @param aCommandLine The command line object, used to retrieve some arguments.
     */
    public Application(final CommandLine aCommandLine) {
        this(new ConfigFile(aCommandLine.getOptionValue("config", "")), null);
    }

    public Application(final String[] args) throws ParseException {
        this(Application.parseCommandLine(args));
    }



    static CommandLine parseCommandLine(final String[] args) throws ParseException {
        final Options options = new Options();
        options.addOption(new Option("config", true, "The config file path"));

        final CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    Application(final ConfigFile configFile, final IStorage storage){
        this.configFile = configFile;
        LOG.debug("Start to read configuration file");
        this.configFile.processRead();
        this.executorService = Executors.newCachedThreadPool();
        if(storage == null) {
            if(EnumConfig.EnumStorageType.SQLITE.toString().equals(this.configFile.getString(EnumConfig.STORAGE_TYPE))){
                this.storage = new SqliteStorage(this.configFile);
            } else if(EnumConfig.EnumStorageType.FILE.toString().equals(this.configFile.getString(EnumConfig.STORAGE_TYPE))){
                final String storageFilePath = this.configFile.getString(EnumConfig.STORAGE_FILE);
                this.storage = new FileStorage(storageFilePath);
            } else {
                this.storage = new InMemoryStorage();
            }
            LOG.debug("Initialize sqlite storage");
        } else {
            this.storage = storage;
        }
    }

    /**
     * Launch the http server and registers all handlers.
     * @see com.renaud.test.server.AbstractHandler
     * @throws IOException if the Http server cant be created.
     */
    public void run() throws IOException {
        final int targetPort = this.configFile.getInt(EnumConfig.SERVER_PORT);
        LOG.debug("Start HTTP server, port: {}", targetPort);

        final HttpServer server = HttpServer.create(new InetSocketAddress(targetPort), 0);

        final RouteHandler routeHandler = new RouteHandler();
        // Endpoints
        // App
        routeHandler.registerController(new AppHandler(this.storage, "/app"));
        // Metrics
        routeHandler.registerController(new MetricsHandler(this.storage, "/metrics"));

        server.createContext("/", routeHandler);
        server.setExecutor(this.executorService);
        server.start();
    }

    IStorage getStorage(){
        return this.storage;
    }
}
