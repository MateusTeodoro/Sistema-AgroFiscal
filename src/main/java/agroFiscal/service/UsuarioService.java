package agroFiscal.service;

import agroFiscal.model.Usuario;
import agroFiscal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario usuario) {
        
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        
        if (existente.isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está em uso na fazenda.");
        }

        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        
        if (usuario.getTipoPerfil() == null || usuario.getTipoPerfil().isEmpty()) {
            usuario.setTipoPerfil("OPERACIONAL"); 
        }

        return usuarioRepository.save(usuario);
    }
}