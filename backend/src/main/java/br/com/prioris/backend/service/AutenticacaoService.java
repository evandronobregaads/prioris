package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.auth.LoginRequestDTO;
import br.com.prioris.backend.dto.auth.LoginResponseDTO;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.CredenciaisInvalidasException;
import br.com.prioris.backend.repository.UsuarioRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {

        this.usuarioRepository =
                usuarioRepository;

        this.passwordEncoder =
                passwordEncoder;
    }


    public LoginResponseDTO login(
            LoginRequestDTO request
    ) {

        String emailNormalizado =
                request.email()
                        .trim()
                        .toLowerCase();


        Usuario usuario =
                usuarioRepository
                        .findByEmailIgnoreCase(
                                emailNormalizado
                        )
                        .orElseThrow(
                                CredenciaisInvalidasException::new
                        );


        boolean senhaCorreta =
                passwordEncoder.matches(
                        request.senha(),
                        usuario.getSenhaHash()
                );


        if (!senhaCorreta) {

            throw new CredenciaisInvalidasException();
        }


        return new LoginResponseDTO(

                usuario.getIdUsuario(),

                usuario.getNome(),

                usuario.getEmail()
        );
    }
}
