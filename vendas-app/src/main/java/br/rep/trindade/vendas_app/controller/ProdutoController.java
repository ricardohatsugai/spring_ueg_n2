package br.rep.trindade.vendas_app.controller;

import br.rep.trindade.vendas_app.model.Produto;
import br.rep.trindade.vendas_app.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public String listProdutos(Model model) {
        model.addAttribute("produtos", produtoService.findAll());
        return "produtos/list";
    }

    @GetMapping("/new")
    public String showProdutoForm(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/form";
    }

    @PostMapping("/save")
    public String saveProduto(@Valid @ModelAttribute("produto") Produto produto, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "produtos/form";
        }
        produtoService.save(produto);
        redirectAttributes.addFlashAttribute("message", "Produto salvo com sucesso!");
        return "redirect:/produtos";
    }

    @GetMapping("/edit/{id}")
    public String showEditProdutoForm(@PathVariable Long id, Model model) {
        Produto produto = produtoService.findById(id);
        model.addAttribute("produto", produto);
        return "produtos/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        produtoService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Produto excluído com sucesso!");
        return "redirect:/produtos";
    }
}
