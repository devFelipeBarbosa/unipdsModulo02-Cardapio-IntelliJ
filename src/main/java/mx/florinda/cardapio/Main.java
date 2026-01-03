package mx.florinda.cardapio;

//https://github.com/unipds-projetos/modulo1-fundamentos-java-extra-cardapio/tree/aula2-aprofundando-em-colecoes

import java.util.*;
import java.util.stream.Collectors;

import static mx.florinda.cardapio.ItemCardapio.CategoriaCardapio.*;

public class Main {
    static void main(String[] args) {

        Database database = new Database();
        List<ItemCardapio> itens = database.listaDeItensCardapio();

        System.out.println("1 - Saber quais as categorias realmente tenho no cardapio:");
        System.out.println("\n--------HASHSET/TREESET------");
        Comparator<ItemCardapio.CategoriaCardapio> compadorPorNome = Comparator.comparing(ItemCardapio.CategoriaCardapio::name);
        Set<ItemCardapio.CategoriaCardapio> categorias = new TreeSet<>(compadorPorNome);
        for (ItemCardapio item : itens) {
            ItemCardapio.CategoriaCardapio categoria = item.categoria();
            categorias.add(categoria);
        }

        for (ItemCardapio.CategoriaCardapio categoria : categorias) {
            System.out.println(categoria);
        }

        System.out.println("--------Stream/Map------");
        itens.stream()
                .map(ItemCardapio::categoria)
                .collect(Collectors.toCollection(()-> new TreeSet<>(compadorPorNome)))
                .forEach(System.out::println);

        System.out.println("\n2 - Saber quantos itens de cada categoria realmente tem no cardapio");
        // Categoria => Quantidade
        System.out.println("\n--------HashMap------");
        Map<ItemCardapio.CategoriaCardapio, Integer> itensPorCategoria = new TreeMap<>();
        for (ItemCardapio item : itens) {
            ItemCardapio.CategoriaCardapio categoria = item.categoria();
            if(!itensPorCategoria.containsKey(categoria)){
                itensPorCategoria.put(categoria, 1);
            } else {
                Integer quantidadeAnterior = itensPorCategoria.get(categoria);
                itensPorCategoria.put(categoria, quantidadeAnterior + 1);
            }
        }

        //TreeMap ou TreeSet segue a ordem para ordenar de forma natural da variável
        System.out.println(itensPorCategoria);

        System.out.println("--------Stream------");
        itens.stream()
                .collect(Collectors.groupingBy(ItemCardapio::categoria, LinkedHashMap::new, Collectors.counting()))
                .forEach((chave, valor) -> System.out.println(chave + " => " + valor));

        itens.forEach(System.out::println);

        System.out.println("--------------");
        Optional<ItemCardapio> optionalItem = database.itemCardapioPorId(5l);
        String mensagem = optionalItem.map(ItemCardapio::toString).orElse("Não encontrado!");
        System.out.println(mensagem);

        System.out.println("\n3 - Precisa Manter as categorias que estão em promoção:");

        System.out.println("------TreeSet--------");
        Set<ItemCardapio.CategoriaCardapio> categoriasEmPromocao = new TreeSet<>();
        categoriasEmPromocao.add(SOBREMESAS);
        categoriasEmPromocao.add(ENTRADAS);
        categoriasEmPromocao.forEach(System.out::println);

        System.out.println("------SetOf--------");
        Set<ItemCardapio.CategoriaCardapio> categoriasEmPromocao2 = Set.of(SOBREMESAS, ENTRADAS);
        categoriasEmPromocao2.forEach(System.out::println);

        System.out.println("------EnumSet--------");
        Set<ItemCardapio.CategoriaCardapio> categoriasEmPromocao3 = EnumSet.of(SOBREMESAS, ENTRADAS);
        categoriasEmPromocao3.forEach(System.out::println);

        System.out.println("\n4 - Preciso incluir Descricoes associadas as categorias em promocao:");
        System.out.println("------EnumMap--------");
        Map<ItemCardapio.CategoriaCardapio, String> promocoes = new EnumMap<>(ItemCardapio.CategoriaCardapio.class);
        promocoes.put(SOBREMESAS, "O doce perfeito para você!");
        promocoes.put(ENTRADAS, "Comece sua refeição com um toque de sabor!");
        System.out.println(promocoes);

        System.out.println("\n5 - Preciso de um histórico de visualização do cardápio:");
        HistoricoVisualizacao historico = new HistoricoVisualizacao(database);
        historico.registrarVisualizacao(5l);
        historico.registrarVisualizacao(1L);
        historico.registrarVisualizacao(4L);
        historico.registrarVisualizacao(6L);

        System.out.println("\n-------Visualizacoes-------");
        historico.mostrarTotalItensVisualizados();
        historico.listarVisualizacoes();



    }
}

