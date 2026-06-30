package agroFiscal.service;

import agroFiscal.model.NotaFiscal;
import agroFiscal.repository.NotaFiscalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotaFiscalServiceTest {

    @Mock
    private NotaFiscalRepository notaFiscalRepository;

    @InjectMocks
    private NotaFiscalService notaFiscalService;

    private NotaFiscal notaTeste;

    @BeforeEach
    void setUp() {
        notaTeste = new NotaFiscal();
        notaTeste.setId(1L);
        notaTeste.setNumero("12345");
        notaTeste.setStatus("PENDENTE"); // Estado inicial
    }

    @Test
    void deveAprovarOrcamentoComSucesso() {
        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(notaTeste));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaTeste);

        NotaFiscal notaAprovada = notaFiscalService.aprovarOrcamento(1L);

        // A nota deve ter mudado de PENDENTE para AGUARDANDO_ENTREGA
        assertEquals("AGUARDANDO_ENTREGA", notaAprovada.getStatus());
        verify(notaFiscalRepository, times(1)).save(notaTeste);
    }

    @Test
    void naoDeveAprovarOrcamentoSeStatusNaoForPendente() {
        notaTeste.setStatus("PAGA"); // Forçando um status incorreto
        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(notaTeste));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            notaFiscalService.aprovarOrcamento(1L);
        });

        assertEquals("Apenas notas PENDENTES podem ter o orçamento aprovado.", exception.getMessage());
        verify(notaFiscalRepository, never()).save(any());
    }

    @Test
    void deveConfirmarRecebimentoFisicoComSucesso() {
        notaTeste.setStatus("AGUARDANDO_ENTREGA");
        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(notaTeste));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaTeste);

        NotaFiscal notaRecebida = notaFiscalService.confirmarRecebimentoFisico(1L);

        // Deve evoluir para o setor financeiro
        assertEquals("AGUARDANDO_PAGAMENTO", notaRecebida.getStatus());
    }

    @Test
    void deveLiquidarPagamentoComSucesso() {
        notaTeste.setStatus("AGUARDANDO_PAGAMENTO");
        when(notaFiscalRepository.findById(1L)).thenReturn(Optional.of(notaTeste));
        when(notaFiscalRepository.save(any(NotaFiscal.class))).thenReturn(notaTeste);

        notaFiscalService.liquidarPagamento(1L);

        // O fluxo chegou ao fim
        assertEquals("PAGA", notaTeste.getStatus());
    }
}