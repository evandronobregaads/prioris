package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.PrioridadeDiariaRequestDTO;
import br.com.prioris.backend.dto.PrioridadeDiariaResponseDTO;
import br.com.prioris.backend.service.PrioridadeDiariaService;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;

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
        "/api/usuarios/{idUsuario}/prioridade-diaria"
)
@Tag(
        name = "Prioridade Diária",
        description = """
                Definição, alteração e histórico da Prioridade #1
                do dia no Prioris.
                """
)
public class PrioridadeDiariaController {

    private final PrioridadeDiariaService prioridadeService;


    public PrioridadeDiariaController(
            PrioridadeDiariaService prioridadeService
    ) {
        this.prioridadeService = prioridadeService;
    }


    /* =====================================================
       DEFINIR PRIORIDADE #1
    ===================================================== */

    @Operation(
            summary = "Definir Prioridade #1 do dia",
            description = """
                    Define uma tarefa como a Prioridade #1
                    do usuário para o dia atual.

                    O Prioris permite apenas uma prioridade
                    diária por usuário em cada data.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Prioridade diária definida com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PrioridadeDiariaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idPrioridadeDiaria": 15,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "classificacaoAbcde": "A",
                                              "statusTarefa": "PENDENTE",
                                              "dataPrioridade": "2026-09-09"
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
                    description = "Usuário ou tarefa não encontrado"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Já existe uma Prioridade #1 definida para o usuário hoje",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "error": "Conflict",
                                              "message": "Já existe uma prioridade diária para hoje."
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
    public ResponseEntity<PrioridadeDiariaResponseDTO> definir(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tarefa que será definida como Prioridade #1 do dia",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PrioridadeDiariaRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idTarefa": 24
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            PrioridadeDiariaRequestDTO dto
    ) {

        PrioridadeDiariaResponseDTO prioridade =
                prioridadeService.definir(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(prioridade);
    }


    /* =====================================================
       BUSCAR PRIORIDADE DE HOJE
    ===================================================== */

    @Operation(
            summary = "Buscar Prioridade #1 de hoje",
            description = """
                    Retorna a Prioridade #1 definida para
                    o usuário na data atual.

                    Caso o usuário ainda não tenha definido
                    uma prioridade para hoje, o endpoint
                    retorna 204 No Content.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prioridade diária encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PrioridadeDiariaResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idPrioridadeDiaria": 15,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "classificacaoAbcde": "A",
                                              "statusTarefa": "PENDENTE",
                                              "dataPrioridade": "2026-09-09"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Nenhuma Prioridade #1 definida para hoje"
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
    @GetMapping("/hoje")
    public ResponseEntity<PrioridadeDiariaResponseDTO> buscarHoje(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        try {

            PrioridadeDiariaResponseDTO prioridade =
                    prioridadeService.buscarHoje(
                            idUsuario
                    );

            return ResponseEntity.ok(
                    prioridade
            );

        } catch (RecursoNaoEncontradoException e) {

            return ResponseEntity
                    .noContent()
                    .build();
        }
    }


    /* =====================================================
       ALTERAR PRIORIDADE DE HOJE
    ===================================================== */

    @Operation(
            summary = "Alterar Prioridade #1 de hoje",
            description = """
                    Substitui a tarefa atualmente definida
                    como Prioridade #1 do usuário no dia atual.

                    A nova tarefa deve pertencer ao usuário
                    informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Prioridade diária alterada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PrioridadeDiariaResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, tarefa ou prioridade diária não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/hoje")
    public ResponseEntity<PrioridadeDiariaResponseDTO> alterarHoje(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nova tarefa que será definida como Prioridade #1",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            PrioridadeDiariaRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idTarefa": 23
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            PrioridadeDiariaRequestDTO dto
    ) {

        return ResponseEntity.ok(
                prioridadeService.alterarHoje(
                        idUsuario,
                        dto
                )
        );
    }


    /* =====================================================
       EXCLUIR PRIORIDADE DE HOJE
    ===================================================== */

    @Operation(
            summary = "Excluir Prioridade #1 de hoje",
            description = """
                    Remove a Prioridade #1 definida para
                    o usuário na data atual.

                    A tarefa permanece cadastrada no Prioris;
                    somente sua definição como prioridade diária
                    é removida.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Prioridade diária removida com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou prioridade diária não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping("/hoje")
    public ResponseEntity<Void> excluirHoje(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        prioridadeService.excluirHoje(idUsuario);

        return ResponseEntity
                .noContent()
                .build();
    }


    /* =====================================================
       HISTÓRICO DE PRIORIDADES
    ===================================================== */

    @Operation(
            summary = "Consultar histórico de prioridades",
            description = """
                    Retorna o histórico das Prioridades #1
                    definidas pelo usuário ao longo do tempo.

                    O histórico permite acompanhar quais tarefas
                    foram consideradas mais importantes em cada dia.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Histórico de prioridades listado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    PrioridadeDiariaResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idPrioridadeDiaria": 15,
                                                "idUsuario": 5,
                                                "idTarefa": 24,
                                                "tituloTarefa": "Finalizar documentação do Prioris",
                                                "classificacaoAbcde": "A",
                                                "statusTarefa": "CONCLUIDA",
                                                "dataPrioridade": "2026-09-09"
                                              },
                                              {
                                                "idPrioridadeDiaria": 14,
                                                "idUsuario": 5,
                                                "idTarefa": 23,
                                                "tituloTarefa": "Revisar testes da API",
                                                "classificacaoAbcde": "A",
                                                "statusTarefa": "CONCLUIDA",
                                                "dataPrioridade": "2026-09-08"
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
    @GetMapping("/historico")
    public ResponseEntity<List<PrioridadeDiariaResponseDTO>>
    listarHistorico(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                prioridadeService.listarHistorico(
                        idUsuario
                )
        );
    }
}