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

@Controller
@RequestMapping("/vendas")
public class VendaController {

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
        model.addAttribute("vendas", vendaService.findAll());
        return "vendas/list";
    }

    @GetMapping("/new")
    public String showVendaForm(Model model) {
        model.addAttribute("venda", new Venda());
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("pagamentos", pagamentoService.findAll());
        model.addAttribute("produtos", produtoService.findAll());
        // Adiciona um item de venda vazio para o formulário inicial
        if (model.getAttribute("venda") instanceof Venda) {
            Venda venda = (Venda) model.getAttribute("venda");
            if (venda.getItensVenda() == null || venda.getItensVenda().isEmpty()) {
                venda.setItensVenda(new ArrayList<>());
                venda.getItensVenda().add(new ItemVenda());
            }
        }
        return "vendas/form";
    }

    @PostMapping("/save")
    public String saveVenda(@Valid @ModelAttribute("venda") Venda venda, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        // Se houver erros de validação, recarrega o formulário com dados necessários
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("pagamentos", pagamentoService.findAll());
            model.addAttribute("produtos", produtoService.findAll());
            // Garante que o item de venda vazio esteja presente para o formulário não quebrar
            if (venda.getItensVenda() == null || venda.getItensVenda().isEmpty()) {
                venda.setItensVenda(new ArrayList<>());
                venda.getItensVenda().add(new ItemVenda());
            }
            return "vendas/form";
        }
        try {
            vendaService.save(venda);
            redirectAttributes.addFlashAttribute("message", "Venda salva com sucesso!");
            return "redirect:/vendas";
        } catch (Exception e) {
            // Captura exceções como estoque insuficiente ou recursos não encontrados
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("pagamentos", pagamentoService.findAll());
            model.addAttribute("produtos", produtoService.findAll());
            // Garante que o item de venda vazio esteja presente para o formulário não quebrar
            if (venda.getItensVenda() == null || venda.getItensVenda().isEmpty()) {
                venda.setItensVenda(new ArrayList<>());
                venda.getItensVenda().add(new ItemVenda());
            }
            return "vendas/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditVendaForm(@PathVariable Long id, Model model) {
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
        try {
            vendaService.delete(id);
            redirectAttributes.addFlashAttribute("message", "Venda excluída com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir venda: " + e.getMessage());
        }
        return "redirect:/vendas";
    }
}
