package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.RevisaoSemanalService;

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
import java.util.Optional;


@RestController
@RequestMapping("/api/usuarios/{idUsuario}")
@Tag(
        name = "Revisão Semanal",
        description = """
                Registro das revisões semanais, dificuldades,
                conquistas, ajustes e evolução do Score de Execução.
                """
)
public class RevisaoSemanalController {

    private final RevisaoSemanalService revisaoService;


    public RevisaoSemanalController(
            RevisaoSemanalService revisaoService
    ) {
        this.revisaoService = revisaoService;
    }


    /* =====================================================
       CADASTRAR REVISÃO SEMANAL
    ===================================================== */

    @Operation(
            summary = "Criar revisão semanal",
            description = """
                    Registra a revisão de um planejamento semanal.

                    A revisão permite registrar as principais conquistas,
                    dificuldades encontradas, ajustes para a próxima semana
                    e observações adicionais.

                    Cada planejamento semanal pode possuir apenas
                    uma revisão.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Revisão semanal criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idRevisaoSemanal": 6,
                                              "idPlanejamentoSemanal": 9,
                                              "semanaCiclo": 1,
                                              "dataInicioSemana": "2026-09-07",
                                              "dataFimSemana": "2026-09-13",
                                              "scoreExecucao": 80.00,
                                              "principaisConquistas": "Concluí os principais testes e avancei na documentação.",
                                              "dificuldades": "Alguns ajustes da API exigiram mais tempo do que o previsto.",
                                              "ajustesProximaSemana": "Reservar blocos específicos para testes e documentação.",
                                              "observacoes": "Semana produtiva e com avanço importante no projeto.",
                                              "dataRevisao": "2026-09-09T11:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da revisão semanal são inválidos",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": "Os dados informados para a revisão semanal são inválidos."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou planejamento semanal não encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe uma revisão para este planejamento semanal",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Já existe uma revisão para este planejamento semanal"
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
    @PostMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO> cadastrar(

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

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da revisão semanal",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "principaisConquistas": "Concluí os principais testes e avancei na documentação.",
                                              "dificuldades": "Alguns ajustes da API exigiram mais tempo do que o previsto.",
                                              "ajustesProximaSemana": "Reservar blocos específicos para testes e documentação.",
                                              "observacoes": "Semana produtiva e com avanço importante no projeto."
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            RevisaoSemanalRequestDTO dto
    ) {

        RevisaoSemanalResponseDTO revisao =
                revisaoService.cadastrar(
                        idUsuario,
                        idPlanejamento,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(revisao);
    }


    /* =====================================================
       BUSCAR REVISÃO DO PLANEJAMENTO
    ===================================================== */

    @Operation(
            summary = "Buscar revisão semanal",
            description = """
                    Retorna a revisão associada ao planejamento
                    semanal informado.

                    Caso ainda não exista uma revisão para o
                    planejamento, retorna 204 No Content.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Revisão semanal encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idRevisaoSemanal": 6,
                                              "idPlanejamentoSemanal": 9,
                                              "semanaCiclo": 1,
                                              "dataInicioSemana": "2026-09-07",
                                              "dataFimSemana": "2026-09-13",
                                              "scoreExecucao": 80.00,
                                              "principaisConquistas": "Concluí os principais testes e avancei na documentação.",
                                              "dificuldades": "Alguns ajustes exigiram mais tempo.",
                                              "ajustesProximaSemana": "Proteger melhor os períodos de foco.",
                                              "observacoes": "Revisão semanal registrada.",
                                              "dataRevisao": "2026-09-09T11:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Ainda não existe revisão para este planejamento semanal"
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
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO> buscar(

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

        Optional<RevisaoSemanalResponseDTO> revisao =
                revisaoService.buscar(
                        idUsuario,
                        idPlanejamento
                );


        if (revisao.isEmpty()) {

            return ResponseEntity
                    .noContent()
                    .build();
        }


        return ResponseEntity.ok(
                revisao.get()
        );
    }


    /* =====================================================
       HISTÓRICO DE REVISÕES
    ===================================================== */

    @Operation(
            summary = "Consultar histórico de revisões semanais",
            description = """
                    Retorna todas as revisões semanais
                    registradas pelo usuário.

                    O histórico permite acompanhar a evolução
                    semanal, os scores de execução, conquistas,
                    dificuldades e ajustes realizados ao longo
                    dos ciclos.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Histórico de revisões listado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    RevisaoSemanalResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idRevisaoSemanal": 6,
                                                "idPlanejamentoSemanal": 9,
                                                "semanaCiclo": 1,
                                                "dataInicioSemana": "2026-09-07",
                                                "dataFimSemana": "2026-09-13",
                                                "scoreExecucao": 80.00,
                                                "principaisConquistas": "Concluí os principais testes.",
                                                "dificuldades": "Alguns ajustes consumiram mais tempo.",
                                                "ajustesProximaSemana": "Melhorar a organização dos blocos de foco.",
                                                "observacoes": "Primeira semana do ciclo.",
                                                "dataRevisao": "2026-09-09T11:00:00"
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
    @GetMapping("/revisoes-semanais")
    public ResponseEntity<List<RevisaoSemanalResponseDTO>>
    listarHistorico(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                revisaoService.listarHistorico(
                        idUsuario
                )
        );
    }


    /* =====================================================
       ATUALIZAR REVISÃO — PUT
    ===================================================== */

    @Operation(
            summary = "Atualizar revisão semanal",
            description = """
                    Atualiza integralmente os campos editáveis
                    de uma revisão semanal existente.

                    O Score de Execução registrado pelo sistema
                    não é informado manualmente neste corpo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Revisão semanal atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, planejamento ou revisão semanal não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PutMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO> atualizar(

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

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualização da revisão semanal",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "principaisConquistas": "Completei os testes planejados e avancei na documentação.",
                                              "dificuldades": "Alguns ajustes da API exigiram mais tempo do que o previsto.",
                                              "ajustesProximaSemana": "Reservar blocos específicos para testes e documentação.",
                                              "observacoes": "Revisão completamente atualizada através do PUT."
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            RevisaoSemanalRequestDTO dto
    ) {

        return ResponseEntity.ok(
                revisaoService.atualizar(
                        idUsuario,
                        idPlanejamento,
                        dto
                )
        );
    }


    /* =====================================================
       ATUALIZAR REVISÃO — PATCH
    ===================================================== */

    @Operation(
            summary = "Atualizar revisão semanal parcialmente",
            description = """
                    Atualiza somente os campos enviados
                    na requisição, preservando os demais
                    dados da revisão semanal.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Revisão semanal atualizada parcialmente com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, planejamento ou revisão semanal não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<RevisaoSemanalResponseDTO>
    atualizarParcialmente(

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

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos da revisão semanal que deverão ser alterados",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            RevisaoSemanalPatchDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "observacoes": "Observação alterada parcialmente através do PATCH."
                                            }
                                            """
                            )
                    )
            )
            @RequestBody
            RevisaoSemanalPatchDTO dto
    ) {

        return ResponseEntity.ok(
                revisaoService.atualizarParcialmente(
                        idUsuario,
                        idPlanejamento,
                        dto
                )
        );
    }


    /* =====================================================
       EXCLUIR REVISÃO
    ===================================================== */

    @Operation(
            summary = "Excluir revisão semanal",
            description = """
                    Exclui a revisão associada ao planejamento
                    semanal informado.

                    O planejamento semanal permanece cadastrado;
                    somente sua revisão é removida.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Revisão semanal excluída com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, planejamento ou revisão semanal não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping(
            "/planejamentos-semanais/{idPlanejamento}/revisao"
    )
    public ResponseEntity<Void> excluir(

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

        revisaoService.excluir(
                idUsuario,
                idPlanejamento
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}