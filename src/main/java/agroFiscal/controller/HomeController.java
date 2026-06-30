package agroFiscal.controller;

import agroFiscal.model.NotaFiscal;
import agroFiscal.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private NotaFiscalService notaFiscalService;

    @GetMapping("/")
    public String index(Model model) {
        
        List<NotaFiscal> todasNotas = notaFiscalService.listarTodas();
        
        long notasPendentes = todasNotas.stream()
                .filter(n -> n.getStatus().equals("PENDENTE"))
                .count();

        long aguardandoPagamento = todasNotas.stream()
                .filter(n -> n.getStatus().equals("AGUARDANDO_PAGAMENTO"))
                .count();

        model.addAttribute("totalNotas", todasNotas.size());
        model.addAttribute("notasPendentes", notasPendentes);
        model.addAttribute("aguardandoPagamento", aguardandoPagamento);

        return "index";
    }
}