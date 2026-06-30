package agroFiscal.controller;

import agroFiscal.model.Fornecedor;
import agroFiscal.service.FornecedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/fornecedores")
public class FornecedorController {

    @Autowired
    private FornecedorService fornecedorService;

    @GetMapping
    public String listar(Model model) {
        // Busca todos os fornecedores no banco e envia para a tela com o nome "fornecedores"
        model.addAttribute("fornecedores", fornecedorService.listarTodos());
        return "fornecedor/lista"; 
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("fornecedor", new Fornecedor());
        return "fornecedor/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(Fornecedor fornecedor) {
        fornecedorService.salvar(fornecedor);
        return "redirect:/fornecedores"; // Redireciona de volta para a tabela
    }
}