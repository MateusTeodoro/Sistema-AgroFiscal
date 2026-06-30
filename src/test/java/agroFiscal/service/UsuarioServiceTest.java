package agroFiscal.service;

import agroFiscal.model.Usuario;
import agroFiscal.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioTeste;

    @BeforeEach
    void setUp() {
        // Prepara um usuário fictício antes de cada teste rodar
        usuarioTeste = new Usuario();
        usuarioTeste.setNome("João");
        usuarioTeste.setEmail("joao@fazenda.com.br");
        usuarioTeste.setSenha("senha123");
    }

    @Test
    void deveRegistrarUsuarioComSucesso() {
        // Configura o Mock: Quando buscar por email, diz que não encontrou nada (Optional vazio)
        when(usuarioRepository.findByEmail(usuarioTeste.getEmail())).thenReturn(Optional.empty());
        // Simula a criptografia da senha
        when(passwordEncoder.encode(any())).thenReturn("senhaCriptografada");
        // Simula o salvamento retornando o próprio usuário
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTeste);

        // Executa o método
        Usuario usuarioSalvo = usuarioService.registrarUsuario(usuarioTeste);

        // Validações (Asserts)
        assertNotNull(usuarioSalvo);
        assertEquals("senhaCriptografada", usuarioSalvo.getSenha());
        assertEquals("OPERACIONAL", usuarioSalvo.getTipoPerfil(), "O perfil padrão deve ser OPERACIONAL");
        
        // Verifica se o repositório realmente foi chamado uma vez
        verify(usuarioRepository, times(1)).save(usuarioTeste);
    }

    @Test
    void naoDeveRegistrarUsuarioComEmailDuplicado() {
        // Configura o Mock: Simula que o e-mail já existe no banco
        when(usuarioRepository.findByEmail(usuarioTeste.getEmail())).thenReturn(Optional.of(usuarioTeste));

        // Valida se o sistema lança a exceção correta
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrarUsuario(usuarioTeste);
        });

        assertEquals("Este e-mail já está em uso na fazenda.", exception.getMessage());
        
        // Garante que o método save() NUNCA foi chamado
        verify(usuarioRepository, never()).save(any());
    }
}