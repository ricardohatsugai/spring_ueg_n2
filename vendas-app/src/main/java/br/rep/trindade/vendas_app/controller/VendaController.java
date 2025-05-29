package br.rep.trindade.vendas_app.controller;

import br.rep.trindade.vendas_app.model.Venda;
import br.rep.trindade.vendas_app.model.Cliente;
import br.rep.trindade.vendas_app.model.Pagamento;
import br.rep.trindade.vendas_app.model.Produto;
import br.rep.trindade.vendas_app.model.ItemVenda;
import br.rep.trindade.vendas_app.service.VendaService;
import br.rep.trindade.vendas_app.service.ClienteService;
import br.rep.trindade.vendas_app.service.PagamentoService;
import br.rep.trindade.vendas_app.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Importar Collectors

import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory

@Controller
@RequestMapping("/vendas")
public class VendaController {

    private static final Logger logger = LoggerFactory.getLogger(VendaController.class); // Instância do Logger

    @Autowired
    private VendaService vendaService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private PagamentoService pagamentoService;
    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listVendas(Model model) {
        logger.info("Acessando listagem de vendas.");
        model.addAttribute("vendas", vendaService.findAll());
        return "vendas/list";
    }

    @GetMapping("/new")
    public String showVendaForm(Model model) {
        logger.info("Acessando formulário de nova venda.");
        Venda venda = new Venda();
        // Garante que a lista de itens de venda nunca seja nula, mesmo que vazia
        venda.setItensVenda(new ArrayList<>());
        // Adiciona um item de venda vazio para o formulário inicial, se não houver nenhum
        if (venda.getItensVenda().isEmpty()) {
            venda.getItensVenda().add(new ItemVenda());
        }
        model.addAttribute("venda", venda);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("pagamentos", pagamentoService.findAll());
        model.addAttribute("produtos", produtoService.findAll());
        return "vendas/form";
    }

    @PostMapping("/save")
    public String saveVenda(@Valid @ModelAttribute("venda") Venda venda, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        logger.info("Recebendo requisição para salvar/atualizar venda.");
        logger.debug("Venda recebida do formulário (antes da validação): {}", venda);

        // Filtra itens de venda que podem ter sido adicionados mas não preenchidos
        if (venda.getItensVenda() != null) {
            List<ItemVenda> itensValidos = venda.getItensVenda().stream()
                    .filter(item -> item.getProduto() != null && item.getProduto().getId() != null && item.getQuantidade() != null && item.getQuantidade() > 0)
                    .collect(Collectors.toList());
            venda.setItensVenda(itensValidos);
        }

        // Se houver erros de validação (após o filtro)
        if (result.hasErrors()) {
            logger.warn("Erros de validação encontrados no formulário de venda: {}", result.getAllErrors());
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("pagamentos", pagamentoService.findAll());
            model.addAttribute("produtos", produtoService.findAll());
            // Garante que haja pelo menos um item de venda para o formulário não quebrar
            if (venda.getItensVenda() == null || venda.getItensVenda().isEmpty()) {
                venda.setItensVenda(new ArrayList<>());
                venda.getItensVenda().add(new ItemVenda());
            }
            return "vendas/form";
        }

        try {
            logger.debug("Chamando VendaService.save com venda: {}", venda);
            vendaService.save(venda);
            redirectAttributes.addFlashAttribute("message", "Venda salva com sucesso!");
            logger.info("Venda salva com sucesso, redirecionando para /vendas.");
            return "redirect:/vendas";
        } catch (Exception e) {
            // Captura exceções como estoque insuficiente ou recursos não encontrados
            logger.error("Erro ao salvar venda: {}", e.getMessage(), e); // Loga a exceção completa
            model.addAttribute("errorMessage", "Erro ao salvar venda: " + e.getMessage());
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("pagamentos", pagamentoService.findAll());
            model.addAttribute("produtos", produtoService.findAll());
            // Garante que haja pelo menos um item de venda para o formulário não quebrar
            if (venda.getItensVenda() == null || venda.getItensVenda().isEmpty()) {
                venda.setItensVenda(new ArrayList<>());
                venda.getItensVenda().add(new ItemVenda());
            }
            return "vendas/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditVendaForm(@PathVariable Long id, Model model) {
        logger.info("Acessando formulário de edição de venda com ID: {}", id);
        Venda venda = vendaService.findById(id);
        model.addAttribute("venda", venda);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("pagamentos", pagamentoService.findAll());
        model.addAttribute("produtos", produtoService.findAll());
        // Garante que haja pelo menos um item de venda para o formulário
        if (venda.getItensVenda() == null || venda.getItensVenda().isEmpty()) {
            venda.setItensVenda(new ArrayList<>());
            venda.getItensVenda().add(new ItemVenda());
        }
        return "vendas/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteVenda(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("Recebendo requisição para excluir venda com ID: {}", id);
        try {
            vendaService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Venda excluída com sucesso!");
            logger.info("Venda ID {} excluída com sucesso.", id);
        } catch (Exception e) {
            logger.error("Erro ao excluir venda ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir venda: " + e.getMessage());
        }
        return "redirect:/vendas";
    }
}
