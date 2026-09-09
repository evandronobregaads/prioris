package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.auth.LoginRequestDTO;
import br.com.prioris.backend.dto.auth.LoginResponseDTO;
import br.com.prioris.backend.service.AutenticacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Autenticação",
        description = """
                Autenticação dos usuários do Prioris por e-mail e senha.
                """
)
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;


    public AutenticacaoController(
            AutenticacaoService autenticacaoService
    ) {

        this.autenticacaoService =
                autenticacaoService;
    }


    /* =====================================================
       LOGIN
    ===================================================== */

    @Operation(
            summary = "Realizar login",
            description = """
                    Autentica um usuário do Prioris utilizando
                    e-mail e senha.

                    A senha informada é validada contra a senha
                    armazenada de forma protegida no sistema.

                    Em caso de sucesso, retorna os dados básicos
                    do usuário autenticado.

                    Esta versão do Prioris não utiliza token JWT.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            LoginResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idUsuario": 5,
                                              "nome": "Teste Prioris",
                                              "email": "testeprioris@email.com"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de login inválidos ou campos obrigatórios não informados",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Os dados informados são inválidos."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail ou senha inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 401,
                                              "error": "Unauthorized",
                                              "message": "E-mail ou senha inválidos."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais utilizadas para autenticação",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            LoginRequestDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Login válido",
                                            value = """
                                                    {
                                                      "email": "testeprioris@email.com",
                                                      "senha": "Senha123"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Credenciais inválidas",
                                            value = """
                                                    {
                                                      "email": "testeprioris@email.com",
                                                      "senha": "senha-incorreta"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid
            @RequestBody
            LoginRequestDTO request

    ) {

        LoginResponseDTO response =
                autenticacaoService.login(
                        request
                );


        return ResponseEntity.ok(
                response
        );
    }
}