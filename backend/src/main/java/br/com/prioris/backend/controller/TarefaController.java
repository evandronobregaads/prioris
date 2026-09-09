package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.TarefaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/usuarios/{idUsuario}/tarefas")
@Tag(
        name = "Tarefas",
        description = "Gerenciamento de tarefas, classificação ABCDE e vínculos com objetivos ou metas."
)
public class TarefaController {

    private final TarefaService tarefaService;


    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }


    /* =====================================================
       LISTAR TAREFAS
    ===================================================== */

    @Operation(
            summary = "Listar tarefas",
            description = """
                    Retorna todas as tarefas pertencentes ao usuário informado.
                    As tarefas podem estar vinculadas diretamente a um objetivo,
                    a uma meta ou não possuir vínculo estratégico.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefas listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idTarefa": 24,
                                                "idUsuario": 5,
                                                "idMeta": 17,
                                                "idObjetivo": null,
                                                "titulo": "Finalizar documentação da API",
                                                "descricao": "Concluir a documentação do projeto Prioris.",
                                                "classificacaoAbcde": "A",
                                                "dataPlanejada": "2026-09-09",
                                                "prazo": "2026-09-09T18:00:00",
                                                "tempoEstimado": 60,
                                                "status": "PENDENTE"
                                              }
                                            ]
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
    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> listarTodos(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                tarefaService.listarTodos(idUsuario)
        );
    }


    /* =====================================================
       BUSCAR TAREFA POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar tarefa por ID",
            description = """
                    Retorna os dados de uma tarefa específica pertencente
                    ao usuário informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefa encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idTarefa": 24,
                                              "idUsuario": 5,
                                              "idMeta": 17,
                                              "idObjetivo": null,
                                              "titulo": "Finalizar documentação da API",
                                              "descricao": "Concluir a documentação do projeto Prioris.",
                                              "classificacaoAbcde": "A",
                                              "dataPlanejada": "2026-09-09",
                                              "prazo": "2026-09-09T18:00:00",
                                              "tempoEstimado": 60,
                                              "status": "PENDENTE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou tarefa não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Tarefa não encontrada com o id informado."
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
    @GetMapping("/{idTarefa}")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da tarefa",
                    example = "24",
                    required = true
            )
            @PathVariable Long idTarefa
    ) {

        return ResponseEntity.ok(
                tarefaService.buscarPorId(
                        idUsuario,
                        idTarefa
                )
        );
    }


    /* =====================================================
       CADASTRAR TAREFA
    ===================================================== */

    @Operation(
            summary = "Cadastrar tarefa",
            description = """
                    Cria uma nova tarefa para o usuário.

                    A tarefa pode estar vinculada a uma meta OU diretamente
                    a um objetivo, mas nunca aos dois simultaneamente.

                    Também é permitido cadastrar uma tarefa sem vínculo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idTarefa": 24,
                                              "idUsuario": 5,
                                              "idMeta": 17,
                                              "idObjetivo": null,
                                              "titulo": "Finalizar documentação da API",
                                              "descricao": "Concluir a documentação Swagger do Prioris.",
                                              "classificacaoAbcde": "A",
                                              "dataPlanejada": "2026-09-09",
                                              "prazo": "2026-09-09T18:00:00",
                                              "tempoEstimado": 60,
                                              "status": "PENDENTE"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou tentativa de vincular simultaneamente Meta e Objetivo",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "A tarefa não pode estar vinculada a uma meta e a um objetivo ao mesmo tempo."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, meta ou objetivo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PostMapping
    public ResponseEntity<TarefaResponseDTO> cadastrar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para cadastrar uma tarefa",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    name = "Tarefa vinculada a uma meta",
                                    value = """
                                            {
                                              "idMeta": 17,
                                              "idObjetivo": null,
                                              "titulo": "Finalizar documentação da API",
                                              "descricao": "Concluir a documentação Swagger do Prioris.",
                                              "classificacaoAbcde": "A",
                                              "dataPlanejada": "2026-09-09",
                                              "prazo": "2026-09-09T18:00:00",
                                              "tempoEstimado": 60
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            TarefaRequestDTO dto
    ) {

        TarefaResponseDTO tarefa =
                tarefaService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarefa);
    }


    /* =====================================================
       ATUALIZAR TAREFA — PUT
    ===================================================== */

    @Operation(
            summary = "Atualizar tarefa",
            description = """
                    Atualiza integralmente os dados editáveis de uma tarefa.

                    O PUT também permite alterar o vínculo estratégico da tarefa,
                    definindo uma meta, um objetivo ou removendo o vínculo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefa atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou vínculo estratégico incompatível"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, tarefa, meta ou objetivo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PutMapping("/{idTarefa}")
    public ResponseEntity<TarefaResponseDTO> atualizar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da tarefa",
                    example = "24",
                    required = true
            )
            @PathVariable Long idTarefa,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualização da tarefa",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaAtualizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idMeta": null,
                                              "idObjetivo": 13,
                                              "titulo": "Finalizar Swagger do Prioris",
                                              "descricao": "Revisar toda a documentação da API.",
                                              "classificacaoAbcde": "A",
                                              "dataPlanejada": "2026-09-10",
                                              "prazo": "2026-09-10T18:30:00",
                                              "tempoEstimado": 90,
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            TarefaAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                tarefaService.atualizar(
                        idUsuario,
                        idTarefa,
                        dto
                )
        );
    }


    /* =====================================================
       ATUALIZAR PARCIALMENTE — PATCH
    ===================================================== */

    @Operation(
            summary = "Atualizar tarefa parcialmente",
            description = """
                    Atualiza somente os campos enviados na requisição,
                    preservando os demais dados da tarefa.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefa atualizada parcialmente com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou tarefa não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{idTarefa}")
    public ResponseEntity<TarefaResponseDTO> atualizarParcialmente(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da tarefa",
                    example = "24",
                    required = true
            )
            @PathVariable Long idTarefa,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos que deverão ser alterados",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = TarefaPatchDTO.class
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
            TarefaPatchDTO dto
    ) {

        return ResponseEntity.ok(
                tarefaService.atualizarParcialmente(
                        idUsuario,
                        idTarefa,
                        dto
                )
        );
    }


    /* =====================================================
       EXCLUIR TAREFA
    ===================================================== */

    @Operation(
            summary = "Excluir tarefa",
            description = """
                    Remove a tarefa indicada de acordo com as regras
                    de negócio do Prioris.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Tarefa removida com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou tarefa não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping("/{idTarefa}")
    public ResponseEntity<Void> excluir(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da tarefa",
                    example = "24",
                    required = true
            )
            @PathVariable Long idTarefa
    ) {

        tarefaService.excluir(
                idUsuario,
                idTarefa
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}