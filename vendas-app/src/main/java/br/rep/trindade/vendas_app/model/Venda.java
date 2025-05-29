package br.rep.trindade.vendas_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; import jakarta.validation.constraints.NotNull; // Removido, pois o total é calculado
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "vendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "O cliente é obrigatório para a venda.")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_pagamento", nullable = false)
    @NotNull(message = "O método de pagamento é obrigatório para a venda.")
    private Pagamento pagamento;

    @Column(nullable = false, precision = 10, scale = 2)
    // @NotNull(message = "O total da venda é obrigatório.") // REMOVIDO: O total é calculado no serviço, não é um campo de entrada direta do formulário.
    @PositiveOrZero(message = "O total da venda não pode ser negativo.")
    private BigDecimal total;

    @Column(length = 500)
    private String observacao;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itensVenda;
}
