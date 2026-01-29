package mx.florinda.cardapio;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLDatabase implements Database {

    @Override
    public List<ItemCardapio> listaDeItensCardapio() {

        List<ItemCardapio> itens = new ArrayList<>();

        String sql = "SELECT id, nome, descricao, categoria, preco, preco_promocional FROM item_cardapio";

        try (Connection connection =
                     DriverManager.getConnection("jdbc:mysql://localhost:3306/cardapio", "root", "121484");

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                String categoriaStr = rs.getString("categoria");
                BigDecimal preco = rs.getBigDecimal("preco");
                BigDecimal precoPromocional = rs.getBigDecimal("preco_promocional");

                ItemCardapio.CategoriaCardapio categoria = ItemCardapio.CategoriaCardapio.valueOf(categoriaStr);

                var itemCardapio = new ItemCardapio(id, nome, descricao, categoria, preco, precoPromocional);
                itens.add(itemCardapio);

            }

            return itens;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int totalItensCardapio() {
        String sql = "SELECT COUNT(*) FROM item_cardapio;";

        try (Connection connection =
                     DriverManager.getConnection("jdbc:mysql://localhost:3306/cardapio", "root", "121484");

             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int total = 0;

            while (rs.next()) {
                total = rs.getInt(1);

            }

            return total;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void adicionaItemCardapio(ItemCardapio itemCardapio) {
        String sql = "INSERT INTO item_cardapio (id, nome, descricao, categoria, preco, preco_promocional) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection =
                     DriverManager.getConnection("jdbc:mysql://localhost:3306/cardapio", "root", "121484");

             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, itemCardapio.id());
            ps.setString(2, itemCardapio.nome());
            ps.setString(3, itemCardapio.descricao());
            ps.setString(4, itemCardapio.categoria().name());
            ps.setBigDecimal(5, itemCardapio.preco());
            ps.setBigDecimal(6, itemCardapio.precoComDesconto());
            ps.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ItemCardapio> itemCardapioPorId(Long itemId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean removerItemCardapio(Long itemId) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public boolean alterarPrecoItemCardapio(Long itemId, BigDecimal novoPreco) {
        throw new UnsupportedOperationException("TODO");
    }


}
