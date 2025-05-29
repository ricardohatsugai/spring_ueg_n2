package br.rep.trindade.vendas_app.service;

import br.rep.trindade.vendas_app.exception.ResourceNotFoundException;
import br.rep.trindade.vendas_app.model.Cliente;
import br.rep.trindade.vendas_app.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service indica que esta classe é um componente de serviço Spring.
@Service
public class ClienteService {

    // @Autowired injeta uma instância de ClienteRepository.
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Retorna uma lista de todos os clientes.
     * @return Lista de Cliente. */

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    /**
     * Busca um cliente pelo ID.
     * @param id O ID do cliente.
     * @return O cliente encontrado.
     * @throws ResourceNotFoundException Se o cliente não for encontrado. */

    public Cliente findById(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado com o ID: " + id));
    }

    /**
     * Salva um novo cliente ou atualiza um existente.
     * @param cliente O objeto Cliente a ser salvo.
     * @return O cliente salvo. */

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    /**
     * Atualiza os dados de um cliente existente.
     * @param id O ID do cliente a ser atualizado.
     * @param clienteDetails Os novos detalhes do cliente.
     * @return O cliente atualizado.
     * @throws ResourceNotFoundException Se o cliente não for encontrado.
     */
    public Cliente update(Long id, Cliente clienteDetails) {
        Cliente cliente = findById(id); // Lança exceção se não encontrado
        cliente.setNome(clienteDetails.getNome());
        cliente.setCpf(clienteDetails.getCpf());
        cliente.setEndereco(clienteDetails.getEndereco());
        cliente.setTelefone(clienteDetails.getTelefone());
        cliente.setEmail(clienteDetails.getEmail());
        return clienteRepository.save(cliente);
    }

    /**
     * Exclui um cliente pelo ID.
     * @param id O ID do cliente a ser excluído.
     * @throws ResourceNotFoundException Se o cliente não for encontrado.
     */
    public void delete(Long id) {
        Cliente cliente = findById(id); // Lança exceção se não encontrado
        clienteRepository.delete(cliente);
    }
}
