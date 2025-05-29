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

import org.slf4j.Logger; // Importa a classe Logger
import org.slf4j.LoggerFactory; // Importa a classe LoggerFactory

@Service
public class VendaService {

    // Cria uma instância de Logger para esta classe
    private static final Logger logger = LoggerFactory.getLogger(VendaService.class);

    @Autowired
    private VendaRepository vendaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ItemVendaService itemVendaService;

    public List<Venda> findAll() {
        logger.info("Buscando todas as vendas.");
        return vendaRepository.findAll();
    }

    public Venda findById(Long id) {
        logger.info("Buscando venda com ID: {}", id);
        return vendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada com o ID: " + id));
    }

    @Transactional
    public Venda save(Venda venda) {
        logger.info("Iniciando processo de salvamento de venda.");
        logger.debug("Dados da venda recebidos: {}", venda); // Loga os dados completos da venda

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
            logger.debug("Processando {} itens de venda.", venda.getItensVenda().size());
            for (ItemVenda item : venda.getItensVenda()) {
                // Validação adicional para garantir que o produto não é nulo antes de buscar
                if (item.getProduto() == null || item.getProduto().getId() == null) {
                    logger.error("Item de venda com produto nulo ou ID de produto nulo. Item: {}", item);
                    throw new IllegalArgumentException("Produto inválido em um dos itens da venda.");
                }
                Produto produto = produtoRepository.findById(item.getProduto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + item.getProduto().getId()));

                // Verifica se há estoque suficiente
                if (produto.getQuantidade() < item.getQuantidade()) {
                    logger.warn("Estoque insuficiente para o produto: {} (ID: {}). Quantidade solicitada: {}, Estoque disponível: {}",
                            produto.getDescricao(), produto.getId(), item.getQuantidade(), produto.getQuantidade());
                    throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getDescricao());
                }

                item.setProduto(produto);
                item.setVenda(venda); // Garante a ligação com a venda atual
                item.setValor(produto.getPreco()); // Usa o preço atual do produto como valor unitário do item
                item.setValorTotal(item.getValor().multiply(BigDecimal.valueOf(item.getQuantidade())));

                // Atualiza o estoque do produto
                produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
                produtoRepository.save(produto);
                logger.debug("Estoque do produto {} (ID: {}) atualizado para: {}", produto.getDescricao(), produto.getId(), produto.getQuantidade());

                totalVenda = totalVenda.add(item.getValorTotal());
            }
        } else {
            logger.warn("Nenhum item de venda presente na venda.");
        }
        venda.setTotal(totalVenda);
        logger.debug("Total da venda calculado: {}", totalVenda);

        // Salva a venda, e a cascata garantirá que os itens de venda também sejam salvos.
        Venda savedVenda = vendaRepository.save(venda);
        logger.info("Venda salva com sucesso! ID da venda: {}", savedVenda.getId());
        return savedVenda;
    }

    @Transactional
    public Venda update(Long id, Venda vendaDetails) {
        logger.info("Iniciando processo de atualização de venda com ID: {}", id);
        Venda existingVenda = findById(id);
        logger.debug("Venda existente encontrada: {}", existingVenda);

        // Reverte o estoque dos itens antigos da venda antes de processar os novos
        if (existingVenda.getItensVenda() != null) {
            logger.debug("Revertendo estoque para {} itens antigos.", existingVenda.getItensVenda().size());
            existingVenda.getItensVenda().forEach(item -> {
                Produto produto = item.getProduto();
                produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
                produtoRepository.save(produto);
                logger.debug("Estoque do produto {} (ID: {}) revertido para: {}", produto.getDescricao(), produto.getId(), produto.getQuantidade());
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

        // Limpa os itens antigos e adiciona os novos itens de venda
        if (existingVenda.getItensVenda() != null) {
            existingVenda.getItensVenda().clear();
        } else {
            existingVenda.setItensVenda(new java.util.ArrayList<>());
        }

        BigDecimal totalVenda = BigDecimal.ZERO;

        if (vendaDetails.getItensVenda() != null) {
            logger.debug("Processando {} novos itens de venda para atualização.", vendaDetails.getItensVenda().size());
            for (ItemVenda newItem : vendaDetails.getItensVenda()) {
                // Validação adicional para garantir que o produto não é nulo antes de buscar
                if (newItem.getProduto() == null || newItem.getProduto().getId() == null) {
                    logger.error("Novo item de venda com produto nulo ou ID de produto nulo. Item: {}", newItem);
                    throw new IllegalArgumentException("Produto inválido em um dos novos itens da venda.");
                }
                Produto produto = produtoRepository.findById(newItem.getProduto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + newItem.getProduto().getId()));

                if (produto.getQuantidade() < newItem.getQuantidade()) {
                    logger.warn("Estoque insuficiente para o produto: {} (ID: {}). Quantidade solicitada: {}, Estoque disponível: {}",
                            produto.getDescricao(), produto.getId(), newItem.getQuantidade(), produto.getQuantidade());
                    throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getDescricao());
                }

                newItem.setProduto(produto);
                newItem.setVenda(existingVenda); // Garante a ligação com a venda existente
                newItem.setValor(produto.getPreco());
                newItem.setValorTotal(newItem.getValor().multiply(BigDecimal.valueOf(newItem.getQuantidade())));

                // Atualiza o estoque do produto
                produto.setQuantidade(produto.getQuantidade() - newItem.getQuantidade());
                produtoRepository.save(produto);
                logger.debug("Estoque do produto {} (ID: {}) atualizado para: {}", produto.getDescricao(), produto.getId(), produto.getQuantidade());

                totalVenda = totalVenda.add(newItem.getValorTotal());
                existingVenda.getItensVenda().add(newItem);
            }
        } else {
            logger.warn("Nenhum novo item de venda presente na atualização.");
        }
        existingVenda.setTotal(totalVenda);
        logger.debug("Total da venda atualizado: {}", totalVenda);

        Venda updatedVenda = vendaRepository.save(existingVenda);
        logger.info("Venda com ID {} atualizada com sucesso.", updatedVenda.getId());
        return updatedVenda;
    }

    @Transactional
    public void delete(Long id) {
        logger.info("Iniciando processo de exclusão de venda com ID: {}", id);
        Venda venda = findById(id);
        logger.debug("Venda a ser excluída encontrada: {}", venda);

        // Reverte o estoque dos produtos antes de deletar a venda
        if (venda.getItensVenda() != null) {
            logger.debug("Revertendo estoque para {} itens da venda a ser excluída.", venda.getItensVenda().size());
            venda.getItensVenda().forEach(item -> {
                Produto produto = item.getProduto();
                produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
                produtoRepository.save(produto);
                logger.debug("Estoque do produto {} (ID: {}) revertido para: {}", produto.getDescricao(), produto.getId(), produto.getQuantidade());
            });
        }
        vendaRepository.delete(venda);
        logger.info("Venda com ID {} excluída com sucesso.", id);
    }
}
