package br.rep.trindade.vendas_app.repository;

import br.rep.trindade.vendas_app.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* @Repository indica que esta interface é um componente de repositório Spring.
 * Estende JpaRepository para fornecer operações CRUD básicas para a entidade Cliente.
 * JpaRepository<T, ID> onde T é a entidade e ID é o tipo da chave primária. */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
