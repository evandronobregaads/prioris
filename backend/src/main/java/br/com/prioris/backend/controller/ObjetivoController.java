package br.com.prioris.backend.controller;

import br.com.prioris.backend.dto.ObjetivoAtualizacaoDTO;
import br.com.prioris.backend.dto.ObjetivoPatchDTO;
import br.com.prioris.backend.dto.ObjetivoRequestDTO;
import br.com.prioris.backend.dto.ObjetivoResponseDTO;
import br.com.prioris.backend.service.ObjetivoService;

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
@RequestMapping("/api/usuarios/{idUsuario}/objetivos")
@Tag(
        name = "Objetivos",
        description = "Gerenciamento dos objetivos pessoais e profissionais do usuário."
)
public class ObjetivoController {

    private final ObjetivoService objetivoService;


    public ObjetivoController(
            ObjetivoService objetivoService
    ) {
        this.objetivoService = objetivoService;
    }


    /* =====================================================
       LISTAR OBJETIVOS
    ===================================================== */

    @Operation(
            summary = "Listar objetivos",
            description = """
                    Retorna os objetivos cadastrados para o usuário informado.

                    Os objetivos representam resultados pessoais ou profissionais
                    que orientam as metas e tarefas estratégicas do Prioris.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Objetivos listados com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            implementation = ObjetivoResponseDTO.class
                                    )
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "idObjetivo": 13,
                                                "idUsuario": 5,
                                                "titulo": "Conquistar minha primeira oportunidade em tecnologia",
                                                "descricao": "Desenvolver conhecimentos e portfólio para ingressar profissionalmente na área.",
                                                "area": "CARREIRA",
                                                "motivo": "Realizar minha transição profissional para tecnologia.",
                                                "prazo": "2026-12-31",
                                                "status": "ATIVO",
                                                "dataCriacao": "2026-09-09T09:00:00"
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
    public ResponseEntity<List<ObjetivoResponseDTO>> listarTodos(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario
    ) {

        return ResponseEntity.ok(
                objetivoService.listarTodos(idUsuario)
        );
    }


    /* =====================================================
       BUSCAR OBJETIVO POR ID
    ===================================================== */

    @Operation(
            summary = "Buscar objetivo por ID",
            description = """
                    Retorna os dados de um objetivo específico
                    pertencente ao usuário informado.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Objetivo encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idObjetivo": 13,
                                              "idUsuario": 5,
                                              "titulo": "Conquistar minha primeira oportunidade em tecnologia",
                                              "descricao": "Desenvolver conhecimentos e portfólio para ingressar profissionalmente na área.",
                                              "area": "CARREIRA",
                                              "motivo": "Realizar minha transição profissional para tecnologia.",
                                              "prazo": "2026-12-31",
                                              "status": "ATIVO",
                                              "dataCriacao": "2026-09-09T09:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário ou objetivo não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "Objetivo não encontrado com o id informado."
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
    @GetMapping("/{idObjetivo}")
    public ResponseEntity<ObjetivoResponseDTO> buscarPorId(

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
            @PathVariable Long idObjetivo
    ) {

        return ResponseEntity.ok(
                objetivoService.buscarPorId(
                        idUsuario,
                        idObjetivo
                )
        );
    }


    /* =====================================================
       CADASTRAR OBJETIVO
    ===================================================== */

    @Operation(
            summary = "Cadastrar objetivo",
            description = """
                    Cria um novo objetivo para o usuário informado.

                    O objetivo pode representar um resultado pessoal
                    ou profissional e posteriormente receber metas
                    relacionadas à sua execução.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Objetivo criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoResponseDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "idObjetivo": 13,
                                              "idUsuario": 5,
                                              "titulo": "Concluir o Prioris",
                                              "descricao": "Finalizar o desenvolvimento e documentação da primeira versão do sistema.",
                                              "area": "DESENVOLVIMENTO_PROFISSIONAL",
                                              "motivo": "Utilizar o projeto como experiência acadêmica e portfólio.",
                                              "prazo": "2026-09-30",
                                              "status": "ATIVO",
                                              "dataCriacao": "2026-09-09T09:00:00"
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
                                              "message": "Os dados informados para o objetivo são inválidos."
                                            }
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
    @PostMapping
    public ResponseEntity<ObjetivoResponseDTO> cadastrar(

            @Parameter(
                    description = "Identificador do usuário",
                    example = "5",
                    required = true
            )
            @PathVariable Long idUsuario,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados necessários para cadastrar um objetivo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoRequestDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "titulo": "Concluir o Prioris",
                                              "descricao": "Finalizar o desenvolvimento e documentação da primeira versão do sistema.",
                                              "area": "DESENVOLVIMENTO_PROFISSIONAL",
                                              "motivo": "Utilizar o projeto como experiência acadêmica e portfólio.",
                                              "prazo": "2026-09-30"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            ObjetivoRequestDTO dto
    ) {

        ObjetivoResponseDTO objetivo =
                objetivoService.cadastrar(
                        idUsuario,
                        dto
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(objetivo);
    }


    /* =====================================================
       ATUALIZAR OBJETIVO — PUT
    ===================================================== */

    @Operation(
            summary = "Atualizar objetivo",
            description = """
                    Atualiza integralmente os dados editáveis
                    de um objetivo existente.

                    O PUT substitui os dados informados pelo novo
                    estado completo do objetivo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Objetivo atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
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
    @PutMapping("/{idObjetivo}")
    public ResponseEntity<ObjetivoResponseDTO> atualizar(

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

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados completos para atualização do objetivo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoAtualizacaoDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "titulo": "Finalizar e apresentar o Prioris",
                                              "descricao": "Concluir a V1 e preparar a apresentação final.",
                                              "area": "DESENVOLVIMENTO_PROFISSIONAL",
                                              "motivo": "Entregar um projeto completo e utilizá-lo no portfólio.",
                                              "prazo": "2026-09-30",
                                              "status": "ATIVO"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            ObjetivoAtualizacaoDTO dto
    ) {

        return ResponseEntity.ok(
                objetivoService.atualizar(
                        idUsuario,
                        idObjetivo,
                        dto
                )
        );
    }


    /* =====================================================
       ATUALIZAR OBJETIVO — PATCH
    ===================================================== */

    @Operation(
            summary = "Atualizar objetivo parcialmente",
            description = """
                    Atualiza somente os campos enviados na requisição,
                    preservando os demais dados do objetivo.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Objetivo atualizado parcialmente com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoResponseDTO.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados enviados são inválidos"
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
    @PatchMapping("/{idObjetivo}")
    public ResponseEntity<ObjetivoResponseDTO> atualizarParcialmente(

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

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Campos do objetivo que deverão ser alterados",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ObjetivoPatchDTO.class
                            ),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": "CONCLUIDO"
                                            }
                                            """
                            )
                    )
            )
            @Valid
            @RequestBody
            ObjetivoPatchDTO dto
    ) {

        return ResponseEntity.ok(
                objetivoService.atualizarParcialmente(
                        idUsuario,
                        idObjetivo,
                        dto
                )
        );
    }


    /* =====================================================
       CANCELAR OBJETIVO
    ===================================================== */

    @Operation(
            summary = "Cancelar objetivo",
            description = """
                    Cancela o objetivo informado de acordo com
                    as regras de negócio do Prioris.

                    O endpoint não retorna conteúdo quando
                    a operação é concluída com sucesso.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Objetivo cancelado com sucesso"
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
    @DeleteMapping("/{idObjetivo}")
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
            @PathVariable Long idObjetivo
    ) {

        objetivoService.excluir(
                idUsuario,
                idObjetivo
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}