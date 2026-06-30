package agroFiscal.controller;

import agroFiscal.service.NotaFiscalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping("/aprovar/{id}")
    public String aprovarNota(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            notaFiscalService.aprovarOrcamento(id);
            redirectAttributes.addFlashAttribute("sucesso", "Orçamento aprovado. Carga autorizada para recebimento.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/notas";
    }

    @PostMapping("/receber/{id}")
    public String receberCarga(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            notaFiscalService.confirmarRecebimentoFisico(id);
            redirectAttributes.addFlashAttribute("sucesso", "Recebimento confirmado. Nota enviada ao financeiro.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/notas";
    }

    @PostMapping("/pagar/{id}")
    public String pagarNota(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            notaFiscalService.liquidarPagamento(id);
            redirectAttributes.addFlashAttribute("sucesso", "Pagamento liquidado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/notas";
    }
}