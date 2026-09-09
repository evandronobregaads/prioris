package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.MetaService;

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
@RequestMapping(
        "/api/usuarios/{idUsuario}/objetivos/{idObjetivo}/metas"
)
@Tag(
        name = "Metas",
        description = "Gerenciamento das metas vinculadas aos objetivos do Prioris."
)
public class MetaController {

    private final MetaService metaService;


    public MetaController(
            MetaService metaService
    ) {
        this.metaService = metaService;
    }


    /* =====================================================
       LISTAR METAS
    ===================================================== */

    @Operation(
            summary = "Listar metas de um objetivo",
            description = """
                    Retorna todas as metas pertencentes ao objetivo
                    informado e ao respectivo usuário.

                    As metas representam resultados intermediários
                    utilizados para transformar objetivos em ações
                    executáveis dentro do Prioris.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Metas listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = MetaResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idMeta": 17,
                                                "idObjetivo": 13,
                                                "idUsuario": 5,
                                                "titulo": "Finalizar documentação do Prioris",
                                                "descricao": "Concluir Swagger, README e demais documentos da entrega.",
                                                "prazo": "2026-09-30",
                                                "status": "EM_ANDAMENTO",
                                                "dataCriacao": "2026-09-09T09:30:00"
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou objetivo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @GetMapping
    public ResponseEntity<List<MetaResponseDTO>> listarTodos(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do objetivo ao qual as metas pertencem",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo
    ) {

        return ResponseEntity.ok(
                metaService.listarTodos(
                        idUsuario,
                        idObjetivo
                )
        );
    }


    /* =====================================================
       BUSCAR META POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar meta por ID",
            description = """
                    Retorna uma meta específica vinculada
                    ao objetivo e ao usuário informados.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Meta encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idMeta": 17,
                                              "idObjetivo": 13,
                                              "idUsuario": 5,
                                              "titulo": "Finalizar documentação do Prioris",
                                              "descricao": "Concluir Swagger, README e demais documentos da entrega.",
                                              "prazo": "2026-09-30",
                                              "status": "EM_ANDAMENTO",
                                              "dataCriacao": "2026-09-09T09:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, objetivo ou meta não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Meta não encontrada com o id informado."
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
    @GetMapping("/{idMeta}")
    public ResponseEntity<MetaResponseDTO> buscarPorId(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do objetivo",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo,

            @Parameter(
                    description = "Identificador da meta",
                    example = "17",
                    required = true
            )
            @PathVariable Long idMeta
    ) {

        return ResponseEntity.ok(
                metaService.buscarPorId(
                        idUsuario,
                        idObjetivo,
                        idMeta
                )
        );
    }


    /* =====================================================
       CADASTRAR META
    ===================================================== */

    @Operation(
            summary = "Cadastrar meta",
            description = """
                    Cria uma nova meta vinculada a um objetivo
                    pertencente ao usuário informado.

                    Uma nova meta inicia com o status definido
                    pelas regras de negócio do Prioris.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Meta criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idMeta": 17,
                                              "idObjetivo": 13,
                                              "idUsuario": 5,
                                              "titulo": "Concluir documentação da API",
                                              "descricao": "Finalizar a documentação técnica da API do Prioris.",
                                              "prazo": "2026-09-30",
                                              "status": "PENDENTE",
                                              "dataCriacao": "2026-09-09T09:30:00"
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
                                              "message": "Os dados informados para a meta são inválidos."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou objetivo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PostMapping
    public ResponseEntity<MetaResponseDTO> cadastrar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do objetivo que receberá a meta",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para cadastrar uma meta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "titulo": "Concluir documentação da API",
                                              "descricao": "Finalizar a documentação técnica da API do Prioris.",
                                              "prazo": "2026-09-30"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            MetaRequestDTO dto
    ) {

        MetaResponseDTO meta =
                metaService.cadastrar(
                        idUsuario,
                        idObjetivo,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(meta);
    }


    /* =====================================================
       ATUALIZAR META — PUT
    ===================================================== */

    @Operation(
            summary = "Atualizar meta",
            description = """
                    Atualiza integralmente os dados editáveis
                    de uma meta existente.

                    A meta deve pertencer ao objetivo e ao
                    usuário informados na rota.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Meta atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idMeta": 17,
                                              "idObjetivo": 13,
                                              "idUsuario": 5,
                                              "titulo": "Finalizar documentação e testes",
                                              "descricao": "Concluir toda a auditoria técnica da V1.",
                                              "prazo": "2026-10-05",
                                              "status": "EM_ANDAMENTO",
                                              "dataCriacao": "2026-09-09T09:30:00"
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
                    description = "Usuário, objetivo ou meta não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PutMapping("/{idMeta}")
    public ResponseEntity<MetaResponseDTO> atualizar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do objetivo",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo,

            @Parameter(
                    description = "Identificador da meta",
                    example = "17",
                    required = true
            )
            @PathVariable Long idMeta,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualização da meta",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaAtualizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "titulo": "Finalizar documentação e testes",
                                              "descricao": "Concluir toda a auditoria técnica da V1.",
                                              "prazo": "2026-10-05",
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            MetaAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                metaService.atualizar(
                        idUsuario,
                        idObjetivo,
                        idMeta,
                        dto
                )
        );
    }


    /* =====================================================
       ATUALIZAR META — PATCH
    ===================================================== */

    @Operation(
            summary = "Atualizar meta parcialmente",
            description = """
                    Atualiza somente os campos enviados na requisição,
                    preservando os demais dados da meta.

                    Pode ser utilizado, por exemplo, para alterar
                    apenas o status da meta.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Meta atualizada parcialmente com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, objetivo ou meta não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{idMeta}")
    public ResponseEntity<MetaResponseDTO> atualizarParcialmente(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do objetivo",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo,

            @Parameter(
                    description = "Identificador da meta",
                    example = "17",
                    required = true
            )
            @PathVariable Long idMeta,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos da meta que deverão ser alterados",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = MetaPatchDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": "CONCLUIDA"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            MetaPatchDTO dto
    ) {

        return ResponseEntity.ok(
                metaService.atualizarParcialmente(
                        idUsuario,
                        idObjetivo,
                        idMeta,
                        dto
                )
        );
    }


    /* =====================================================
       CANCELAR META
    ===================================================== */

    @Operation(
            summary = "Cancelar meta",
            description = """
                    Cancela a meta informada de acordo com
                    as regras de negócio do Prioris.

                    O endpoint não retorna conteúdo quando
                    a operação é concluída com sucesso.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Meta cancelada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, objetivo ou meta não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping("/{idMeta}")
    public ResponseEntity<Void> excluir(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do objetivo",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo,

            @Parameter(
                    description = "Identificador da meta",
                    example = "17",
                    required = true
            )
            @PathVariable Long idMeta
    ) {

        metaService.excluir(
                idUsuario,
                idObjetivo,
                idMeta
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}