package mx.florinda.cardapio;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ClienteItensCardapio {

    static void main(String[] args) throws Exception {

        System.out.println("1 - Usando URL:");
        URL url = new URL("https://viacep.com.br/ws/01001000/json/");

        try (Scanner scanner = new Scanner(url.openStream())) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                System.out.println(line);
            }
        }

        System.out.println("2 - Usando URI:");
        //URI uri = URI.create("https://viacep.com.br/ws/01001000/json/"); --> Utilizando ViaCEP Link Externo

        URI uri = URI.create("http://localhost:8000/itensCardapio.json"); // Utilizando Servidor Local

        try(HttpClient httpClient = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder(uri).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();
            String body = httpResponse.body();
            System.out.println("Statuscode: " + statusCode);
            System.out.println(body);

        }

    }
}
