package agroFiscal.service;

import agroFiscal.model.NotaFiscal;
import agroFiscal.repository.NotaFiscalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotaFiscalService {

    @Autowired
    private NotaFiscalRepository notaFiscalRepository;

    public List<NotaFiscal> listarTodas() {
        return notaFiscalRepository.findAll();
    }

    @Transactional
    public NotaFiscal registrarNovaNota(NotaFiscal nota) {
        
        nota.setStatus("PENDENTE");
        return notaFiscalRepository.save(nota);
    }

    @Transactional
    public NotaFiscal aprovarOrcamento(Long notaId) {
        NotaFiscal nota = notaFiscalRepository.findById(notaId)
                .orElseThrow(() -> new IllegalArgumentException("Nota fiscal não encontrada."));
                
        if (!nota.getStatus().equals("PENDENTE")) {
            throw new IllegalStateException("Apenas notas PENDENTES podem ter o orçamento aprovado.");
        }
        
        nota.setStatus("AGUARDANDO_ENTREGA");
        return notaFiscalRepository.save(nota);
    }

    @Transactional
    public NotaFiscal confirmarRecebimentoFisico(Long notaId) {
        NotaFiscal nota = notaFiscalRepository.findById(notaId)
                .orElseThrow(() -> new IllegalArgumentException("Nota fiscal não encontrada."));
                
        if (!nota.getStatus().equals("AGUARDANDO_ENTREGA")) {
            throw new IllegalStateException("Carga não autorizada para recebimento ou já recebida.");
        }
        
        nota.setStatus("AGUARDANDO_PAGAMENTO");
        return notaFiscalRepository.save(nota);
    }
}