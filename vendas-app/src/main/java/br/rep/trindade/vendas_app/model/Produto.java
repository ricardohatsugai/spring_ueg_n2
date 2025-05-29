package br.rep.trindade.vendas_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "A referência é obrigatória.")
    private String referencia;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    /* @NotNull valida que o campo não é nulo.
     * @PositiveOrZero valida que o número é maior ou igual a zero. */
    @Column(nullable = false)
    @NotNull(message = "A quantidade é obrigatória.")
    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    private Integer quantidade;

    // precision define o número total de dígitos e scale define o número de dígitos após o ponto decimal.
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "O preço é obrigatório.")
    @PositiveOrZero(message = "O preço não pode ser negativo.")
    private BigDecimal preco;
}
