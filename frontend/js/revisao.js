import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";

/* =====================================================
   PRIORIS — REVISÃO SEMANAL
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


/* =====================================================
   2. ESTADO
===================================================== */

let planejamentos = [];
let planejamentoAtual = null;

let revisaoAtual = null;
let revisoes = [];

let modoEdicao = false;


/* =====================================================
   3. ELEMENTOS
===================================================== */

const secaoRevisaoAtual =
    document.getElementById(
        "secao-revisao-atual"
    );

const scoreGrid =
    document.getElementById(
        "revisao-score-grid"
    );

const secaoForm =
    document.getElementById(
        "secao-form-revisao"
    );

const secaoRevisaoRealizada =
    document.getElementById(
        "secao-revisao-realizada"
    );

const listaRevisoes =
    document.getElementById(
        "lista-revisoes"
    );

const formRevisao =
    document.getElementById(
        "form-revisao"
    );

const btnCancelarEdicao =
    document.getElementById(
        "btn-cancelar-edicao-revisao"
    );


/* =====================================================
   4. INICIALIZAÇÃO
===================================================== */

async function carregarPagina() {

    console.log(
        "Revisão.js iniciado. Usuário:",
        ID_USUARIO_ATUAL
    );


    await carregarUsuario();

    await carregarPlanejamentos();

    await carregarHistorico();


    console.log(
        "Página Revisão Semanal carregada."
    );
}


/* =====================================================
   5. USUÁRIO
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
   6. PLANEJAMENTOS
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


        planejamentoAtual =
            encontrarPlanejamentoAtual(
                planejamentos
            );


        /*
         * Busca os detalhes atualizados
         * para garantir Score e totais corretos.
         */
        if (planejamentoAtual) {

            try {

                planejamentoAtual =
                    await apiFetch(
                        `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}`
                    );

            } catch (erro) {

                console.warn(
                    "Não foi possível buscar os detalhes do planejamento:",
                    erro
                );
            }
        }


        renderizarPlanejamento();


        if (planejamentoAtual) {

            await carregarRevisaoDoPlanejamento();

        } else {

            scoreGrid.classList.add(
                "hidden"
            );

            secaoForm.classList.add(
                "hidden"
            );

            secaoRevisaoRealizada.classList.add(
                "hidden"
            );
        }


    } catch (erro) {

        console.error(
            "Erro ao carregar planejamentos:",
            erro
        );


        secaoRevisaoAtual.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                o planejamento semanal.
            </div>
        `;
    }
}


/* =====================================================
   7. LOCALIZAR SEMANA ATUAL
===================================================== */

function encontrarPlanejamentoAtual(lista) {

    const hoje =
        obterDataLocalISO();


    /*
     * Prioridade:
     * planejamento cuja semana contém hoje.
     */
    const atual =
        lista.find(
            planejamento => {

                if (
                    !planejamento.dataInicioSemana ||
                    !planejamento.dataFimSemana
                ) {
                    return false;
                }


                return (
                    hoje >=
                    planejamento.dataInicioSemana &&

                    hoje <=
                    planejamento.dataFimSemana
                );
            }
        );


    if (atual) {
        return atual;
    }


    /*
     * Se não houver uma semana atual,
     * utiliza o planejamento mais recente.
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
   8. RENDERIZAR PLANEJAMENTO
===================================================== */

function renderizarPlanejamento() {

    if (!planejamentoAtual) {

        secaoRevisaoAtual.innerHTML = `
            <div class="empty-state">

                <strong>
                    Nenhum planejamento semanal encontrado.
                </strong>

                <span>
                    Crie primeiro um planejamento
                    para realizar a revisão da semana.
                </span>

            </div>
        `;

        return;
    }


    secaoRevisaoAtual.innerHTML = `

        <div class="revisao-planejamento-header">

            <div>

                <p class="card-eyebrow">
                    SEMANA EM ANÁLISE
                </p>

                <h2>
                    ${
        planejamentoAtual.semanaCiclo
            ? `Semana ${planejamentoAtual.semanaCiclo} de 12`
            : "Planejamento Semanal"
    }
                </h2>

            </div>

        </div>


        <div class="revisao-periodo">

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
        planejamentoAtual.observacoes
            ? `
                    <div class="revisao-foco-planejamento">

                        <span>
                            FOCO DEFINIDO PARA A SEMANA
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
   9. SCORE
===================================================== */

function atualizarScore() {

    if (!planejamentoAtual) {
        return;
    }


    scoreGrid.classList.remove(
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
        "revisao-total-planejadas",
        planejadas
    );


    definirTexto(
        "revisao-total-concluidas",
        concluidas
    );


    definirTexto(
        "revisao-score",
        `${formatarPercentual(score)}%`
    );
}


/* =====================================================
   10. CARREGAR REVISÃO DA SEMANA
===================================================== */

async function carregarRevisaoDoPlanejamento() {

    if (!planejamentoAtual) {
        return;
    }


    const idPlanejamento =
        planejamentoAtual
            .idPlanejamentoSemanal;


    try {

        revisaoAtual =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${idPlanejamento}/revisao`
            );


        revisaoAtual =
            normalizarRevisao(
                revisaoAtual
            );


        /*
         * Já existe revisão:
         * mostra resultado consolidado
         * e esconde formulário.
         */
        renderizarRevisaoRealizada();


    } catch (erro) {

        /*
         * Quando não existe revisão,
         * o backend pode responder 404.
         *
         * Isso é um estado normal:
         * significa que a semana ainda
         * precisa ser revisada.
         */
        if (
            ehRevisaoNaoEncontrada(
                erro
            )
        ) {

            revisaoAtual = null;

            mostrarFormularioNovaRevisao();

            return;
        }


        console.error(
            "Erro ao carregar revisão:",
            erro
        );


        secaoForm.classList.add(
            "hidden"
        );


        secaoRevisaoRealizada
            .classList.remove("hidden");


        secaoRevisaoRealizada.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível verificar
                a revisão desta semana.
            </div>
        `;
    }
}


/* =====================================================
   11. NORMALIZAR REVISÃO
===================================================== */

function normalizarRevisao(item) {

    if (!item) {
        return null;
    }


    return {

        ...item,

        idRevisaoSemanal:
            item.idRevisaoSemanal ??
            item.idRevisao ??
            item.id,

        idPlanejamentoSemanal:
            item.idPlanejamentoSemanal ??
            item.idPlanejamento ??
            planejamentoAtual
                ?.idPlanejamentoSemanal,

        scoreExecucao:
            item.scoreExecucao ??
            item.score ??
            planejamentoAtual
                ?.scoreExecucao ??
            0
    };
}


/* =====================================================
   12. NOVA REVISÃO
===================================================== */

function mostrarFormularioNovaRevisao() {

    modoEdicao = false;


    secaoRevisaoRealizada
        .classList
        .add("hidden");


    secaoForm
        .classList
        .remove("hidden");


    formRevisao.reset();


    document
        .getElementById(
            "revisao-id"
        )
        .value = "";


    document
        .getElementById(
            "revisao-id-planejamento"
        )
        .value =
        planejamentoAtual
            .idPlanejamentoSemanal;


    definirTexto(
        "titulo-form-revisao",
        "Revisar esta semana"
    );


    definirTexto(
        "btn-salvar-revisao",
        "✓ Salvar revisão"
    );


    btnCancelarEdicao
        .classList
        .add("hidden");


    limparMensagem();
}


/* =====================================================
   13. REVISÃO JÁ REALIZADA
===================================================== */

function renderizarRevisaoRealizada() {

    if (!revisaoAtual) {
        return;
    }


    modoEdicao = false;


    secaoForm
        .classList
        .add("hidden");


    secaoRevisaoRealizada
        .classList
        .remove("hidden");


    const score =
        Number(
            revisaoAtual.scoreExecucao ??
            planejamentoAtual?.scoreExecucao ??
            0
        );


    secaoRevisaoRealizada.innerHTML = `

        <div class="revisao-realizada-header">

            <div>

                <p class="card-eyebrow">
                    REVISÃO CONCLUÍDA
                </p>

                <h2>
                    Aprendizados da semana
                </h2>

            </div>


            <div class="revisao-score-snapshot">

                <span>
                    SCORE REGISTRADO
                </span>

                <strong>
                    ${formatarPercentual(score)}%
                </strong>

            </div>

        </div>


        <div class="revisao-realizada-grid">


            <article
                class="
                    revisao-resposta-card
                    revisao-resposta-conquistas
                "
            >

                <div class="revisao-resposta-header">

                    <span>✓</span>

                    <strong>
                        Principais conquistas
                    </strong>

                </div>


                <p>
                    ${formatarTextoLongo(
        revisaoAtual.principaisConquistas
    )}
                </p>

            </article>


            <article
                class="
                    revisao-resposta-card
                    revisao-resposta-dificuldades
                "
            >

                <div class="revisao-resposta-header">

                    <span>!</span>

                    <strong>
                        Dificuldades
                    </strong>

                </div>


                <p>
                    ${formatarTextoLongo(
        revisaoAtual.dificuldades
    )}
                </p>

            </article>


            <article
                class="
                    revisao-resposta-card
                    revisao-resposta-ajustes
                "
            >

                <div class="revisao-resposta-header">

                    <span>↗</span>

                    <strong>
                        Ajustes para a próxima semana
                    </strong>

                </div>


                <p>
                    ${formatarTextoLongo(
        revisaoAtual.ajustesProximaSemana
    )}
                </p>

            </article>

        </div>


        ${
        revisaoAtual.observacoes
            ? `
                    <div class="revisao-observacoes-realizada">

                        <span>
                            OBSERVAÇÕES
                        </span>

                        <p>
                            ${formatarTextoLongo(
                revisaoAtual.observacoes
            )}
                        </p>

                    </div>
                  `
            : ""
    }


        <div class="revisao-realizada-actions">

            <button
                type="button"
                class="button-icon-edit"
                data-action="editar-revisao"
            >
                Editar revisão
            </button>


            <button
                type="button"
                class="button-icon-delete"
                data-action="excluir-revisao"
            >
                Excluir revisão
            </button>

        </div>
    `;
}


/* =====================================================
   14. EDITAR REVISÃO
===================================================== */

function editarRevisao() {

    if (!revisaoAtual) {
        return;
    }


    modoEdicao = true;


    secaoRevisaoRealizada
        .classList
        .add("hidden");


    secaoForm
        .classList
        .remove("hidden");


    document
        .getElementById(
            "revisao-id"
        )
        .value =
        revisaoAtual.idRevisaoSemanal || "";


    document
        .getElementById(
            "revisao-id-planejamento"
        )
        .value =
        planejamentoAtual
            .idPlanejamentoSemanal;


    document
        .getElementById(
            "revisao-conquistas"
        )
        .value =
        revisaoAtual
            .principaisConquistas || "";


    document
        .getElementById(
            "revisao-dificuldades"
        )
        .value =
        revisaoAtual
            .dificuldades || "";


    document
        .getElementById(
            "revisao-ajustes"
        )
        .value =
        revisaoAtual
            .ajustesProximaSemana || "";


    document
        .getElementById(
            "revisao-observacoes"
        )
        .value =
        revisaoAtual
            .observacoes || "";


    definirTexto(
        "titulo-form-revisao",
        "Editar revisão da semana"
    );


    definirTexto(
        "btn-salvar-revisao",
        "✓ Salvar alterações"
    );


    btnCancelarEdicao
        .classList
        .remove("hidden");


    limparMensagem();
}


/* =====================================================
   15. CANCELAR EDIÇÃO
===================================================== */

function cancelarEdicao() {

    if (!revisaoAtual) {

        mostrarFormularioNovaRevisao();

        return;
    }


    modoEdicao = false;

    renderizarRevisaoRealizada();
}


/* =====================================================
   16. SALVAR REVISÃO
===================================================== */

async function salvarRevisao(event) {

    event.preventDefault();


    if (!planejamentoAtual) {
        return;
    }


    limparMensagem();


    const body = {

        principaisConquistas:
            document
                .getElementById(
                    "revisao-conquistas"
                )
                .value
                .trim(),

        dificuldades:
            document
                .getElementById(
                    "revisao-dificuldades"
                )
                .value
                .trim(),

        ajustesProximaSemana:
            document
                .getElementById(
                    "revisao-ajustes"
                )
                .value
                .trim(),

        observacoes:
            document
                .getElementById(
                    "revisao-observacoes"
                )
                .value
                .trim() || null
    };


    const idPlanejamento =
        planejamentoAtual
            .idPlanejamentoSemanal;


    try {

        bloquearSalvar(
            true
        );


        if (
            modoEdicao &&
            revisaoAtual
        ) {

            /*
             * O backend já possui PUT e PATCH.
             *
             * Usamos PATCH porque estamos
             * alterando dados da revisão existente.
             */
            revisaoAtual =
                await apiFetch(
                    `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${idPlanejamento}/revisao`,
                    {
                        method: "PATCH",

                        body:
                            JSON.stringify(
                                body
                            )
                    }
                );

        } else {

            /*
             * Só pode existir uma revisão
             * para cada planejamento.
             */
            revisaoAtual =
                await apiFetch(
                    `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${idPlanejamento}/revisao`,
                    {
                        method: "POST",

                        body:
                            JSON.stringify(
                                body
                            )
                    }
                );
        }


        revisaoAtual =
            normalizarRevisao(
                revisaoAtual
            );


        modoEdicao = false;


        renderizarRevisaoRealizada();

        await carregarHistorico();


    } catch (erro) {

        mostrarMensagem(
            erro.message
        );


    } finally {

        bloquearSalvar(
            false
        );
    }
}


/* =====================================================
   17. EXCLUIR REVISÃO
===================================================== */

async function excluirRevisao() {

    if (
        !revisaoAtual ||
        !planejamentoAtual
    ) {
        return;
    }


    const confirmar =
        window.confirm(
            "Deseja realmente excluir a revisão desta semana?"
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${planejamentoAtual.idPlanejamentoSemanal}/revisao`,
            {
                method: "DELETE"
            }
        );


        revisaoAtual = null;

        modoEdicao = false;


        mostrarFormularioNovaRevisao();

        await carregarHistorico();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   18. HISTÓRICO DE REVISÕES
===================================================== */

async function carregarHistorico() {

    try {

        revisoes =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/revisoes-semanais`
            );


        if (!Array.isArray(revisoes)) {
            revisoes = [];
        }


        revisoes =
            revisoes.map(
                normalizarRevisao
            );


        renderizarHistorico();


    } catch (erro) {

        console.error(
            "Erro ao carregar histórico de revisões:",
            erro
        );


        listaRevisoes.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                o histórico de revisões.
            </div>
        `;
    }
}


/* =====================================================
   19. RENDERIZAR HISTÓRICO
===================================================== */

function renderizarHistorico() {

    if (!revisoes.length) {

        listaRevisoes.innerHTML = `
            <div class="empty-state">
                Nenhuma revisão semanal registrada.
            </div>
        `;

        return;
    }


    const ordenadas =
        [...revisoes]
            .sort(
                (a, b) =>
                    obterIdRevisao(b) -
                    obterIdRevisao(a)
            );


    listaRevisoes.innerHTML =
        ordenadas
            .map(
                criarHtmlHistorico
            )
            .join("");
}


/* =====================================================
   20. CARD DO HISTÓRICO
===================================================== */

function criarHtmlHistorico(revisao) {

    const planejamento =
        encontrarPlanejamentoDaRevisao(
            revisao
        );


    const score =
        Number(
            revisao.scoreExecucao ?? 0
        );


    return `

        <article class="revisao-historico-card">

            <div class="revisao-historico-header">

                <div>

                    <span class="card-eyebrow">
                        ${
        planejamento?.semanaCiclo
            ? `SEMANA ${planejamento.semanaCiclo}`
            : "REVISÃO SEMANAL"
    }
                    </span>


                    <h3>
                        ${
        planejamento
            ? `${formatarData(
                planejamento.dataInicioSemana
            )} → ${formatarData(
                planejamento.dataFimSemana
            )}`
            : "Semana revisada"
    }
                    </h3>

                </div>


                <div class="revisao-historico-score">

                    <span>
                        SCORE
                    </span>

                    <strong>
                        ${formatarPercentual(score)}%
                    </strong>

                </div>

            </div>


            <div class="revisao-historico-destaque">

                <span>
                    ✓ PRINCIPAL CONQUISTA
                </span>

                <p>
                    ${formatarTextoLongo(
        revisao.principaisConquistas
    )}
                </p>

            </div>


            <div class="revisao-historico-footer">

                <span>
                    ↗ Próximo ajuste:
                </span>

                <strong>
                    ${formatarTextoLongo(
        revisao.ajustesProximaSemana
    )}
                </strong>

            </div>

        </article>
    `;
}


/* =====================================================
   21. LOCALIZAR PLANEJAMENTO DA REVISÃO
===================================================== */

function encontrarPlanejamentoDaRevisao(
    revisao
) {

    const id =
        Number(
            revisao.idPlanejamentoSemanal ??
            revisao.idPlanejamento
        );


    if (!id) {
        return null;
    }


    return (
        planejamentos.find(
            planejamento =>
                Number(
                    planejamento
                        .idPlanejamentoSemanal
                ) === id
        ) || null
    );
}


/* =====================================================
   22. AÇÕES DO CARD DA REVISÃO
===================================================== */

secaoRevisaoRealizada
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


            if (
                botao.dataset.action ===
                "editar-revisao"
            ) {

                editarRevisao();

                return;
            }


            if (
                botao.dataset.action ===
                "excluir-revisao"
            ) {

                await excluirRevisao();
            }
        }
    );


/* =====================================================
   23. IDENTIFICAR 404 NORMAL
===================================================== */

function ehRevisaoNaoEncontrada(erro) {

    /*
     * O caso correto quando ainda não existe
     * uma revisão para esta semana é 404.
     */
    if (erro?.status === 404) {
        return true;
    }


    const mensagem =
        String(
            erro?.message || ""
        ).toLowerCase();


    return (
        mensagem.includes("revisão não encontrada") ||
        mensagem.includes("revisao nao encontrada") ||
        mensagem.includes("revisão semanal não encontrada") ||
        mensagem.includes("revisao semanal nao encontrada")
    );
}


/* =====================================================
   24. MENSAGENS
===================================================== */

function mostrarMensagem(
    mensagem
) {

    definirTexto(
        "mensagem-form-revisao",
        mensagem || ""
    );
}


function limparMensagem() {

    mostrarMensagem("");
}


/* =====================================================
   25. BOTÃO SALVAR
===================================================== */

function bloquearSalvar(bloquear) {

    const botao =
        document.getElementById(
            "btn-salvar-revisao"
        );


    if (!botao) {
        return;
    }


    botao.disabled =
        bloquear;


    if (bloquear) {

        botao.textContent =
            "Salvando...";

    } else {

        botao.textContent =
            modoEdicao
                ? "✓ Salvar alterações"
                : "✓ Salvar revisão";
    }
}


/* =====================================================
   26. IDs
===================================================== */

function obterIdRevisao(revisao) {

    return Number(
        revisao?.idRevisaoSemanal ??
        revisao?.idRevisao ??
        revisao?.id ??
        0
    );
}


/* =====================================================
   27. DATAS
===================================================== */

function obterDataLocalISO() {

    const hoje =
        new Date();


    const ano =
        hoje.getFullYear();


    const mes =
        String(
            hoje.getMonth() + 1
        ).padStart(
            2,
            "0"
        );


    const dia =
        String(
            hoje.getDate()
        ).padStart(
            2,
            "0"
        );


    return `${ano}-${mes}-${dia}`;
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


/* =====================================================
   28. PERCENTUAL
===================================================== */

function formatarPercentual(valor) {

    const numero =
        Number(valor || 0);


    if (Number.isInteger(numero)) {
        return String(numero);
    }


    return numero
        .toFixed(2)
        .replace(".", ",");
}


/* =====================================================
   29. UTILITÁRIOS
===================================================== */

function definirTexto(id, valor) {

    const elemento =
        document.getElementById(id);


    if (elemento) {

        elemento.textContent =
            valor ?? "";
    }
}


function escaparHtml(valor) {

    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


function formatarTextoLongo(valor) {

    const seguro =
        escaparHtml(
            valor || "—"
        );


    return seguro.replaceAll(
        "\n",
        "<br>"
    );
}


/* =====================================================
   30. EVENTOS
===================================================== */

formRevisao
    .addEventListener(
        "submit",
        salvarRevisao
    );


btnCancelarEdicao
    .addEventListener(
        "click",
        cancelarEdicao
    );


/* =====================================================
   31. START
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    carregarPagina
);