package br.com.prioris.backend.service;

import br.com.prioris.backend.dto.*;
import br.com.prioris.backend.entity.Ciclo;
import br.com.prioris.backend.entity.CicloObjetivo;
import br.com.prioris.backend.entity.Objetivo;
import br.com.prioris.backend.entity.Usuario;
import br.com.prioris.backend.exception.ObjetivoJaAssociadoAoCicloException;
import br.com.prioris.backend.exception.RecursoNaoEncontradoException;
import br.com.prioris.backend.repository.CicloObjetivoRepository;
import br.com.prioris.backend.repository.CicloRepository;
import br.com.prioris.backend.repository.ObjetivoRepository;
import br.com.prioris.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CicloService {

    private final CicloRepository cicloRepository;
    private final CicloObjetivoRepository cicloObjetivoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjetivoRepository objetivoRepository;

    public CicloService(
            CicloRepository cicloRepository,
            CicloObjetivoRepository cicloObjetivoRepository,
            UsuarioRepository usuarioRepository,
            ObjetivoRepository objetivoRepository
    ) {
        this.cicloRepository = cicloRepository;
        this.cicloObjetivoRepository = cicloObjetivoRepository;
        this.usuarioRepository = usuarioRepository;
        this.objetivoRepository = objetivoRepository;
    }

    public List<CicloResponseDTO> listarTodos(Long idUsuario) {

        buscarUsuarioAtivo(idUsuario);

        return cicloRepository
                .findAllByUsuario_IdUsuarioAndStatusNotOrderByDataInicioDesc(
                        idUsuario,
                        "CANCELADO"
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public CicloResponseDTO buscarPorId(
            Long idUsuario,
            Long idCiclo
    ) {

        buscarUsuarioAtivo(idUsuario);

        return converterParaResponse(
                buscarCicloValido(idUsuario, idCiclo)
        );
    }

    public CicloResponseDTO cadastrar(
            Long idUsuario,
            CicloRequestDTO dto
    ) {

        Usuario usuario = buscarUsuarioAtivo(idUsuario);

        Ciclo ciclo = new Ciclo();

        ciclo.setUsuario(usuario);
        ciclo.setTitulo(dto.getTitulo().trim());
        ciclo.setDataInicio(dto.getDataInicio());
        ciclo.setDataFim(
                calcularDataFim(dto.getDataInicio())
        );
        ciclo.setStatus("PLANEJADO");

        Ciclo cicloSalvo =
                cicloRepository.save(ciclo);

        return converterParaResponse(cicloSalvo);
    }

    public CicloResponseDTO atualizar(
            Long idUsuario,
            Long idCiclo,
            CicloAtualizacaoDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        Ciclo ciclo =
                buscarCicloValido(idUsuario, idCiclo);

        ciclo.setTitulo(dto.getTitulo().trim());
        ciclo.setDataInicio(dto.getDataInicio());
        ciclo.setDataFim(
                calcularDataFim(dto.getDataInicio())
        );

        if (dto.getStatus() != null) {
            ciclo.setStatus(
                    dto.getStatus().trim().toUpperCase()
            );
        }

        Ciclo cicloAtualizado =
                cicloRepository.save(ciclo);

        return converterParaResponse(cicloAtualizado);
    }

    public CicloResponseDTO atualizarParcialmente(
            Long idUsuario,
            Long idCiclo,
            CicloPatchDTO dto
    ) {

        buscarUsuarioAtivo(idUsuario);

        Ciclo ciclo =
                buscarCicloValido(idUsuario, idCiclo);

        if (dto.getTitulo() != null) {

            String titulo = dto.getTitulo().trim();

            if (titulo.isBlank()) {
                throw new IllegalArgumentException(
                        "O título não pode ficar vazio"
                );
            }

            ciclo.setTitulo(titulo);
        }

        if (dto.getDataInicio() != null) {

            ciclo.setDataInicio(dto.getDataInicio());

            ciclo.setDataFim(
                    calcularDataFim(dto.getDataInicio())
            );
        }

        if (dto.getStatus() != null) {
            ciclo.setStatus(
                    dto.getStatus().trim().toUpperCase()
            );
        }

        Ciclo cicloAtualizado =
                cicloRepository.save(ciclo);

        return converterParaResponse(cicloAtualizado);
    }

    public void excluir(
            Long idUsuario,
            Long idCiclo
    ) {

        buscarUsuarioAtivo(idUsuario);

        Ciclo ciclo =
                buscarCicloValido(idUsuario, idCiclo);

        ciclo.setStatus("CANCELADO");

        cicloRepository.save(ciclo);
    }

    public CicloObjetivoResponseDTO associarObjetivo(
            Long idUsuario,
            Long idCiclo,
            Long idObjetivo
    ) {

        buscarUsuarioAtivo(idUsuario);

        Ciclo ciclo =
                buscarCicloValido(idUsuario, idCiclo);

        Objetivo objetivo =
                buscarObjetivoValido(
                        idUsuario,
                        idObjetivo
                );

        if (cicloObjetivoRepository
                .existsByCiclo_IdCicloAndObjetivo_IdObjetivo(
                        idCiclo,
                        idObjetivo
                )) {

            throw new ObjetivoJaAssociadoAoCicloException(
                    "O objetivo já está associado a este ciclo"
            );
        }

        CicloObjetivo associacao =
                new CicloObjetivo();

        associacao.setCiclo(ciclo);
        associacao.setObjetivo(objetivo);

        CicloObjetivo associacaoSalva =
                cicloObjetivoRepository.save(associacao);

        return converterAssociacaoParaResponse(
                associacaoSalva
        );
    }

    public List<CicloObjetivoResponseDTO> listarObjetivos(
            Long idUsuario,
            Long idCiclo
    ) {

        buscarUsuarioAtivo(idUsuario);
        buscarCicloValido(idUsuario, idCiclo);

        return cicloObjetivoRepository
                .findAllByCiclo_IdCicloOrderByIdCicloObjetivoAsc(
                        idCiclo
                )
                .stream()
                .map(this::converterAssociacaoParaResponse)
                .toList();
    }

    public void removerObjetivo(
            Long idUsuario,
            Long idCiclo,
            Long idObjetivo
    ) {

        buscarUsuarioAtivo(idUsuario);
        buscarCicloValido(idUsuario, idCiclo);

        CicloObjetivo associacao =
                cicloObjetivoRepository
                        .findByCiclo_IdCicloAndObjetivo_IdObjetivo(
                                idCiclo,
                                idObjetivo
                        )
                        .orElseThrow(() ->
                                new RecursoNaoEncontradoException(
                                        "O objetivo não está associado "
                                                + "a este ciclo"
                                )
                        );

        cicloObjetivoRepository.delete(associacao);
    }

    private LocalDate calcularDataFim(
            LocalDate dataInicio
    ) {

        return dataInicio
                .plusWeeks(12)
                .minusDays(1);
    }

    private Usuario buscarUsuarioAtivo(
            Long idUsuario
    ) {

        return usuarioRepository
                .findByIdUsuarioAndAtivoTrue(idUsuario)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado com o id: "
                                        + idUsuario
                        )
                );
    }

    private Ciclo buscarCicloValido(
            Long idUsuario,
            Long idCiclo
    ) {

        return cicloRepository
                .findByIdCicloAndUsuario_IdUsuarioAndStatusNot(
                        idCiclo,
                        idUsuario,
                        "CANCELADO"
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Ciclo não encontrado com o id: "
                                        + idCiclo
                        )
                );
    }

    private Objetivo buscarObjetivoValido(
            Long idUsuario,
            Long idObjetivo
    ) {

        return objetivoRepository
                .findByIdObjetivoAndUsuario_IdUsuarioAndStatusNot(
                        idObjetivo,
                        idUsuario,
                        "CANCELADO"
                )
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Objetivo não encontrado com o id: "
                                        + idObjetivo
                        )
                );
    }

    private CicloResponseDTO converterParaResponse(
            Ciclo ciclo
    ) {

        return new CicloResponseDTO(
                ciclo.getIdCiclo(),
                ciclo.getUsuario().getIdUsuario(),
                ciclo.getTitulo(),
                ciclo.getDataInicio(),
                ciclo.getDataFim(),
                ciclo.getStatus(),
                ciclo.getDataCriacao()
        );
    }

    private CicloObjetivoResponseDTO
    converterAssociacaoParaResponse(
            CicloObjetivo associacao
    ) {

        return new CicloObjetivoResponseDTO(
                associacao.getIdCicloObjetivo(),
                associacao.getCiclo().getIdCiclo(),
                associacao.getObjetivo().getIdObjetivo(),
                associacao.getObjetivo().getTitulo(),
                associacao.getObjetivo().getArea(),
                associacao.getObjetivo().getStatus()
        );
    }
}