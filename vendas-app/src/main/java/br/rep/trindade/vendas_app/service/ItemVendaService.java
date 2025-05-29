package br.rep.trindade.vendas_app.service;

import br.rep.trindade.vendas_app.exception.ResourceNotFoundException;
import br.rep.trindade.vendas_app.model.ItemVenda;
import br.rep.trindade.vendas_app.repository.ItemVendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemVendaService {

    @Autowired
    private ItemVendaRepository itemVendaRepository;

    /**
     * Retorna uma lista de todos os itens de venda.
     * @return Lista de ItemVenda.
     */
    public List<ItemVenda> findAll() {
        return itemVendaRepository.findAll();
    }

    /**
     * Busca um item de venda pelo ID.
     * @param id O ID do item de venda.
     * @return O item de venda encontrado.
     * @throws ResourceNotFoundException Se o item de venda não for encontrado.
     */
    public ItemVenda findById(Long id) {
        return itemVendaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item de Venda não encontrado com o ID: " + id));
    }

    /**
     * Salva um novo item de venda ou atualiza um existente.
     * (Geralmente, save/update/delete de ItemVenda são gerenciados via cascata na Venda,
     * mas este método pode ser útil para casos específicos ou testes.)
     * @param itemVenda O objeto ItemVenda a ser salvo.
     * @return O item de venda salvo.
     */
    public ItemVenda save(ItemVenda itemVenda) {
        return itemVendaRepository.save(itemVenda);
    }

    /**
     * Atualiza os dados de um item de venda existente.
     * @param id O ID do item de venda a ser atualizado.
     * @param itemVendaDetails Os novos detalhes do item de venda.
     * @return O item de venda atualizado.
     * @throws ResourceNotFoundException Se o item de venda não for encontrado.
     */
    public ItemVenda update(Long id, ItemVenda itemVendaDetails) {
        ItemVenda itemVenda = findById(id);
        itemVenda.setQuantidade(itemVendaDetails.getQuantidade());
        itemVenda.setValor(itemVendaDetails.getValor());
        itemVenda.setValorTotal(itemVendaDetails.getValorTotal());
        // Alterações nas associações (Produto, Venda) geralmente são feitas no contexto da Venda.
        return itemVendaRepository.save(itemVenda);
    }

    /**
     * Exclui um item de venda pelo ID.
     * @param id O ID do item de venda a ser excluído.
     * @throws ResourceNotFoundException Se o item de venda não for encontrado.
     */
    public void delete(Long id) {
        ItemVenda itemVenda = findById(id);
        itemVendaRepository.delete(itemVenda);
    }
}
