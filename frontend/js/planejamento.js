import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";

/* =====================================================
   PRIORIS — PLANEJAMENTO SEMANAL
===================================================== */


/* =====================================================
   1. CONFIGURAÇÕES
===================================================== */

const ID_USUARIO_ATUAL =
    exigirAutenticacao();

const btnLogout =
    document.getElementById(
        "btn-logout"
    );


btnLogout?.addEventListener(
    "click",
    () => {

        const confirmar =
            window.confirm(
                "Deseja sair do Prioris?"
            );


        if (!confirmar) {
            return;
        }


        logout();
    }
);

let planejamentos = [];
let planejamentoAtual = null;

let ciclos = [];
let tarefas = [];
let tarefasPlanejamento = [];


/* =====================================================
   2. ELEMENTOS
===================================================== */

const secaoPlanejamentoAtual =
    document.getElementById("secao-planejamento-atual");

const secaoScore =
    document.getElementById("secao-score-planejamento");

const secaoTarefas =
    document.getElementById("secao-tarefas-planejamento");

const listaPlanejamentos =
    document.getElementById("lista-planejamentos");

const listaTarefasPlanejamento =
    document.getElementById("lista-tarefas-planejamento");

const selectTarefa =
    document.getElementById("select-tarefa-planejamento");

const modalPlanejamento =
    document.getElementById("modal-planejamento");

const formPlanejamento =
    document.getElementById("form-planejamento");


/* =====================================================
   3. INICIALIZAÇÃO
===================================================== */

async function carregarPagina() {

    console.log(
        "Planejamento.js iniciado. Usuário:",
        ID_USUARIO_ATUAL
    );


    await Promise.allSettled([
        carregarUsuario(),
        carregarCiclos(),
        carregarTarefas()
    ]);


    await carregarPlanejamentos();


    console.log(
        "Página de Planejamento carregada."
    );
}


/* =====================================================
   4. USUÁRIO
===================================================== */

async function carregarUsuario() {

    try {

        const usuario =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}`
            );


        const nome =
            document.getElementById(
                "nome-usuario"
            );


        if (nome) {

            nome.textContent =
                usuario.nome ||
                "Usuário Prioris";
        }


        const avatar =
            document.querySelector(
                ".user-avatar"
            );


        if (
            avatar &&
            usuario.nome
        ) {

            avatar.textContent =
                usuario.nome
                    .trim()
                    .charAt(0)
                    .toUpperCase();
        }

    } catch (erro) {

        console.error(
            "Erro ao carregar usuário:",
            erro
        );
    }
}


/* =====================================================
   5. CICLOS
===================================================== */

async function carregarCiclos() {

    try {

        ciclos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/ciclos`
            );


        if (!Array.isArray(ciclos)) {
            ciclos = [];
        }


        preencherSelectCiclos();


    } catch (erro) {

        console.error(
            "Erro ao carregar ciclos:",
            erro
        );

        ciclos = [];
    }
}


/* =====================================================
   6. TAREFAS DO USUÁRIO
===================================================== */

async function carregarTarefas() {

    try {

        tarefas =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/tarefas`
            );


        if (!Array.isArray(tarefas)) {
            tarefas = [];
        }


        console.log(
            "Tarefas do usuário:",
            tarefas
        );


    } catch (erro) {

        console.error(
            "Erro ao carregar tarefas:",
            erro
        );

        tarefas = [];
    }
}


/* =====================================================
   7. PLANEJAMENTOS
===================================================== */

async function carregarPlanejamentos() {

    try {

        planejamentos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais`
            );


        if (!Array.isArray(planejamentos)) {
            planejamentos = [];
        }


        definirTexto(
            "titulo-contagem-planejamentos",
            `${planejamentos.length} planejamento(s)`
        );


        planejamentoAtual =
            encontrarPlanejamentoAtual(
                planejamentos
            );


        /*
         * Busca o planejamento individualmente
         * para garantir Score e totais atualizados.
         */
        if (planejamentoAtual) {

            try {

                planejamentoAtual =
                    await apiFetch(
                        `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}`
                    );

            } catch (erro) {

                console.warn(
                    "Não foi possível buscar detalhes do planejamento:",
                    erro
                );
            }
        }


        renderizarPlanejamentoAtual();

        renderizarHistorico();


        if (planejamentoAtual) {

            await carregarTarefasPlanejamento();

        } else {

            secaoScore.classList.add(
                "hidden"
            );

            secaoTarefas.classList.add(
                "hidden"
            );
        }


    } catch (erro) {

        console.error(
            "Erro ao carregar planejamentos:",
            erro
        );


        secaoPlanejamentoAtual.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar os planejamentos.
            </div>
        `;


        listaPlanejamentos.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar o histórico.
            </div>
        `;
    }
}


/* =====================================================
   8. ENCONTRAR PLANEJAMENTO ATUAL
===================================================== */

function encontrarPlanejamentoAtual(lista) {

    const hoje =
        obterDataLocalISO();


    /*
     * Primeiro tenta localizar a semana
     * que contém a data de hoje.
     */
    const semanaAtual =
        lista.find(
            planejamento => {

                if (
                    !planejamento.dataInicioSemana ||
                    !planejamento.dataFimSemana
                ) {
                    return false;
                }


                return (
                    hoje >= planejamento.dataInicioSemana &&
                    hoje <= planejamento.dataFimSemana
                );
            }
        );


    if (semanaAtual) {
        return semanaAtual;
    }


    /*
     * Caso não exista uma semana contendo hoje,
     * pega o planejamento mais recente.
     */
    return (
        [...lista]
            .sort(
                (a, b) =>
                    String(
                        b.dataInicioSemana || ""
                    ).localeCompare(
                        String(
                            a.dataInicioSemana || ""
                        )
                    )
            )[0]
        || null
    );
}


/* =====================================================
   9. RENDERIZAR PLANEJAMENTO ATUAL
===================================================== */

function renderizarPlanejamentoAtual() {

    if (!planejamentoAtual) {

        secaoPlanejamentoAtual.innerHTML = `
            <div class="empty-state">

                <strong>
                    Nenhum planejamento semanal encontrado.
                </strong>

                <span>
                    Crie sua primeira semana estratégica
                    para começar a acompanhar sua execução.
                </span>

            </div>
        `;


        return;
    }


    const ciclo =
        ciclos.find(
            item =>
                Number(item.idCiclo) ===
                Number(planejamentoAtual.idCiclo)
        );


    secaoPlanejamentoAtual.innerHTML = `

        <div class="planejamento-atual-header">

            <div>

                <p class="card-eyebrow">
                    PLANEJAMENTO ATUAL
                </p>

                <h2>
                    ${
        planejamentoAtual.semanaCiclo
            ? `Semana ${planejamentoAtual.semanaCiclo} de 12`
            : "Planejamento Semanal"
    }
                </h2>

            </div>


            <button
                type="button"
                class="button-icon-edit"
                data-action="editar-planejamento-atual"
            >
                Editar planejamento
            </button>

        </div>


        <div class="planejamento-periodo">

            <span>
                📅
                ${formatarData(
        planejamentoAtual.dataInicioSemana
    )}
            </span>

            <span>
                →
            </span>

            <span>
                ${formatarData(
        planejamentoAtual.dataFimSemana
    )}
            </span>

        </div>


        ${
        ciclo
            ? `
                    <div class="planejamento-ciclo-info">

                        <span>
                            CICLO
                        </span>

                        <strong>
                            ${escaparHtml(
                ciclo.titulo
            )}
                        </strong>

                    </div>
                `
            : ""
    }


        ${
        planejamentoAtual.observacoes
            ? `
                    <div class="planejamento-foco-semana">

                        <span>
                            FOCO DA SEMANA
                        </span>

                        <p>
                            ${escaparHtml(
                planejamentoAtual.observacoes
            )}
                        </p>

                    </div>
                `
            : ""
    }
    `;


    atualizarScore();
}


/* =====================================================
   10. SCORE
===================================================== */

function atualizarScore() {

    if (!planejamentoAtual) {
        return;
    }


    secaoScore.classList.remove(
        "hidden"
    );


    const planejadas =
        Number(
            planejamentoAtual
                .totalTarefasPlanejadas ?? 0
        );


    const concluidas =
        Number(
            planejamentoAtual
                .totalTarefasConcluidas ?? 0
        );


    const score =
        Number(
            planejamentoAtual
                .scoreExecucao ?? 0
        );


    definirTexto(
        "total-tarefas-planejadas",
        planejadas
    );


    definirTexto(
        "total-tarefas-concluidas",
        concluidas
    );


    definirTexto(
        "score-execucao-planejamento",
        `${formatarPercentual(score)}%`
    );
}


/* =====================================================
   11. TAREFAS DO PLANEJAMENTO
===================================================== */

async function carregarTarefasPlanejamento() {

    if (!planejamentoAtual) {
        return;
    }


    try {

        tarefasPlanejamento =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}/tarefas`
            );


        if (!Array.isArray(tarefasPlanejamento)) {
            tarefasPlanejamento = [];
        }


        console.log(
            "Tarefas do planejamento:",
            tarefasPlanejamento
        );


        secaoTarefas.classList.remove(
            "hidden"
        );


        renderizarTarefasPlanejamento();

        preencherSelectTarefas();


    } catch (erro) {

        console.error(
            "Erro ao carregar tarefas do planejamento:",
            erro
        );


        tarefasPlanejamento = [];


        secaoTarefas.classList.remove(
            "hidden"
        );


        listaTarefasPlanejamento.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar as tarefas estratégicas.
            </div>
        `;


        preencherSelectTarefas();
    }
}


/* =====================================================
   12. NORMALIZAR TAREFA ASSOCIADA
===================================================== */

function normalizarTarefaPlanejamento(item) {

    if (!item) {
        return null;
    }


    /*
     * Caso o endpoint retorne algo como:
     *
     * {
     *   idTarefa: 10,
     *   tituloTarefa: "...",
     *   statusTarefa: "PENDENTE"
     * }
     */
    if (
        item.idTarefa &&
        item.tituloTarefa
    ) {

        const tarefaOriginal =
            tarefas.find(
                tarefa =>
                    Number(tarefa.idTarefa) ===
                    Number(item.idTarefa)
            );


        return {

            ...tarefaOriginal,

            idTarefa:
                Number(item.idTarefa),

            titulo:
            item.tituloTarefa,

            status:
                item.statusTarefa ??
                tarefaOriginal?.status,

            classificacaoAbcde:
                item.classificacaoAbcde ??
                tarefaOriginal?.classificacaoAbcde,

            tempoEstimado:
                item.tempoEstimado ??
                tarefaOriginal?.tempoEstimado,

            dataPlanejada:
                item.dataPlanejada ??
                tarefaOriginal?.dataPlanejada
        };
    }


    /*
     * Caso venha a tarefa completa.
     */
    if (
        item.idTarefa &&
        item.titulo
    ) {

        return {
            ...item,

            idTarefa:
                Number(
                    item.idTarefa
                )
        };
    }


    /*
     * Caso venha:
     * { tarefa: {...} }
     */
    if (
        item.tarefa &&
        item.tarefa.idTarefa
    ) {

        return {
            ...item.tarefa,

            idTarefa:
                Number(
                    item.tarefa.idTarefa
                )
        };
    }


    /*
     * Última tentativa:
     * localizar pelo ID.
     */
    const id =
        Number(
            item.idTarefa ??
            item.tarefaId ??
            item.id
        );


    if (!id) {
        return null;
    }


    return (
        tarefas.find(
            tarefa =>
                Number(tarefa.idTarefa) === id
        ) || null
    );
}


/* =====================================================
   13. RENDERIZAR TAREFAS ESTRATÉGICAS
===================================================== */

function renderizarTarefasPlanejamento() {

    if (!tarefasPlanejamento.length) {

        listaTarefasPlanejamento.innerHTML = `
            <div class="empty-state">

                Nenhuma tarefa estratégica
                adicionada nesta semana.

            </div>
        `;

        return;
    }


    const html =
        tarefasPlanejamento
            .map(
                item => {

                    const tarefa =
                        normalizarTarefaPlanejamento(
                            item
                        );


                    if (!tarefa) {
                        return "";
                    }


                    const concluida =
                        tarefa.status ===
                        "CONCLUIDA";


                    return `

                        <div
                            class="tarefa-planejamento-item"
                            data-tarefa-id="${tarefa.idTarefa}"
                        >

                            <div
                                class="
                                    abcde-badge
                                    badge-${String(
                        tarefa.classificacaoAbcde || "C"
                    ).toLowerCase()}
                                "
                            >
                                ${escaparHtml(
                        tarefa.classificacaoAbcde || "C"
                    )}
                            </div>


                            <div class="tarefa-planejamento-content">

                                <strong
                                    class="${
                        concluida
                            ? "task-completed"
                            : ""
                    }"
                                >
                                    ${escaparHtml(
                        tarefa.titulo
                    )}
                                </strong>


                                <div class="tarefa-planejamento-meta">

                                    <span>
                                        ${escaparHtml(
                        formatarStatus(
                            tarefa.status
                        )
                    )}
                                    </span>


                                    ${
                        tarefa.tempoEstimado
                            ? `
                                                <span>
                                                    ⏱
                                                    ${tarefa.tempoEstimado} min
                                                </span>
                                            `
                            : ""
                    }


                                    ${
                        tarefa.dataPlanejada
                            ? `
                                                <span>
                                                    📅
                                                    ${formatarData(
                                tarefa.dataPlanejada
                            )}
                                                </span>
                                            `
                            : ""
                    }

                                </div>

                            </div>


                            <button
                                type="button"
                                class="button-icon-delete"
                                data-action="remover-tarefa"
                            >
                                Remover
                            </button>

                        </div>
                    `;
                }
            )
            .join("");


    listaTarefasPlanejamento.innerHTML =
        html ||
        `
            <div class="empty-state">
                Nenhuma tarefa estratégica encontrada.
            </div>
        `;
}


/* =====================================================
   14. SELECT DE TAREFAS
===================================================== */

function preencherSelectTarefas() {

    const idsAssociados =
        new Set(
            tarefasPlanejamento
                .map(
                    item =>
                        normalizarTarefaPlanejamento(
                            item
                        )?.idTarefa
                )
                .filter(Boolean)
                .map(Number)
        );


    const disponiveis =
        tarefas.filter(
            tarefa => {

                const status =
                    String(
                        tarefa.status || ""
                    ).toUpperCase();


                const valida =
                    status !== "CANCELADA" &&
                    status !== "ELIMINADA";


                const associada =
                    idsAssociados.has(
                        Number(
                            tarefa.idTarefa
                        )
                    );


                return (
                    valida &&
                    !associada
                );
            }
        );


    if (!disponiveis.length) {

        selectTarefa.innerHTML = `
            <option value="">
                Nenhuma tarefa disponível
            </option>
        `;


        document
            .getElementById(
                "btn-adicionar-tarefa-planejamento"
            )
            .disabled = true;


        return;
    }


    selectTarefa.innerHTML = `

        <option value="">
            Selecione uma tarefa...
        </option>

        ${
        disponiveis
            .map(
                tarefa => `

                        <option
                            value="${tarefa.idTarefa}"
                        >
                            ${
                    tarefa.classificacaoAbcde
                        ? `[${escaparHtml(
                            tarefa.classificacaoAbcde
                        )}] `
                        : ""
                }

                            ${escaparHtml(
                    tarefa.titulo
                )}
                        </option>

                    `
            )
            .join("")
    }
    `;


    document
        .getElementById(
            "btn-adicionar-tarefa-planejamento"
        )
        .disabled = false;
}


/* =====================================================
   15. ADICIONAR TAREFA
===================================================== */

async function adicionarTarefaPlanejamento() {

    if (!planejamentoAtual) {
        return;
    }


    const idTarefa =
        Number(
            selectTarefa.value
        );


    if (!idTarefa) {

        alert(
            "Selecione uma tarefa."
        );

        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}/tarefas/${idTarefa}`,
            {
                method: "POST"
            }
        );


        /*
         * Recarrega o planejamento individual,
         * pois o número de tarefas planejadas
         * e o score podem ter mudado.
         */
        await atualizarPlanejamentoAtual();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   16. REMOVER TAREFA
===================================================== */

async function removerTarefaPlanejamento(
    idTarefa
) {

    if (!planejamentoAtual) {
        return;
    }


    const tarefa =
        tarefas.find(
            item =>
                Number(item.idTarefa) ===
                Number(idTarefa)
        );


    const confirmar =
        window.confirm(
            `Deseja remover a tarefa "${tarefa?.titulo || "selecionada"}" deste planejamento?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}/tarefas/${idTarefa}`,
            {
                method: "DELETE"
            }
        );


        await atualizarPlanejamentoAtual();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   17. ATUALIZAR PLANEJAMENTO ATUAL
===================================================== */

async function atualizarPlanejamentoAtual() {

    if (!planejamentoAtual) {
        return;
    }


    planejamentoAtual =
        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}`
        );


    renderizarPlanejamentoAtual();

    await carregarTarefasPlanejamento();

    /*
     * Também recarrega histórico para
     * refletir o Score atualizado.
     */
    await recarregarHistorico();
}


/* =====================================================
   18. RECARGA LEVE DO HISTÓRICO
===================================================== */

async function recarregarHistorico() {

    try {

        planejamentos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais`
            );


        if (!Array.isArray(planejamentos)) {
            planejamentos = [];
        }


        definirTexto(
            "titulo-contagem-planejamentos",
            `${planejamentos.length} planejamento(s)`
        );


        renderizarHistorico();


    } catch (erro) {

        console.error(
            "Erro ao atualizar histórico:",
            erro
        );
    }
}


/* =====================================================
   19. HISTÓRICO
===================================================== */

function renderizarHistorico() {

    if (!planejamentos.length) {

        listaPlanejamentos.innerHTML = `
            <div class="empty-state">
                Nenhum planejamento semanal cadastrado.
            </div>
        `;

        return;
    }


    const ordenados =
        [...planejamentos]
            .sort(
                (a, b) =>
                    String(
                        b.dataInicioSemana || ""
                    ).localeCompare(
                        String(
                            a.dataInicioSemana || ""
                        )
                    )
            );


    listaPlanejamentos.innerHTML =
        ordenados
            .map(
                criarHtmlPlanejamentoHistorico
            )
            .join("");
}


/* =====================================================
   20. CARD DO HISTÓRICO
===================================================== */

function criarHtmlPlanejamentoHistorico(
    planejamento
) {

    const atual =
        planejamentoAtual &&
        Number(
            planejamento.idPlanejamentoSemanal
        ) ===
        Number(
            planejamentoAtual.idPlanejamentoSemanal
        );


    const score =
        Number(
            planejamento.scoreExecucao ?? 0
        );


    return `

        <article
            class="
                planejamento-historico-card
                ${
        atual
            ? "planejamento-historico-atual"
            : ""
    }
            "
            data-planejamento-id="${planejamento.idPlanejamentoSemanal}"
        >

            <div class="planejamento-historico-header">

                <div>

                    <span class="card-eyebrow">
                        ${
        atual
            ? "SEMANA ATUAL"
            : "PLANEJAMENTO"
    }
                    </span>


                    <h3>
                        ${
        planejamento.semanaCiclo
            ? `Semana ${planejamento.semanaCiclo}`
            : "Semana planejada"
    }
                    </h3>

                </div>


                <strong class="planejamento-score-mini">
                    ${formatarPercentual(score)}%
                </strong>

            </div>


            <div class="planejamento-historico-periodo">

                <span>
                    ${formatarData(
        planejamento.dataInicioSemana
    )}
                </span>

                <span>
                    →
                </span>

                <span>
                    ${formatarData(
        planejamento.dataFimSemana
    )}
                </span>

            </div>


            <div class="planejamento-historico-numeros">

                <span>
                    ${planejamento.totalTarefasPlanejadas ?? 0}
                    planejada(s)
                </span>

                <span>
                    ${planejamento.totalTarefasConcluidas ?? 0}
                    concluída(s)
                </span>

            </div>


            ${
        planejamento.observacoes
            ? `
                        <p>
                            ${escaparHtml(
                planejamento.observacoes
            )}
                        </p>
                    `
            : ""
    }


            <div class="planejamento-historico-actions">

                <button
                    type="button"
                    class="button-icon-edit"
                    data-action="editar-planejamento"
                >
                    Editar
                </button>


                <button
                    type="button"
                    class="button-icon-delete"
                    data-action="excluir-planejamento"
                >
                    Excluir
                </button>

            </div>

        </article>
    `;
}


/* =====================================================
   21. SELECT DE CICLOS
===================================================== */

function preencherSelectCiclos() {

    const select =
        document.getElementById(
            "planejamento-ciclo"
        );


    select.innerHTML = `

        <option value="">
            Sem ciclo associado
        </option>

        ${
        ciclos
            .filter(
                ciclo =>
                    ciclo.status !==
                    "CANCELADO"
            )
            .map(
                ciclo => `

                        <option
                            value="${ciclo.idCiclo}"
                        >
                            ${escaparHtml(
                    ciclo.titulo
                )}
                        </option>

                    `
            )
            .join("")
    }
    `;
}


/* =====================================================
   22. NOVO PLANEJAMENTO
===================================================== */

function abrirModalNovoPlanejamento() {

    formPlanejamento.reset();


    document
        .getElementById(
            "planejamento-id"
        )
        .value = "";


    definirTexto(
        "modal-planejamento-titulo",
        "Nova semana"
    );


    /*
     * Se existir ciclo em andamento,
     * seleciona automaticamente.
     */
    const cicloAtual =
        ciclos.find(
            ciclo =>
                ciclo.status ===
                "EM_ANDAMENTO"
        );


    if (cicloAtual) {

        document
            .getElementById(
                "planejamento-ciclo"
            )
            .value =
            cicloAtual.idCiclo;


        const semana =
            calcularSemanaDoCiclo(
                cicloAtual
            );


        if (semana) {

            document
                .getElementById(
                    "planejamento-semana-ciclo"
                )
                .value =
                semana;
        }
    }


    /*
     * Segunda-feira da semana atual.
     */
    document
        .getElementById(
            "planejamento-data-inicio"
        )
        .value =
        obterInicioSemanaAtual();


    limparMensagemPlanejamento();


    modalPlanejamento
        .classList
        .remove("hidden");
}


/* =====================================================
   23. EDITAR PLANEJAMENTO
===================================================== */

function abrirModalEditarPlanejamento(
    planejamento
) {

    definirTexto(
        "modal-planejamento-titulo",
        "Editar planejamento"
    );


    document
        .getElementById(
            "planejamento-id"
        )
        .value =
        planejamento.idPlanejamentoSemanal;


    document
        .getElementById(
            "planejamento-ciclo"
        )
        .value =
        planejamento.idCiclo || "";


    document
        .getElementById(
            "planejamento-semana-ciclo"
        )
        .value =
        planejamento.semanaCiclo || "";


    document
        .getElementById(
            "planejamento-data-inicio"
        )
        .value =
        planejamento.dataInicioSemana
            ? String(
                planejamento.dataInicioSemana
            ).substring(0, 10)
            : "";


    document
        .getElementById(
            "planejamento-observacoes"
        )
        .value =
        planejamento.observacoes || "";


    limparMensagemPlanejamento();


    modalPlanejamento
        .classList
        .remove("hidden");
}


/* =====================================================
   24. SALVAR PLANEJAMENTO
===================================================== */

async function salvarPlanejamento(event) {

    event.preventDefault();


    const id =
        document
            .getElementById(
                "planejamento-id"
            )
            .value;


    const idCicloTexto =
        document
            .getElementById(
                "planejamento-ciclo"
            )
            .value;


    const semanaTexto =
        document
            .getElementById(
                "planejamento-semana-ciclo"
            )
            .value;


    const body = {

        idCiclo:
            idCicloTexto
                ? Number(idCicloTexto)
                : null,

        semanaCiclo:
            semanaTexto
                ? Number(semanaTexto)
                : null,

        dataInicioSemana:
        document
            .getElementById(
                "planejamento-data-inicio"
            )
            .value,

        observacoes:
            document
                .getElementById(
                    "planejamento-observacoes"
                )
                .value
                .trim() || null
    };


    try {

        if (id) {

            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${id}`,
                {
                    method: "PATCH",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );

        } else {

            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );
        }


        fecharModalPlanejamento();

        await carregarPlanejamentos();


    } catch (erro) {

        mostrarMensagem(
            "mensagem-form-planejamento",
            erro.message
        );
    }
}


/* =====================================================
   25. EXCLUIR PLANEJAMENTO
===================================================== */

async function excluirPlanejamento(
    planejamento
) {

    const confirmar =
        window.confirm(
            `Deseja realmente excluir o planejamento da semana iniciada em ${formatarData(
                planejamento.dataInicioSemana
            )}?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamento.idPlanejamentoSemanal}`,
            {
                method: "DELETE"
            }
        );


        await carregarPlanejamentos();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   26. EVENTOS — PLANEJAMENTO ATUAL
===================================================== */

secaoPlanejamentoAtual
    .addEventListener(
        "click",
        event => {

            const botao =
                event.target.closest(
                    "[data-action='editar-planejamento-atual']"
                );


            if (
                botao &&
                planejamentoAtual
            ) {

                abrirModalEditarPlanejamento(
                    planejamentoAtual
                );
            }
        }
    );


/* =====================================================
   27. EVENTOS — TAREFAS
===================================================== */

listaTarefasPlanejamento
    .addEventListener(
        "click",
        async event => {

            const botao =
                event.target.closest(
                    "[data-action='remover-tarefa']"
                );


            if (!botao) {
                return;
            }


            const item =
                botao.closest(
                    "[data-tarefa-id]"
                );


            if (!item) {
                return;
            }


            await removerTarefaPlanejamento(
                Number(
                    item.dataset.tarefaId
                )
            );
        }
    );


/* =====================================================
   28. EVENTOS — HISTÓRICO
===================================================== */

listaPlanejamentos
    .addEventListener(
        "click",
        async event => {

            const botao =
                event.target.closest(
                    "[data-action]"
                );


            if (!botao) {
                return;
            }


            const card =
                botao.closest(
                    "[data-planejamento-id]"
                );


            if (!card) {
                return;
            }


            const id =
                Number(
                    card.dataset
                        .planejamentoId
                );


            const planejamento =
                planejamentos.find(
                    item =>
                        Number(
                            item.idPlanejamentoSemanal
                        ) === id
                );


            if (!planejamento) {
                return;
            }


            if (
                botao.dataset.action ===
                "editar-planejamento"
            ) {

                abrirModalEditarPlanejamento(
                    planejamento
                );

                return;
            }


            if (
                botao.dataset.action ===
                "excluir-planejamento"
            ) {

                await excluirPlanejamento(
                    planejamento
                );
            }
        }
    );


/* =====================================================
   29. MODAL
===================================================== */

function fecharModalPlanejamento() {

    modalPlanejamento
        .classList
        .add("hidden");


    formPlanejamento.reset();

    limparMensagemPlanejamento();
}


/* =====================================================
   30. CÁLCULO DA SEMANA DO CICLO
===================================================== */

function calcularSemanaDoCiclo(ciclo) {

    if (!ciclo?.dataInicio) {
        return null;
    }


    const hoje =
        criarDataLocal(
            obterDataLocalISO()
        );


    const inicio =
        criarDataLocal(
            ciclo.dataInicio
        );


    if (hoje < inicio) {
        return 1;
    }


    const diferenca =
        hoje.getTime() -
        inicio.getTime();


    const dias =
        Math.floor(
            diferenca /
            86400000
        );


    return Math.min(
        Math.floor(
            dias / 7
        ) + 1,
        12
    );
}


/* =====================================================
   31. INÍCIO DA SEMANA ATUAL
===================================================== */

function obterInicioSemanaAtual() {

    const hoje =
        new Date();


    const diaSemana =
        hoje.getDay();


    /*
     * Domingo = 0
     * Segunda = 1
     */
    const diferenca =
        diaSemana === 0
            ? -6
            : 1 - diaSemana;


    const segunda =
        new Date(
            hoje.getFullYear(),
            hoje.getMonth(),
            hoje.getDate() + diferenca
        );


    return formatarDataISO(
        segunda
    );
}


/* =====================================================
   32. UTILITÁRIOS
===================================================== */

function obterDataLocalISO() {

    return formatarDataISO(
        new Date()
    );
}


function formatarDataISO(data) {

    const ano =
        data.getFullYear();


    const mes =
        String(
            data.getMonth() + 1
        ).padStart(
            2,
            "0"
        );


    const dia =
        String(
            data.getDate()
        ).padStart(
            2,
            "0"
        );


    return `${ano}-${mes}-${dia}`;
}


function criarDataLocal(valor) {

    const [
        ano,
        mes,
        dia
    ] =
        String(valor)
            .substring(0, 10)
            .split("-")
            .map(Number);


    return new Date(
        ano,
        mes - 1,
        dia
    );
}


function formatarData(data) {

    if (!data) {
        return "—";
    }


    const [
        ano,
        mes,
        dia
    ] =
        String(data)
            .substring(0, 10)
            .split("-");


    return `${dia}/${mes}/${ano}`;
}


function formatarPercentual(valor) {

    const numero =
        Number(valor || 0);


    if (
        Number.isInteger(numero)
    ) {
        return String(numero);
    }


    return numero
        .toFixed(2)
        .replace(".", ",");
}


function formatarStatus(valor) {

    if (!valor) {
        return "";
    }


    return String(valor)
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(
            /\b\w/g,
            letra =>
                letra.toUpperCase()
        );
}


function definirTexto(
    id,
    valor
) {

    const elemento =
        document.getElementById(id);


    if (elemento) {

        elemento.textContent =
            valor ?? "";
    }
}


function mostrarMensagem(
    id,
    mensagem
) {

    definirTexto(
        id,
        mensagem
    );
}


function limparMensagemPlanejamento() {

    definirTexto(
        "mensagem-form-planejamento",
        ""
    );
}


function escaparHtml(valor) {

    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/* =====================================================
   33. LISTENERS
===================================================== */

document
    .getElementById(
        "btn-novo-planejamento"
    )
    .addEventListener(
        "click",
        abrirModalNovoPlanejamento
    );


document
    .getElementById(
        "btn-fechar-planejamento"
    )
    .addEventListener(
        "click",
        fecharModalPlanejamento
    );


document
    .getElementById(
        "btn-cancelar-planejamento"
    )
    .addEventListener(
        "click",
        fecharModalPlanejamento
    );


document
    .getElementById(
        "btn-adicionar-tarefa-planejamento"
    )
    .addEventListener(
        "click",
        adicionarTarefaPlanejamento
    );


formPlanejamento
    .addEventListener(
        "submit",
        salvarPlanejamento
    );


/* =====================================================
   34. START
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    carregarPagina
);