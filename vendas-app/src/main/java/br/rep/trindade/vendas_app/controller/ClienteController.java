package br.rep.trindade.vendas_app.controller;

import br.rep.trindade.vendas_app.model.Cliente;
import br.rep.trindade.vendas_app.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// @Controller indica que esta classe é um controlador Spring MVC e lida com requisições web.
@Controller
// @RequestMapping define o caminho base para todos os métodos neste controlador.
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Exibe a lista de todos os clientes.
     * @param model O modelo para adicionar atributos para a view.
     * @return O nome do template Thymeleaf a ser renderizado.
     */
    @GetMapping
    public String listClientes(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/list"; // Retorna o template src/main/resources/templates/clientes/list.html
    }

    /**
     * Exibe o formulário para criar um novo cliente.
     * @param model O modelo para adicionar um novo objeto Cliente para o formulário.
     * @return O nome do template Thymeleaf do formulário.
     */
    @GetMapping("/new")
    public String showClienteForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form"; // Retorna o template src/main/resources/templates/clientes/form.html
    }

    /**
     * Processa o envio do formulário para salvar um cliente (novo ou existente).
     * @param cliente O objeto Cliente preenchido pelo formulário.
     * @param result Contém os resultados da validação.
     * @param redirectAttributes Usado para adicionar mensagens flash após um redirecionamento.
     * @return Redireciona para a lista de clientes ou volta ao formulário em caso de erro.
     */
    @PostMapping("/save")
    public String saveCliente(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result, RedirectAttributes redirectAttributes) {
        // Se houver erros de validação, retorna ao formulário.
        if (result.hasErrors()) {
            return "clientes/form";
        }
        clienteService.save(cliente);
        redirectAttributes.addFlashAttribute("message", "Cliente salvo com sucesso!");
        return "redirect:/clientes"; // Redireciona para o endpoint /clientes (listClientes)
    }

    /**
     * Exibe o formulário para editar um cliente existente.
     * @param id O ID do cliente a ser editado.
     * @param model O modelo para adicionar o cliente encontrado para o formulário.
     * @return O nome do template Thymeleaf do formulário.
     */
    @GetMapping("/edit/{id}")
    public String showEditClienteForm(@PathVariable Long id, Model model) {
        Cliente cliente = clienteService.findById(id);
        model.addAttribute("cliente", cliente);
        return "clientes/form";
    }

    /**
     * Exclui um cliente pelo ID.
     * @param id O ID do cliente a ser excluído.
     * @param redirectAttributes Usado para adicionar mensagens flash após um redirecionamento.
     * @return Redireciona para a lista de clientes.
     */
    @GetMapping("/delete/{id}")
    public String deleteCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clienteService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Cliente excluído com sucesso!");
        return "redirect:/clientes";
    }
}
