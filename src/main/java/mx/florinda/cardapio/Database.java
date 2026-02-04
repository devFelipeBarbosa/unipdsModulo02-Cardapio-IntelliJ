package mx.florinda.cardapio;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface Database {
    List<ItemCardapio> listaDeItensCardapio();

    Optional<ItemCardapio> itemCardapioPorId(Long itemId);

    boolean removerItemCardapio(Long itemId);

    boolean alterarPrecoItemCardapio(Long itemId, BigDecimal novoPreco);

    int totalItensCardapio();

    void adicionaItemCardapio(ItemCardapio itemCardapio);
}
