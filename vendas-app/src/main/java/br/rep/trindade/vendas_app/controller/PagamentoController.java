package br.rep.trindade.vendas_app.controller;

import br.rep.trindade.vendas_app.model.Pagamento;
import br.rep.trindade.vendas_app.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pagamentos")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public String listPagamentos(Model model) {
        model.addAttribute("pagamentos", pagamentoService.findAll());
        return "pagamentos/list";
    }

    @GetMapping("/new")
    public String showPagamentoForm(Model model) {
        // RENOMEADO: De "pagamento" para "pagamentoForm"
        model.addAttribute("pagamentoForm", new Pagamento());
        return "pagamentos/form";
    }

    @PostMapping("/save")
    // RENOMEADO: @ModelAttribute de "pagamento" para "pagamentoForm"
    public String savePagamento(@Valid @ModelAttribute("pagamentoForm") Pagamento pagamento, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "pagamentos/form";
        }
        pagamentoService.save(pagamento);
        redirectAttributes.addFlashAttribute("message", "Pagamento salvo com sucesso!");
        return "redirect:/pagamentos";
    }

    @GetMapping("/edit/{id}")
    public String showEditPagamentoForm(@PathVariable Long id, Model model) {
        Pagamento pagamento = pagamentoService.findById(id);
        // RENOMEADO: De "pagamento" para "pagamentoForm"
        model.addAttribute("pagamentoForm", pagamento);
        return "pagamentos/form";
    }

    @GetMapping("/delete/{id}")
    public String deletePagamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pagamentoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Pagamento excluído com sucesso!");
        return "redirect:/pagamentos";
    }
}
