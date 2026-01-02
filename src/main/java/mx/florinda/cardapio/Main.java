package mx.florinda.cardapio;

//https://github.com/unipds-projetos/modulo1-fundamentos-java-extra-cardapio/tree/aula2-aprofundando-em-colecoes

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static void main(String[] args) {

        Database database = new Database();
        List<ItemCardapio> itens = database.listaDeItensCardapio();

        // saber quais as categorias realmente tenho no cardapio
        System.out.println("--------HASHSET/TREESET------");
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


        // saber quantos itens de cada categoria realmente tem no cardapio
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

    }
}

