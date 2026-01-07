package mx.florinda.cardapio;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServidorItensCardapio {

    public static void main(String[] args) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(8000);
        HttpServer httpServer = HttpServer.create(inetSocketAddress, 0);

        httpServer.createContext("/itensCardapio.json", exchange -> {
            try {
                Path path = Path.of("itensCardapio.json");
                String json = Files.readString(path);
                byte[] bytes = json.getBytes();

                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, bytes.length);

                OutputStream responseBody = exchange.getResponseBody();
                responseBody.write(bytes);
                responseBody.close();

            } catch (IOException e) {
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().close();
                e.printStackTrace();
            }
        });

        System.out.println("Subiu servidor http!");
        httpServer.start();
    }
}
