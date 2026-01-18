package mx.florinda.cardapio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorItensCardapioComSocket {
    public static void main(String[] args) throws Exception {

        try (ExecutorService executorService = Executors.newFixedThreadPool(50)) {

            try (ServerSocket serverSocket = new ServerSocket(8000)) {
                System.out.println("Servidor Iniciado!");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(() -> trataRequisicao(clientSocket));

                }
            }
        }
    }

    private static void trataRequisicao(Socket clientSocket) {
        try (clientSocket){

            InputStream clientIS = clientSocket.getInputStream();

            StringBuilder requestBuilder = new StringBuilder();

            int data;

            do {
                data = clientIS.read();
                requestBuilder.append((char) data);

            } while (clientIS.available() > 0);

            String request = requestBuilder.toString();
            System.out.println(request);

            Thread.sleep(250);

            Path path = Path.of("itensCardapio.json");
            String json = Files.readString(path);

            OutputStream clientOS = clientSocket.getOutputStream();
            PrintStream clientOut = new PrintStream(clientOS);

            clientOut.println("HTTP/1.1 200 OK");
            clientOut.println("Content-Type: application/json; charset=UTF-8");
            clientOut.println();
            clientOut.println(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
