package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.UsuarioAtualizacaoDTO;
import br.com.prioris.backend.dto.UsuarioPatchDTO;
import br.com.prioris.backend.dto.UsuarioRequestDTO;
import br.com.prioris.backend.dto.UsuarioResponseDTO;
import br.com.prioris.backend.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/usuarios")
@Tag(
        name = "Usuários",
        description = "Gerenciamento dos usuários do Prioris."
)
public class UsuarioController {

    private final UsuarioService usuarioService;


    public UsuarioController(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }


    /* =====================================================
       LISTAR USUÁRIOS
    ===================================================== */

    @Operation(
            summary = "Listar usuários",
            description = """
                    Retorna todos os usuários ativos cadastrados no Prioris.
                    Caso não existam usuários ativos, retorna uma lista vazia.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuários listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = UsuarioResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idUsuario": 5,
                                                "nome": "Usuário Teste",
                                                "email": "usuario@email.com",
                                                "dataCriacao": "2026-09-09T08:30:00",
                                                "ativo": true
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {

        return ResponseEntity.ok(
                usuarioService.listarTodos()
        );
    }


    /* =====================================================
       BUSCAR USUÁRIO POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar usuário por ID",
            description = """
                    Retorna os dados de um usuário ativo
                    a partir do seu identificador.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idUsuario": 5,
                                              "nome": "Usuário Teste",
                                              "email": "usuario@email.com",
                                              "dataCriacao": "2026-09-09T08:30:00",
                                              "ativo": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Usuário não encontrado com o id informado."
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
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }


    /* =====================================================
       CADASTRAR USUÁRIO
    ===================================================== */

    @Operation(
            summary = "Cadastrar usuário",
            description = """
                    Cria um novo usuário no Prioris.

                    O nome, e-mail e senha são obrigatórios.
                    O e-mail deve ser válido e não pode estar
                    cadastrado para outro usuário.

                    A senha é armazenada de forma protegida
                    utilizando BCrypt.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idUsuario": 12,
                                              "nome": "Novo Usuário",
                                              "email": "novo.usuario@email.com",
                                              "dataCriacao": "2026-09-09T14:00:00",
                                              "ativo": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Dados informados são inválidos."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já cadastrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "E-mail já cadastrado."
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
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para cadastrar um usuário",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "nome": "Novo Usuário",
                                              "email": "novo.usuario@email.com",
                                              "senha": "SenhaSegura123"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            UsuarioRequestDTO dto
    ) {

        UsuarioResponseDTO usuario =
                usuarioService.cadastrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }


    /* =====================================================
       ATUALIZAR USUÁRIO — PUT
    ===================================================== */

    @Operation(
            summary = "Atualizar usuário",
            description = """
                    Atualiza integralmente os dados editáveis
                    de um usuário existente.

                    O usuário deve estar ativo e o novo e-mail,
                    caso alterado, não pode pertencer a outro usuário.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idUsuario": 5,
                                              "nome": "Usuário Atualizado",
                                              "email": "usuario.atualizado@email.com",
                                              "dataCriacao": "2026-09-09T08:30:00",
                                              "ativo": true
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já utilizado por outro usuário"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualização do usuário",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioAtualizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "nome": "Usuário Atualizado",
                                              "email": "usuario.atualizado@email.com",
                                              "senha": "NovaSenha123"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            UsuarioAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                usuarioService.atualizar(
                        id,
                        dto
                )
        );
    }


    /* =====================================================
       ATUALIZAR PARCIALMENTE — PATCH
    ===================================================== */

    @Operation(
            summary = "Atualizar usuário parcialmente",
            description = """
                    Atualiza somente os campos enviados na requisição,
                    preservando os demais dados do usuário.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário atualizado parcialmente com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já utilizado por outro usuário"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizarParcialmente(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos que deverão ser alterados",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = UsuarioPatchDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "nome": "Usuário Teste Atualizado"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            UsuarioPatchDTO dto
    ) {

        return ResponseEntity.ok(
                usuarioService.atualizarParcialmente(
                        id,
                        dto
                )
        );
    }


    /* =====================================================
       DESATIVAR USUÁRIO
    ===================================================== */

    @Operation(
            summary = "Desativar usuário",
            description = """
                    Realiza a exclusão lógica do usuário.

                    O registro permanece armazenado no banco de dados,
                    porém deixa de ser considerado um usuário ativo
                    pelo sistema.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Usuário desativado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long id
    ) {

        usuarioService.excluir(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}