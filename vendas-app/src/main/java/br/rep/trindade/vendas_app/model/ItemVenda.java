package br.rep.trindade.vendas_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_vendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Adicionando um ID para a tabela de junção, é uma boa prática para gerenciar itens individualmente.

    @ManyToOne
    @JoinColumn(name = "id_venda", nullable = false)
    private Venda venda; // Relacionamento com a Venda a qual este item pertence.

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    @NotNull(message = "O produto é obrigatório para o item de venda.")
    private Produto produto; // Relacionamento com o Produto vendido.

    @Column(nullable = false)
    @NotNull(message = "A quantidade do item é obrigatória.")
    @Positive(message = "A quantidade do item deve ser positiva.")
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "O valor unitário do item é obrigatório.")
    @PositiveOrZero(message = "O valor unitário não pode ser negativo.")
    private BigDecimal valor; // Valor unitário do produto no momento da venda (pode ser diferente do preço atual do produto).

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "O valor total do item é obrigatório.")
    @PositiveOrZero(message = "O valor total do item não pode ser negativo.")
    private BigDecimal valorTotal; // Quantidade * Valor unitário.
}
