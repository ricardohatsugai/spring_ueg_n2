package br.rep.trindade.vendas_app.repository;

import br.rep.trindade.vendas_app.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
}
