package com.renaud.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class MainTest {

    /**
     * Credits : https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java/1359700#1359700
     *
     * @param targetURL
     * @param urlParameters
     * @return
     */
    public static String executePost(final String targetURL, String urlParameters) throws IOException {
        HttpURLConnection connection = null;

        try {
            //Create connection
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (final Exception e) {
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Credits : https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java/14016308#14016308
     *
     * @param host
     * @param uri
     * @param port
     * @return
     * @throws IOException
     */
    static String get(final String host, final String uri, final int port) throws IOException {
        final Socket socket = new Socket(host, port);
        final String request = "GET " + uri + " HTTP/1.0\r\n\r\n";
        OutputStream os = socket.getOutputStream();
        os.write(request.getBytes());
        os.flush();

        InputStream is = socket.getInputStream();
        final StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = is.read()) != -1)
            builder.append(((char) ch));
        socket.close();
        return builder.toString();
    }

    @Test
    public void mainTest() throws Exception {
        final String configFile = "conf/config.ini";
        final String[] parameters = {"-config", configFile};

        Main.main(parameters);

        final String response = MainTest.executePost("http://localhost:8000/app", "int1=3&int2=5&limit=16&str1=fizz&str2=buzz");
        final JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        Assert.assertEquals(jsonResponse.toString(), "success", jsonResponse.get("state").getAsString());
        final JsonArray results = jsonResponse.get("results").getAsJsonArray();
        Assert.assertEquals("1,2,fizz,4,buzz,fizz,7,8,fizz,buzz,11,fizz,13,14,fizzbuzz,16", results.get(0).getAsString());


        final String r2 = MainTest.get("localhost", "invalid_url", 8000);
        final String e2 = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 50\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<h1>404 Not Found</h1>No context found for request";
        Assert.assertEquals(e2, r2);
    }
}
