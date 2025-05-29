package br.rep.trindade.vendas_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Anotação @Controller indica que esta classe é um controlador Spring MVC.
@Controller
public class HomeController {

    /**
     * Mapeia a requisição GET para a URL raiz ("/") da aplicação.
     * Quando um usuário acessa http://localhost:8080, este método será invocado.
     * @return O nome do template Thymeleaf a ser renderizado (index.html).
     */
    @GetMapping("/")
    public String home() {
        return "index"; // Retorna o template src/main/resources/templates/index.html
    }
}
