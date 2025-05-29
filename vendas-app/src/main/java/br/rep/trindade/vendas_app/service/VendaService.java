package br.rep.trindade.vendas_app.service;

import br.rep.trindade.vendas_app.exception.ResourceNotFoundException;
import br.rep.trindade.vendas_app.model.*;
import br.rep.trindade.vendas_app.repository.VendaRepository;
import br.rep.trindade.vendas_app.repository.ClienteRepository;
import br.rep.trindade.vendas_app.repository.PagamentoRepository;
import br.rep.trindade.vendas_app.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    // O ItemVendaService é injetado, mas as operações de save/update/delete de ItemVenda
    // são geralmente gerenciadas via cascata na Venda.
    @Autowired
    private ItemVendaService itemVendaService;

    /**
     * Retorna uma lista de todas as vendas.
     * @return Lista de Venda.
     */
    public List<Venda> findAll() {
        return vendaRepository.findAll();
    }

    /**
     * Busca uma venda pelo ID.
     * @param id O ID da venda.
     * @return A venda encontrada.
     * @throws ResourceNotFoundException Se a venda não for encontrada.
     */
    public Venda findById(Long id) {
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com o ID: " + id));
    }

    /**
     * Salva uma nova venda, gerenciando o estoque dos produtos e calculando o total.
     * @param venda O objeto Venda a ser salvo.
     * @return A venda salva.
     * @throws ResourceNotFoundException Se o cliente ou pagamento não forem encontrados.
     * @throws IllegalArgumentException Se o estoque de um produto for insuficiente.
     */
    @Transactional // Garante que todas as operações dentro do método sejam uma única transação de banco de dados.
    public Venda save(Venda venda) {
        // Garante que cliente e pagamento existem no banco de dados
        Cliente cliente = clienteRepository.findById(venda.getCliente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + venda.getCliente().getId()));
        Pagamento pagamento = pagamentoRepository.findById(venda.getPagamento().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com o ID: " + venda.getPagamento().getId()));

        venda.setCliente(cliente);
        venda.setPagamento(pagamento);
        venda.setData(LocalDate.now()); // Define a data atual para a venda

        BigDecimal totalVenda = BigDecimal.ZERO;

        // Verifica e processa os itens de venda, atualizando o estoque e calculando valores
        if (venda.getItensVenda() != null) {
            for (ItemVenda item : venda.getItensVenda()) {
                Produto produto = produtoRepository.findById(item.getProduto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + item.getProduto().getId()));

                // Verifica se há estoque suficiente
                if (produto.getQuantidade() < item.getQuantidade()) {
                    throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getDescricao());
                }

                item.setProduto(produto);
                item.setVenda(venda); // Garante a ligação com a venda atual
                item.setValor(produto.getPreco()); // Usa o preço atual do produto como valor unitário do item
                item.setValorTotal(item.getValor().multiply(BigDecimal.valueOf(item.getQuantidade())));

                // Atualiza o estoque do produto
                produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
                produtoRepository.save(produto);

                totalVenda = totalVenda.add(item.getValorTotal());
            }
        }
        venda.setTotal(totalVenda);

        // Salva a venda, e a cascata garantirá que os itens de venda também sejam salvos.
        return vendaRepository.save(venda);
    }

    /**
     * Atualiza os dados de uma venda existente, incluindo a reversão e atualização do estoque.
     * @param id O ID da venda a ser atualizada.
     * @param vendaDetails Os novos detalhes da venda.
     * @return A venda atualizada.
     * @throws ResourceNotFoundException Se a venda, cliente ou pagamento não forem encontrados.
     * @throws IllegalArgumentException Se o estoque de um produto for insuficiente.
     */
    @Transactional
    public Venda update(Long id, Venda vendaDetails) {
        Venda existingVenda = findById(id);

        // Reverte o estoque dos itens antigos da venda antes de processar os novos
        if (existingVenda.getItensVenda() != null) {
            existingVenda.getItensVenda().forEach(item -> {
                Produto produto = item.getProduto();
                produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
                produtoRepository.save(produto);
            });
        }

        // Atualiza cliente e pagamento da venda
        Cliente cliente = clienteRepository.findById(vendaDetails.getCliente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + vendaDetails.getCliente().getId()));
        Pagamento pagamento = pagamentoRepository.findById(vendaDetails.getPagamento().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com o ID: " + vendaDetails.getPagamento().getId()));

        existingVenda.setCliente(cliente);
        existingVenda.setPagamento(pagamento);
        existingVenda.setObservacao(vendaDetails.getObservacao());
        // A data da venda geralmente não é alterada em uma atualização, mas pode ser adicionada se necessário.

        // Limpa os itens antigos e adiciona os novos itens de venda
        if (existingVenda.getItensVenda() != null) {
            existingVenda.getItensVenda().clear();
        } else {
            existingVenda.setItensVenda(new java.util.ArrayList<>());
        }

        BigDecimal totalVenda = BigDecimal.ZERO;

        if (vendaDetails.getItensVenda() != null) {
            for (ItemVenda newItem : vendaDetails.getItensVenda()) {
                Produto produto = produtoRepository.findById(newItem.getProduto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + newItem.getProduto().getId()));

                // Verifica estoque para os novos itens
                if (produto.getQuantidade() < newItem.getQuantidade()) {
                    throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getDescricao());
                }

                newItem.setProduto(produto);
                newItem.setVenda(existingVenda); // Garante a ligação com a venda existente
                newItem.setValor(produto.getPreco());
                newItem.setValorTotal(newItem.getValor().multiply(BigDecimal.valueOf(newItem.getQuantidade())));

                // Atualiza o estoque do produto
                produto.setQuantidade(produto.getQuantidade() - newItem.getQuantidade());
                produtoRepository.save(produto);

                totalVenda = totalVenda.add(newItem.getValorTotal());
                existingVenda.getItensVenda().add(newItem);
            }
        }
        existingVenda.setTotal(totalVenda);

        return vendaRepository.save(existingVenda);
    }

    /**
     * Exclui uma venda pelo ID, revertendo o estoque dos produtos.
     * @param id O ID da venda a ser excluída.
     * @throws ResourceNotFoundException Se a venda não for encontrada.
     */
    @Transactional
    public void delete(Long id) {
        Venda venda = findById(id);

        // Reverte o estoque dos produtos antes de deletar a venda
        if (venda.getItensVenda() != null) {
            venda.getItensVenda().forEach(item -> {
                Produto produto = item.getProduto();
                produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
                produtoRepository.save(produto);
            });
        }
        vendaRepository.delete(venda);
    }
}
