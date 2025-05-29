package br.rep.trindade.vendas_app.service;

import br.rep.trindade.vendas_app.exception.ResourceNotFoundException;
import br.rep.trindade.vendas_app.model.Pagamento;
import br.rep.trindade.vendas_app.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    public List<Pagamento> findAll() {
        return pagamentoRepository.findAll();
    }

    public Pagamento findById(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com o ID: " + id));
    }

    public Pagamento save(Pagamento pagamento) {
        return pagamentoRepository.save(pagamento);
    }

    public Pagamento update(Long id, Pagamento pagamentoDetails) {
        Pagamento pagamento = findById(id); // Lança exceção se não encontrado
        pagamento.setPagamento(pagamentoDetails.getPagamento());
        return pagamentoRepository.save(pagamento);
    }

    public void delete(Long id) {
        Pagamento pagamento = findById(id); // Lança exceção se não encontrado
        pagamentoRepository.delete(pagamento);
    }
}
