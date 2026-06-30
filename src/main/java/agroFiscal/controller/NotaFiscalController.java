package agroFiscal.controller;

import agroFiscal.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notas")
public class NotaFiscalController {

    @Autowired
    private NotaFiscalService notaFiscalService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("notas", notaFiscalService.listarTodas());
        return "nota/lista";
    }

    @GetMapping("/upload")
    public String exibirFormularioUpload() {
        return "nota/upload";
    }

    @PostMapping("/processar-xml")
    public String processarXml(@RequestParam("arquivoXml") MultipartFile arquivoXml, RedirectAttributes redirectAttributes) {
        try {
            if (arquivoXml.isEmpty()) {
                redirectAttributes.addFlashAttribute("erro", "Por favor, selecione um arquivo XML.");
                return "redirect:/notas/upload";
            }
            
            notaFiscalService.processarXml(arquivoXml);
            redirectAttributes.addFlashAttribute("sucesso", "Nota Fiscal importada com sucesso!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar XML: " + e.getMessage());
        }
        
        return "redirect:/notas";
    }
}