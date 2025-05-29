package br.rep.trindade.vendas_app.service;

import br.rep.trindade.vendas_app.exception.ResourceNotFoundException;
import br.rep.trindade.vendas_app.model.Produto;
import br.rep.trindade.vendas_app.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Produto findById(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com o ID: " + id));
    }

    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto update(Long id, Produto produtoDetails) {
        Produto produto = findById(id); // Lança exceção se não encontrado
        produto.setReferencia(produtoDetails.getReferencia());
        produto.setDescricao(produtoDetails.getDescricao());
        produto.setQuantidade(produtoDetails.getQuantidade());
        produto.setPreco(produtoDetails.getPreco());
        return produtoRepository.save(produto);
    }

    public void delete(Long id) {
        Produto produto = findById(id); // Lança exceção se não encontrado
        produtoRepository.delete(produto);
    }
}
