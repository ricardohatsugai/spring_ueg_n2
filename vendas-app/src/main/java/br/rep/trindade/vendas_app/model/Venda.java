package br.rep.trindade.vendas_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
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

    /* @ManyToOne indica um relacionamento muitos-para-um (muitas vendas para um cliente).
     * @JoinColumn especifica a coluna de junção (chave estrangeira). */
    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "O cliente é obrigatório para a venda.")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_pagamento", nullable = false)
    @NotNull(message = "O método de pagamento é obrigatório para a venda.")
    private Pagamento pagamento;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "O total da venda é obrigatório.")
    @PositiveOrZero(message = "O total da venda não pode ser negativo.")
    private BigDecimal total;

    @Column(length = 500)
    private String observacao;

    /* @OneToMany indica um relacionamento um-para-muitos (uma venda tem muitos itens de venda).
     * mappedBy aponta para o campo que possui o relacionamento na entidade ItemVenda.
     * CascadeType.ALL significa que operações (persist, merge, remove) em Venda serão propagadas para ItemVenda.
     * orphanRemoval = true remove itens de venda órfãos (que não estão mais associados a uma venda). */
    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itensVenda;
}
