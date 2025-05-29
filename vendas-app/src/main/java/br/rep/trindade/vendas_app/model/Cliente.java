package br.rep.trindade.vendas_app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

// Anotação @Entity indica que esta classe é uma entidade JPA e será mapeada para uma tabela no banco de dados.
@Entity
// Anotação @Table especifica o nome da tabela no banco de dados.
@Table(name = "clientes")
// @Data do Lombok gera getters, setters, toString, equals e hashCode automaticamente.
@Data
// @NoArgsConstructor do Lombok gera um construtor sem argumentos.
@NoArgsConstructor
// @AllArgsConstructor do Lombok gera um construtor com todos os argumentos.
@AllArgsConstructor
public class Cliente {

    // @Id indica que este campo é a chave primária da entidade.
    @Id
    /* @GeneratedValue configura a estratégia de geração de valores para a chave primária.
    /* IDENTITY usa uma coluna de auto-incremento do banco de dados. */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* @Column especifica detalhes da coluna no banco de dados.
     * nullable = false indica que a coluna não pode ser nula.
     * length define o tamanho máximo da string.
     * @NotBlank valida que o campo não é nulo nem vazio (após trim). */
    @Column(nullable = false, length = 100)
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    /* unique = true indica que os valores nesta coluna devem ser únicos.
     * @CPF do Hibernate Validator valida o formato do CPF. */
    @Column(nullable = false, unique = true, length = 14)
    @CPF(message = "CPF inválido.")
    @NotBlank(message = "O CPF é obrigatório.")
    private String cpf;

    @Column(length = 255)
    private String endereco;

    // @Pattern valida o formato da string usando uma expressão regular.
    @Column(length = 20)
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Telefone inválido.")
    private String telefone;

    // @Email valida o formato de um endereço de e-mail.
    @Column(length = 100)
    @Email(message = "Email inválido.")
    private String email;
}
