package agroFiscal.config;

import agroFiscal.model.Usuario;
import agroFiscal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        if (usuarioRepository.count() == 0) {
            Usuario gestor = new Usuario();
            gestor.setNome("Gestor Principal");
            gestor.setEmail("admin@fazenda.com.br");
            gestor.setSenha(passwordEncoder.encode("fazenda123"));
            gestor.setTipoPerfil("GESTOR");
            
            usuarioRepository.save(gestor);
            System.out.println("✅ Usuário administrador criado com sucesso!");
        }
    }
}