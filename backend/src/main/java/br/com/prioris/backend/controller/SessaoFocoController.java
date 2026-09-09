package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.SessaoFocoFinalizacaoDTO;
import br.com.prioris.backend.dto.SessaoFocoRequestDTO;
import br.com.prioris.backend.dto.SessaoFocoResponseDTO;
import br.com.prioris.backend.service.SessaoFocoService;

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
@RequestMapping("/api/usuarios/{idUsuario}/sessoes-foco")
@Tag(
        name = "Sessões de Foco",
        description = """
                Gerenciamento das sessões de foco e Pomodoro,
                incluindo início, pausa, retomada, finalização,
                interrupção e histórico de execução.
                """
)
public class SessaoFocoController {

    private final SessaoFocoService sessaoFocoService;


    public SessaoFocoController(
            SessaoFocoService sessaoFocoService
    ) {
        this.sessaoFocoService = sessaoFocoService;
    }


    /* =====================================================
       INICIAR SESSÃO
    ===================================================== */

    @Operation(
            summary = "Iniciar sessão de foco",
            description = """
                    Inicia uma nova sessão de foco para o usuário.

                    A sessão pode ser vinculada a uma tarefa específica
                    ou ser iniciada sem tarefa associada.

                    Devem ser informados o tempo planejado de foco
                    e o tempo planejado de descanso.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Sessão de foco iniciada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idSessaoFoco": 10,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "dataInicio": "2026-09-09T11:30:00",
                                              "dataFim": null,
                                              "tempoFocoPlanejado": 25,
                                              "tempoDescansoPlanejado": 5,
                                              "tempoFocoRealizado": null,
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da sessão são inválidos"
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
    @PostMapping
    public ResponseEntity<SessaoFocoResponseDTO> iniciar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Configuração da sessão de foco.
                            O idTarefa é opcional.
                            """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoRequestDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Sessão vinculada a uma tarefa",
                                            value = """
                                                    {
                                                      "idTarefa": 24,
                                                      "tempoFocoPlanejado": 25,
                                                      "tempoDescansoPlanejado": 5
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Sessão sem tarefa específica",
                                            value = """
                                                    {
                                                      "idTarefa": null,
                                                      "tempoFocoPlanejado": 25,
                                                      "tempoDescansoPlanejado": 5
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid
            @RequestBody
            SessaoFocoRequestDTO dto
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        sessaoFocoService.iniciar(
                                idUsuario,
                                dto
                        )
                );
    }


    /* =====================================================
       HISTÓRICO DE SESSÕES
    ===================================================== */

    @Operation(
            summary = "Consultar histórico de sessões de foco",
            description = """
                    Retorna todas as sessões de foco registradas
                    pelo usuário.

                    O histórico pode incluir sessões em andamento,
                    pausadas, concluídas e interrompidas.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Histórico de sessões listado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation =
                                                    SessaoFocoResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idSessaoFoco": 10,
                                                "idUsuario": 5,
                                                "idTarefa": 24,
                                                "tituloTarefa": "Finalizar documentação do Prioris",
                                                "dataInicio": "2026-09-09T11:30:00",
                                                "dataFim": "2026-09-09T11:55:00",
                                                "tempoFocoPlanejado": 25,
                                                "tempoDescansoPlanejado": 5,
                                                "tempoFocoRealizado": 25,
                                                "status": "CONCLUIDA"
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
    public ResponseEntity<List<SessaoFocoResponseDTO>>
    listarHistorico(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.listarHistorico(
                        idUsuario
                )
        );
    }


    /* =====================================================
       BUSCAR SESSÃO POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar sessão de foco por ID",
            description = """
                    Retorna os dados de uma sessão de foco
                    específica pertencente ao usuário informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão de foco encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idSessaoFoco": 10,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "dataInicio": "2026-09-09T11:30:00",
                                              "dataFim": null,
                                              "tempoFocoPlanejado": 25,
                                              "tempoDescansoPlanejado": 5,
                                              "tempoFocoRealizado": null,
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou sessão de foco não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Sessão de foco não encontrada com o id informado."
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
    @GetMapping("/{idSessao}")
    public ResponseEntity<SessaoFocoResponseDTO> buscarPorId(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da sessão de foco",
                    example = "10",
                    required = true
            )
            @PathVariable Long idSessao
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.buscarPorId(
                        idUsuario,
                        idSessao
                )
        );
    }


    /* =====================================================
       PAUSAR SESSÃO
    ===================================================== */

    @Operation(
            summary = "Pausar sessão de foco",
            description = """
                    Pausa uma sessão de foco que esteja
                    atualmente em andamento.

                    Após a operação, o status da sessão
                    passa para PAUSADA.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão pausada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idSessaoFoco": 10,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "dataInicio": "2026-09-09T11:30:00",
                                              "dataFim": null,
                                              "tempoFocoPlanejado": 25,
                                              "tempoDescansoPlanejado": 5,
                                              "tempoFocoRealizado": null,
                                              "status": "PAUSADA"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A sessão não pode ser pausada no estado atual"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou sessão de foco não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{idSessao}/pausar")
    public ResponseEntity<SessaoFocoResponseDTO> pausar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da sessão de foco",
                    example = "10",
                    required = true
            )
            @PathVariable Long idSessao
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.pausar(
                        idUsuario,
                        idSessao
                )
        );
    }


    /* =====================================================
       RETOMAR SESSÃO
    ===================================================== */

    @Operation(
            summary = "Retomar sessão de foco",
            description = """
                    Retoma uma sessão de foco que esteja pausada.

                    Após a operação, o status da sessão
                    volta para EM_ANDAMENTO.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão retomada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idSessaoFoco": 10,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "dataInicio": "2026-09-09T11:30:00",
                                              "dataFim": null,
                                              "tempoFocoPlanejado": 25,
                                              "tempoDescansoPlanejado": 5,
                                              "tempoFocoRealizado": null,
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "A sessão não pode ser retomada no estado atual"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou sessão de foco não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{idSessao}/retomar")
    public ResponseEntity<SessaoFocoResponseDTO> retomar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da sessão de foco",
                    example = "10",
                    required = true
            )
            @PathVariable Long idSessao
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.retomar(
                        idUsuario,
                        idSessao
                )
        );
    }


    /* =====================================================
       FINALIZAR SESSÃO
    ===================================================== */

    @Operation(
            summary = "Finalizar sessão de foco",
            description = """
                    Finaliza uma sessão de foco normalmente.

                    Deve ser informado o total de minutos
                    de foco efetivamente realizados.

                    Após a finalização, a sessão passa
                    para o status CONCLUIDA.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão finalizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idSessaoFoco": 10,
                                              "idUsuario": 5,
                                              "idTarefa": 24,
                                              "tituloTarefa": "Finalizar documentação do Prioris",
                                              "dataInicio": "2026-09-09T11:30:00",
                                              "dataFim": "2026-09-09T11:50:00",
                                              "tempoFocoPlanejado": 25,
                                              "tempoDescansoPlanejado": 5,
                                              "tempoFocoRealizado": 20,
                                              "status": "CONCLUIDA"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Tempo realizado inválido ou sessão
                            em estado incompatível com a finalização
                            """
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou sessão de foco não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{idSessao}/finalizar")
    public ResponseEntity<SessaoFocoResponseDTO> finalizar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da sessão de foco",
                    example = "10",
                    required = true
            )
            @PathVariable Long idSessao,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Tempo efetivamente realizado durante a sessão",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoFinalizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "tempoFocoRealizado": 20
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            SessaoFocoFinalizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.finalizar(
                        idUsuario,
                        idSessao,
                        dto
                )
        );
    }


    /* =====================================================
       INTERROMPER SESSÃO
    ===================================================== */

    @Operation(
            summary = "Interromper sessão de foco",
            description = """
                    Encerra uma sessão de foco antes de sua
                    conclusão normal.

                    Deve ser informado o tempo de foco realizado
                    até o momento da interrupção.

                    Após a operação, a sessão passa para
                    o status INTERROMPIDA.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sessão interrompida com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idSessaoFoco": 11,
                                              "idUsuario": 5,
                                              "idTarefa": null,
                                              "tituloTarefa": null,
                                              "dataInicio": "2026-09-09T12:00:00",
                                              "dataFim": "2026-09-09T12:05:00",
                                              "tempoFocoPlanejado": 25,
                                              "tempoDescansoPlanejado": 5,
                                              "tempoFocoRealizado": 5,
                                              "status": "INTERROMPIDA"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Tempo realizado inválido ou sessão
                            em estado incompatível com a interrupção
                            """
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou sessão de foco não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PatchMapping("/{idSessao}/interromper")
    public ResponseEntity<SessaoFocoResponseDTO> interromper(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador da sessão de foco",
                    example = "11",
                    required = true
            )
            @PathVariable Long idSessao,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Tempo de foco realizado até o momento
                            em que a sessão foi interrompida
                            """,
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation =
                                            SessaoFocoFinalizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "tempoFocoRealizado": 5
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            SessaoFocoFinalizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                sessaoFocoService.interromper(
                        idUsuario,
                        idSessao,
                        dto
                )
        );
    }
}