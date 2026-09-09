package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.PlanejamentoSemanalService;

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
        "/api/usuarios/{idUsuario}/planejamentos-semanais"
)
@Tag(
        name = "Planejamento Semanal",
        description = """
                Gerenciamento do planejamento semanal,
                das tarefas estratégicas associadas e
                do Score de Execução do Prioris.
                """
)
public class PlanejamentoSemanalController {

    private final PlanejamentoSemanalService planejamentoService;


    public PlanejamentoSemanalController(
            PlanejamentoSemanalService planejamentoService
    ) {
        this.planejamentoService = planejamentoService;
    }


    /* =====================================================
       LISTAR PLANEJAMENTOS SEMANAIS
    ===================================================== */

    @Operation(
            summary = "Listar planejamentos semanais",
            description = """
                    Retorna todos os planejamentos semanais
                    cadastrados para o usuário informado.

                    Cada planejamento representa uma semana
                    de execução e pode conter tarefas estratégicas
                    utilizadas no cálculo do Score de Execução.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Planejamentos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    PlanejamentoSemanalResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idPlanejamentoSemanal": 9,
                                                "idUsuario": 5,
                                                "idCiclo": 8,
                                                "semanaCiclo": 1,
                                                "dataInicioSemana": "2026-09-07",
                                                "dataFimSemana": "2026-09-13",
                                                "totalTarefasPlanejadas": 5,
                                                "totalTarefasConcluidas": 4,
                                                "scoreExecucao": 80.00,
                                                "dataCriacao": "2026-09-09T10:30:00"
                                              }
                                            ]
                                            """
                            )
                    )
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
    @GetMapping
    public ResponseEntity<List<PlanejamentoSemanalResponseDTO>>
    listarTodos(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                planejamentoService.listarTodos(
                        idUsuario
                )
        );
    }


    /* =====================================================
       BUSCAR PLANEJAMENTO POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar planejamento semanal por ID",
            description = """
                    Retorna os dados completos de um planejamento
                    semanal pertencente ao usuário informado.

                    A resposta inclui o período da semana,
                    o ciclo associado, os totais de tarefas
                    e o Score de Execução.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Planejamento encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PlanejamentoSemanalResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idPlanejamentoSemanal": 9,
                                              "idUsuario": 5,
                                              "idCiclo": 8,
                                              "semanaCiclo": 1,
                                              "dataInicioSemana": "2026-09-07",
                                              "dataFimSemana": "2026-09-13",
                                              "totalTarefasPlanejadas": 5,
                                              "totalTarefasConcluidas": 4,
                                              "scoreExecucao": 80.00,
                                              "dataCriacao": "2026-09-09T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou planejamento semanal não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Planejamento semanal não encontrado com o id informado."
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
    @GetMapping("/{idPlanejamento}")
    public ResponseEntity<PlanejamentoSemanalResponseDTO>
    buscarPorId(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do planejamento semanal",
                    example = "9",
                    required = true
            )
            @PathVariable Long idPlanejamento
    ) {

        return ResponseEntity.ok(
                planejamentoService.buscarPorId(
                        idUsuario,
                        idPlanejamento
                )
        );
    }


    /* =====================================================
       CADASTRAR PLANEJAMENTO SEMANAL
    ===================================================== */

    @Operation(
            summary = "Criar planejamento semanal",
            description = """
                    Cria um novo planejamento semanal
                    para o usuário informado.

                    O planejamento pode ser associado a um
                    ciclo de 12 semanas e representa o período
                    em que as tarefas estratégicas serão executadas.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Planejamento semanal criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PlanejamentoSemanalResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idPlanejamentoSemanal": 9,
                                              "idUsuario": 5,
                                              "idCiclo": 8,
                                              "semanaCiclo": 1,
                                              "dataInicioSemana": "2026-09-07",
                                              "dataFimSemana": "2026-09-13",
                                              "totalTarefasPlanejadas": 0,
                                              "totalTarefasConcluidas": 0,
                                              "scoreExecucao": 0.00,
                                              "dataCriacao": "2026-09-09T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados do planejamento são inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Os dados informados para o planejamento semanal são inválidos."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou ciclo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PostMapping
    public ResponseEntity<PlanejamentoSemanalResponseDTO>
    cadastrar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar o planejamento semanal",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PlanejamentoSemanalRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idCiclo": 8,
                                              "semanaCiclo": 1,
                                              "dataInicioSemana": "2026-09-07"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            PlanejamentoSemanalRequestDTO dto
    ) {

        PlanejamentoSemanalResponseDTO planejamento =
                planejamentoService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(planejamento);
    }


    /* =====================================================
       ADICIONAR TAREFA AO PLANEJAMENTO
    ===================================================== */

    @Operation(
            summary = "Adicionar tarefa ao planejamento",
            description = """
                    Associa uma tarefa existente do usuário
                    ao planejamento semanal informado.

                    A tarefa passa a fazer parte das ações
                    estratégicas daquela semana e poderá
                    influenciar o Score de Execução.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Tarefa adicionada ao planejamento com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PlanejamentoTarefaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idPlanejamentoTarefa": 12,
                                              "idTarefa": 24,
                                              "titulo": "Finalizar documentação do Prioris",
                                              "classificacaoAbcde": "A",
                                              "status": "PENDENTE",
                                              "tempoEstimado": 60
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Associação da tarefa ao planejamento é inválida"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, planejamento ou tarefa não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PostMapping(
            "/{idPlanejamento}/tarefas/{idTarefa}"
    )
    public ResponseEntity<PlanejamentoTarefaResponseDTO>
    adicionarTarefa(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do planejamento semanal",
                    example = "9",
                    required = true
            )
            @PathVariable Long idPlanejamento,

            @Parameter(
                    description = "Identificador da tarefa que será adicionada",
                    example = "24",
                    required = true
            )
            @PathVariable Long idTarefa
    ) {

        PlanejamentoTarefaResponseDTO tarefa =
                planejamentoService.adicionarTarefa(
                        idUsuario,
                        idPlanejamento,
                        idTarefa
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tarefa);
    }


    /* =====================================================
       LISTAR TAREFAS DO PLANEJAMENTO
    ===================================================== */

    @Operation(
            summary = "Listar tarefas do planejamento",
            description = """
                    Retorna todas as tarefas estratégicas
                    associadas ao planejamento semanal informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tarefas do planejamento listadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    PlanejamentoTarefaResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idPlanejamentoTarefa": 12,
                                                "idTarefa": 24,
                                                "titulo": "Finalizar documentação do Prioris",
                                                "classificacaoAbcde": "A",
                                                "status": "PENDENTE",
                                                "tempoEstimado": 60
                                              },
                                              {
                                                "idPlanejamentoTarefa": 13,
                                                "idTarefa": 23,
                                                "titulo": "Revisar testes da API",
                                                "classificacaoAbcde": "A",
                                                "status": "CONCLUIDA",
                                                "tempoEstimado": 45
                                              }
                                            ]
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou planejamento semanal não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @GetMapping(
            "/{idPlanejamento}/tarefas"
    )
    public ResponseEntity<List<PlanejamentoTarefaResponseDTO>>
    listarTarefas(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do planejamento semanal",
                    example = "9",
                    required = true
            )
            @PathVariable Long idPlanejamento
    ) {

        return ResponseEntity.ok(
                planejamentoService.listarTarefas(
                        idUsuario,
                        idPlanejamento
                )
        );
    }


    /* =====================================================
       REMOVER TAREFA DO PLANEJAMENTO
    ===================================================== */

    @Operation(
            summary = "Remover tarefa do planejamento",
            description = """
                    Remove a associação entre uma tarefa
                    e o planejamento semanal informado.

                    A tarefa não é excluída do Prioris.
                    Ela apenas deixa de fazer parte daquele
                    planejamento semanal.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Tarefa removida do planejamento com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            Usuário, planejamento, tarefa
                            ou associação não encontrado
                            """
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping(
            "/{idPlanejamento}/tarefas/{idTarefa}"
    )
    public ResponseEntity<Void> removerTarefa(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do planejamento semanal",
                    example = "9",
                    required = true
            )
            @PathVariable Long idPlanejamento,

            @Parameter(
                    description = "Identificador da tarefa que será removida",
                    example = "24",
                    required = true
            )
            @PathVariable Long idTarefa
    ) {

        planejamentoService.removerTarefa(
                idUsuario,
                idPlanejamento,
                idTarefa
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}