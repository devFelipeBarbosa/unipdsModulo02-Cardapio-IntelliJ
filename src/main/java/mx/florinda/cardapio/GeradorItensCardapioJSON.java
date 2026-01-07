package mx.florinda.cardapio;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class GeradorItensCardapioJSON {

    static void main(String[] args) throws IOException {
        Database database = new Database();
        List<ItemCardapio> itemCardapios = database.listaDeItensCardapio();

        Gson gson = new Gson();
        String json = gson.toJson(itemCardapios);

        Path path = Path.of("itensCardapio.json");
        Files.writeString(path, json);



    }


}
