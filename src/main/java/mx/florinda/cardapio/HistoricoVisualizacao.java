package mx.florinda.cardapio;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HistoricoVisualizacao {

    private final Database database;

    //ItemCardapio => Data e Hora
    private final Map<ItemCardapio, LocalDateTime> visualizacoes = new HashMap<>();


    public HistoricoVisualizacao(Database database) {
        this.database = database;
    }

    public void registrarVisualizacao(Long itemId){
        Optional<ItemCardapio> optionalItemCardapio = database.itemCardapioPorId(itemId);

        if(optionalItemCardapio.isEmpty()){
            System.out.println("Item não encontrado: " + itemId);
            return;
        }else {
            ItemCardapio itemCardapio = optionalItemCardapio.get();
            LocalDateTime agora = LocalDateTime.now();
            visualizacoes.put(itemCardapio, agora);
            System.out.printf("'%s' visualizado em '%s'.\n", itemCardapio.nome(), agora);
        }
    }


    public void mostrarTotalItensVisualizados() {

        System.out.println("Total de itens visualizados: " + visualizacoes.size());
    }

    public void listarVisualizacoes() {
        if(visualizacoes.isEmpty()){
            System.out.println("Nenhum item visualizado.");
            return;
        }else {
            System.out.println("Histórico de Visualizações:");
            visualizacoes.forEach((item, hora)
                    -> System.out.printf("- %s em %s\n", item.nome(), hora));
            System.out.println();
        }
    }
}
