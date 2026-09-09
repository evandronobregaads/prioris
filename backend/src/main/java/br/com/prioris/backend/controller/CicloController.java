package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.service.CicloService;

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
@RequestMapping("/api/usuarios/{idUsuario}/ciclos")
@Tag(
        name = "Ciclos de 12 Semanas",
        description = """
                Gerenciamento dos ciclos de 12 semanas do Prioris
                e dos objetivos estratégicos associados a cada ciclo.
                """
)
public class CicloController {

    private final CicloService cicloService;


    public CicloController(
            CicloService cicloService
    ) {
        this.cicloService = cicloService;
    }


    /* =====================================================
       LISTAR CICLOS
    ===================================================== */

    @Operation(
            summary = "Listar ciclos de 12 semanas",
            description = """
                    Retorna todos os ciclos cadastrados
                    para o usuário informado.

                    Cada ciclo representa um período de execução
                    estratégica de 12 semanas no Prioris.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ciclos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = CicloResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idCiclo": 8,
                                                "idUsuario": 5,
                                                "titulo": "Ciclo Prioris - Execução 2026",
                                                "dataInicio": "2026-09-07",
                                                "dataFim": "2026-11-29",
                                                "status": "EM_ANDAMENTO",
                                                "dataCriacao": "2026-09-09T10:00:00"
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
    public ResponseEntity<List<CicloResponseDTO>> listarTodos(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                cicloService.listarTodos(idUsuario)
        );
    }


    /* =====================================================
       BUSCAR CICLO POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar ciclo por ID",
            description = """
                    Retorna os dados de um ciclo de 12 semanas
                    específico pertencente ao usuário informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ciclo encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idCiclo": 8,
                                              "idUsuario": 5,
                                              "titulo": "Ciclo Prioris - Execução 2026",
                                              "dataInicio": "2026-09-07",
                                              "dataFim": "2026-11-29",
                                              "status": "EM_ANDAMENTO",
                                              "dataCriacao": "2026-09-09T10:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou ciclo não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Ciclo não encontrado com o id informado."
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
    @GetMapping("/{idCiclo}")
    public ResponseEntity<CicloResponseDTO> buscarPorId(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo
    ) {

        return ResponseEntity.ok(
                cicloService.buscarPorId(
                        idUsuario,
                        idCiclo
                )
        );
    }


    /* =====================================================
       CADASTRAR CICLO
    ===================================================== */

    @Operation(
            summary = "Cadastrar ciclo de 12 semanas",
            description = """
                    Cria um novo ciclo de 12 semanas
                    para o usuário informado.

                    A partir da data inicial, o Prioris determina
                    o período correspondente ao ciclo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Ciclo criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idCiclo": 8,
                                              "idUsuario": 5,
                                              "titulo": "Ciclo Prioris - Execução 2026",
                                              "dataInicio": "2026-09-07",
                                              "dataFim": "2026-11-29",
                                              "status": "PLANEJADO",
                                              "dataCriacao": "2026-09-09T10:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de entrada inválidos"
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
    @PostMapping
    public ResponseEntity<CicloResponseDTO> cadastrar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para criar um ciclo de 12 semanas",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "titulo": "Ciclo Prioris - Execução 2026",
                                              "dataInicio": "2026-09-07"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            CicloRequestDTO dto
    ) {

        CicloResponseDTO ciclo =
                cicloService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ciclo);
    }


    /* =====================================================
       ATUALIZAR CICLO — PUT
    ===================================================== */

    @Operation(
            summary = "Atualizar ciclo",
            description = """
                    Atualiza integralmente os dados editáveis
                    de um ciclo de 12 semanas existente.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ciclo atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
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
    @PutMapping("/{idCiclo}")
    public ResponseEntity<CicloResponseDTO> atualizar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualização do ciclo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloAtualizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "titulo": "Ciclo Prioris - Entrega Final",
                                              "dataInicio": "2026-09-07",
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            CicloAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                cicloService.atualizar(
                        idUsuario,
                        idCiclo,
                        dto
                )
        );
    }


    /* =====================================================
       ATUALIZAR CICLO — PATCH
    ===================================================== */

    @Operation(
            summary = "Atualizar ciclo parcialmente",
            description = """
                    Atualiza somente os campos enviados,
                    preservando os demais dados do ciclo.

                    Pode ser utilizado, por exemplo, para
                    alterar apenas o status do ciclo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ciclo atualizado parcialmente com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
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
    @PatchMapping("/{idCiclo}")
    public ResponseEntity<CicloResponseDTO>
    atualizarParcialmente(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos do ciclo que deverão ser alterados",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloPatchDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": "EM_ANDAMENTO"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            CicloPatchDTO dto
    ) {

        return ResponseEntity.ok(
                cicloService.atualizarParcialmente(
                        idUsuario,
                        idCiclo,
                        dto
                )
        );
    }


    /* =====================================================
       EXCLUIR CICLO
    ===================================================== */

    @Operation(
            summary = "Excluir ciclo",
            description = """
                    Exclui o ciclo informado de acordo com
                    as regras de negócio do Prioris.

                    Quando a operação é concluída com sucesso,
                    não há conteúdo no corpo da resposta.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Ciclo excluído com sucesso"
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
    @DeleteMapping("/{idCiclo}")
    public ResponseEntity<Void> excluir(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo
    ) {

        cicloService.excluir(
                idUsuario,
                idCiclo
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    /* =====================================================
       ASSOCIAR OBJETIVO AO CICLO
    ===================================================== */

    @Operation(
            summary = "Associar objetivo ao ciclo",
            description = """
                    Associa um objetivo existente do usuário
                    a um ciclo de 12 semanas.

                    A associação permite definir quais objetivos
                    estratégicos serão trabalhados durante o ciclo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Objetivo associado ao ciclo com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CicloObjetivoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idCicloObjetivo": 10,
                                              "idCiclo": 8,
                                              "idObjetivo": 13,
                                              "tituloObjetivo": "Concluir o Prioris",
                                              "area": "DESENVOLVIMENTO_PROFISSIONAL",
                                              "statusObjetivo": "ATIVO"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Associação inválida"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, ciclo ou objetivo não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @PostMapping(
            "/{idCiclo}/objetivos/{idObjetivo}"
    )
    public ResponseEntity<CicloObjetivoResponseDTO>
    associarObjetivo(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo,

            @Parameter(
                    description = "Identificador do objetivo que será associado",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo
    ) {

        CicloObjetivoResponseDTO associacao =
                cicloService.associarObjetivo(
                        idUsuario,
                        idCiclo,
                        idObjetivo
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(associacao);
    }


    /* =====================================================
       LISTAR OBJETIVOS DO CICLO
    ===================================================== */

    @Operation(
            summary = "Listar objetivos do ciclo",
            description = """
                    Retorna todos os objetivos associados
                    ao ciclo de 12 semanas informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Objetivos do ciclo listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = CicloObjetivoResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idCicloObjetivo": 10,
                                                "idCiclo": 8,
                                                "idObjetivo": 13,
                                                "tituloObjetivo": "Concluir o Prioris",
                                                "area": "DESENVOLVIMENTO_PROFISSIONAL",
                                                "statusObjetivo": "ATIVO"
                                              }
                                            ]
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
    @GetMapping("/{idCiclo}/objetivos")
    public ResponseEntity<List<CicloObjetivoResponseDTO>>
    listarObjetivos(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo
    ) {

        return ResponseEntity.ok(
                cicloService.listarObjetivos(
                        idUsuario,
                        idCiclo
                )
        );
    }


    /* =====================================================
       REMOVER OBJETIVO DO CICLO
    ===================================================== */

    @Operation(
            summary = "Remover objetivo do ciclo",
            description = """
                    Remove a associação entre um objetivo
                    e um ciclo de 12 semanas.

                    O objetivo não é excluído do sistema;
                    apenas deixa de fazer parte daquele ciclo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Objetivo removido do ciclo com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário, ciclo, objetivo ou associação não encontrado"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado"
            )
    })
    @DeleteMapping(
            "/{idCiclo}/objetivos/{idObjetivo}"
    )
    public ResponseEntity<Void> removerObjetivo(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @Parameter(
                    description = "Identificador do ciclo",
                    example = "8",
                    required = true
            )
            @PathVariable Long idCiclo,

            @Parameter(
                    description = "Identificador do objetivo associado",
                    example = "13",
                    required = true
            )
            @PathVariable Long idObjetivo
    ) {

        cicloService.removerObjetivo(
                idUsuario,
                idCiclo,
                idObjetivo
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}