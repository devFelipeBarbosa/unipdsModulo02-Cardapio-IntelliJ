package mx.florinda.cardapio;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorItensCardapioComSocket {

    private static final Logger logger = Logger.getLogger(ServidorItensCardapioComSocket.class.getName());
    private static final Database database = new SQLDatabase();

    public static void main(String[] args) throws Exception {

        try (ExecutorService executorService = Executors.newFixedThreadPool(50)) {

            try (ServerSocket serverSocket = new ServerSocket(8000)) {
                logger.info("Servidor Iniciado!");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(() -> trataRequisicao(clientSocket));

                }
            }
        }
    }

    private static void trataRequisicao(Socket clientSocket) {
        try (clientSocket) {

            InputStream clientIS = clientSocket.getInputStream();

            StringBuilder requestBuilder = new StringBuilder();

            int data;

            do {
                data = clientIS.read();
                requestBuilder.append((char) data);

            } while (clientIS.available() > 0);

            String request = requestBuilder.toString();

            logger.finest(request);
            logger.fine("\n\nChegou um novo request");

            Thread.sleep(250);

            String[] requestChunks = request.split("\r\n\r\n");
            String requestLineAndHeaders = requestChunks[0];
            String[] requestLineAndHeadersChunks = requestLineAndHeaders.split("\r\n");
            String requestLine = requestLineAndHeadersChunks[0];
            String[] requestLineChunks = requestLine.split(" ");

            String method = requestLineChunks[0];
            String requestURI = requestLineChunks[1];
            String httpVersion = requestLineChunks[2];

            logger.finer(() -> "Method: " + method);
            logger.finer(() -> "Request URI: " + requestURI);
            logger.finer(() -> "HTTP Version: " + httpVersion);

            Thread.sleep(250);

            OutputStream clientOS = clientSocket.getOutputStream();
            PrintStream clientOut = new PrintStream(clientOS);

            try {

                if ("/itensCardapio.json".equals(requestURI)) {

                    logger.fine("Chamou arquivo itensCardapio.json");

                    Path path = Path.of("itensCardapio.json");
                    String json = Files.readString(path);

                    clientOut.println("HTTP/1.1 200 OK");
                    clientOut.println("Content-Type: application/json; charset=UTF-8");
                    clientOut.println();
                    clientOut.println(json);

                } else if ("GET".equals(method) && "/itens-cardapio".equals(requestURI)) {

                    logger.fine("Chamou listagem de itens de Cardapio");
                    List<ItemCardapio> ListaItensCardapios = database.listaDeItensCardapio();

                    Gson gson = new Gson();
                    String json = gson.toJson(ListaItensCardapios);

                    clientOut.println("HTTP/1.1 200 OK");
                    clientOut.println("Content-Type: application/json; charset=UTF-8");
                    clientOut.println();
                    clientOut.println(json);

                } else if ("GET".equals(method) && "/itens-cardapio/total".equals(requestURI)) {

                    logger.fine("Chamou total de itens de Cardapio");
                    int totalItens = database.totalItensCardapio();

                    clientOut.println("HTTP/1.1 200 OK");
                    clientOut.println();
                    clientOut.println(totalItens);

                } else if ("POST".equals(method) && "/itens-cardapio".equals(requestURI)) {

                    logger.fine("Chamou adição de item de Cardapio");

                    //curl -v -X POST -d '{"id":20,"nome":"Item 20","descricao":"Item 20.","categoria":"BEBIDAS","preco":2.99}' -H 'Content-Type: application/json' http://localhost:8000/itens-cardapio

                    if (requestChunks.length == 1) {
                        clientOut.println("HTTP/1.1 400 Bad Request");
                        return;
                    }

                    String body = requestChunks[1];

                    Gson gson = new Gson();
                    ItemCardapio novoItemCardapio = gson.fromJson(body, ItemCardapio.class);

                    database.adicionaItemCardapio(novoItemCardapio);
                    clientOut.println("HTTP/1.1 201 Created");

                } else {
                    logger.warning(() -> "URI não encontrada: " + requestURI);
                    clientOut.println("HTTP/1.1 404 Not Found");
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex, () -> "Erro ao tratar " + method + " " + requestURI);

                clientOut.println("HTTP/1.1 500 Internal Server Error");
                clientOut.println();
                clientOut.println(ex.getMessage());
            }

        } catch (Exception ex) {
            //logger.severe("Erro no servidor" + ex.getMessage());
            logger.log(Level.SEVERE, "Erro no servidor", ex);
            throw new RuntimeException(ex);
        }
    }
}
