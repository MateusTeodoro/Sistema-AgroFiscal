package agroFiscal.service;

import agroFiscal.model.Fornecedor;
import agroFiscal.model.NotaFiscal;
import agroFiscal.repository.FornecedorRepository;
import agroFiscal.repository.NotaFiscalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotaFiscalService {

    @Autowired
    private NotaFiscalRepository notaFiscalRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    public List<NotaFiscal> listarTodas() {
        return notaFiscalRepository.findAll();
    }

    @Transactional
    public void processarXml(MultipartFile arquivo) throws Exception {

        InputStream inputStream = arquivo.getInputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();

        String numeroNota = document.getElementsByTagName("nNF").item(0).getTextContent();
        
        String dataEmissaoString = document.getElementsByTagName("dhEmi").item(0).getTextContent();
        LocalDate dataEmissao = LocalDate.parse(dataEmissaoString.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
        
        String valorTotalString = document.getElementsByTagName("vNF").item(0).getTextContent();
        BigDecimal valorTotal = new BigDecimal(valorTotalString);

        NodeList cnpjNodes = document.getElementsByTagName("CNPJ");
        String cnpjEmitente = cnpjNodes.item(0).getTextContent(); 

        Fornecedor fornecedor = fornecedorRepository.findByCnpj(cnpjEmitente)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor com CNPJ " + cnpjEmitente + " não cadastrado. Cadastre-o primeiro."));

        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setNumero(numeroNota);
        notaFiscal.setDataEmissao(dataEmissao);
        notaFiscal.setValorTotal(valorTotal);
        notaFiscal.setStatus("PENDENTE");
        notaFiscal.setFornecedor(fornecedor);

        notaFiscalRepository.save(notaFiscal);
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

    @Transactional
    public void liquidarPagamento(Long notaId) {
        NotaFiscal nota = notaFiscalRepository.findById(notaId)
                .orElseThrow(() -> new IllegalArgumentException("Nota fiscal não encontrada."));
                
        if (!nota.getStatus().equals("AGUARDANDO_PAGAMENTO")) {
            throw new IllegalStateException("A nota precisa estar aguardando pagamento para ser liquidada.");
        }
        
        nota.setStatus("PAGA");
        notaFiscalRepository.save(nota);
    }

    public NotaFiscal buscarPorId(Long id) {
        return notaFiscalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nota fiscal não encontrada."));
    }
}