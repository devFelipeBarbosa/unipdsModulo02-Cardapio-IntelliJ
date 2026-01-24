package mx.florinda.cardapio;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorItensCardapioComSocket {
    private static final Database database = new Database();

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
            System.out.println("------------------------");
            System.out.println(request);
            System.out.println("\n\nChegou um novo request");

            Thread.sleep(250);

            String[] requestChunks = request.split("\r\n\r\n");
            String requestLineAndHeaders = requestChunks[0];
            String[] requestLineAndHeadersChunks = requestLineAndHeaders.split("\r\n");
            String requestLine = requestLineAndHeadersChunks[0];
            String[] requestLineChunks = requestLine.split(" ");

            String method = requestLineChunks[0];
            String requestURI = requestLineChunks[1];

            System.out.println(method);
            System.out.println(requestURI);

            OutputStream clientOS = clientSocket.getOutputStream();
            PrintStream clientOut = new PrintStream(clientOS);

            if(method.equals("GET") && requestURI.equals("/itensCardapio.json")) {

                System.out.println("Chamou arquivo Json");

                Path path = Path.of("itensCardapio.json");
                String json = Files.readString(path);

                clientOut.println("HTTP/1.1 200 OK");
                clientOut.println("Content-Type: application/json; charset=UTF-8");
                clientOut.println();
                clientOut.println(json);

            } else if (method.equals("GET") && requestURI.equals("/itens-cardapio")){

                System.out.println("Chamou listagem de itens de Cardapio");

                List<ItemCardapio> ListaItensCardapios = database.listaDeItensCardapio();

                Gson gson = new Gson();
                String json = gson.toJson(ListaItensCardapios);

                clientOut.println("HTTP/1.1 200 OK");
                clientOut.println("Content-Type: application/json; charset=UTF-8");
                clientOut.println();
                clientOut.println(json);

            } else if (method.equals("GET") && requestURI.equals("/itens-cardapio/total")){

                System.out.println("Chamou total de itens de Cardapio");

                List<ItemCardapio> ListaItensCardapios = database.listaDeItensCardapio();


                clientOut.println("HTTP/1.1 200 OK");
                clientOut.println("Content-Type: application/json; charset=UTF-8");
                clientOut.println();
                clientOut.println(ListaItensCardapios.size());

            } else if (method.equals("POST") && requestURI.equals("/itens-cardapio")){

                System.out.println("Chamou adição de item de Cardapio");

                //curl -v -X POST -d '{"id":20,"nome":"Item 20","descricao":"Item 20.","categoria":"BEBIDAS","preco":2.99}' -H 'Content-Type: application/json' http://localhost:8000/itens-cardapio

                if (requestChunks.length == 1){
                    clientOut.println("HTTP/1.1 400 Bad Request");
                    return;
                }

                String body = requestChunks[1];
                Gson gson = new Gson();
                ItemCardapio novoItemCardapio = gson.fromJson(body, ItemCardapio.class);
                database.adicionaItemCardapio(novoItemCardapio);

                clientOut.println("HTTP/1.1 201 Created");


            } else {
                System.out.println("URI não encontrada: " + requestURI);
                clientOut.println("HTTP/1.1 404 Not Found");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
