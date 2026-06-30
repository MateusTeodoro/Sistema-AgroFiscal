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
        String cnpjEmitente = cnpjNodes.item(0).getTextContent(); // Pega o primeiro CNPJ (do emitente)

        Fornecedor fornecedor = fornecedorRepository.findByCnpj(cnpjEmitente)
                .orElseThrow(() -> new IllegalArgumentException("Fornecedor com CNPJ " + cnpjEmitente + " não cadastrado. Cadastre-o primeiro."));

        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setNumero(numeroNota);
        notaFiscal.setDataEmissao(dataEmissao);
        notaFiscal.setValorTotal(valorTotal);
        notaFiscal.setStatus("PENDENTE"); // Estado inicial do seu BPMN
        notaFiscal.setFornecedor(fornecedor);

        notaFiscalRepository.save(notaFiscal);
    }
}