import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";

/* =====================================================
   PRIORIS — PRIORIDADE #1
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

let tarefas = [];
let prioridadeAtual = null;
let historicoPrioridades = [];


/* =====================================================
   2. ELEMENTOS
===================================================== */

const secaoPrioridadeAtual =
    document.getElementById(
        "secao-prioridade-atual"
    );

const selectTarefa =
    document.getElementById(
        "select-tarefa-prioridade"
    );

const listaHistorico =
    document.getElementById(
        "lista-historico-prioridades"
    );

const mensagemPrioridade =
    document.getElementById(
        "mensagem-prioridade"
    );


/* =====================================================
   3. INICIALIZAÇÃO
===================================================== */

async function carregarPagina() {

    console.log(
        "Prioridade.js iniciado. Usuário:",
        ID_USUARIO_ATUAL
    );


    await Promise.allSettled([
        carregarUsuario(),
        carregarTarefas()
    ]);


    await carregarPrioridadeAtual();

    await carregarHistorico();


    console.log(
        "Página Prioridade #1 carregada."
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
   5. TAREFAS
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


        /*
         * Não queremos sugerir tarefas
         * canceladas ou eliminadas.
         */
        tarefas =
            tarefas.filter(
                tarefa => {

                    const status =
                        String(
                            tarefa.status || ""
                        ).toUpperCase();


                    return (
                        status !== "CANCELADA" &&
                        status !== "ELIMINADA"
                    );
                }
            );


        preencherSelectTarefas();


    } catch (erro) {

        console.error(
            "Erro ao carregar tarefas:",
            erro
        );

        tarefas = [];

        preencherSelectTarefas();
    }
}


/* =====================================================
   6. SELECT DE TAREFAS
===================================================== */

function preencherSelectTarefas() {

    const disponiveis =
        tarefas.filter(
            tarefa =>
                tarefa.status !==
                "CONCLUIDA"
        );


    if (!disponiveis.length) {

        selectTarefa.innerHTML = `
            <option value="">
                Nenhuma tarefa disponível
            </option>
        `;


        document
            .getElementById(
                "btn-definir-prioridade"
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
            "btn-definir-prioridade"
        )
        .disabled = false;
}


/* =====================================================
   7. CARREGAR PRIORIDADE DE HOJE
===================================================== */

async function carregarPrioridadeAtual() {

    try {

        prioridadeAtual =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria/hoje`
            );


        prioridadeAtual =
            normalizarPrioridade(
                prioridadeAtual
            );


        renderizarPrioridadeAtual();


    } catch (erro) {

        /*
         * O backend devolve 404 quando ainda
         * não existe prioridade no dia.
         *
         * Para a interface isso NÃO é falha:
         * é simplesmente o estado vazio.
         */
        if (
            ehPrioridadeNaoDefinida(
                erro
            )
        ) {

            prioridadeAtual = null;

            renderizarPrioridadeAtual();

            return;
        }


        console.error(
            "Erro ao carregar prioridade:",
            erro
        );


        secaoPrioridadeAtual.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                a prioridade de hoje.
            </div>
        `;
    }
}


/* =====================================================
   8. NORMALIZAR PRIORIDADE
===================================================== */

function normalizarPrioridade(item) {

    if (!item) {
        return null;
    }


    /*
     * Caso o DTO venha com:
     *
     * tituloTarefa
     * statusTarefa
     */
    if (
        item.idTarefa &&
        item.tituloTarefa
    ) {

        const tarefaOriginal =
            tarefas.find(
                tarefa =>
                    Number(
                        tarefa.idTarefa
                    ) ===
                    Number(
                        item.idTarefa
                    )
            );


        return {

            ...item,

            idTarefa:
                Number(item.idTarefa),

            titulo:
            item.tituloTarefa,

            status:
                item.statusTarefa ??
                tarefaOriginal?.status,

            classificacaoAbcde:
                item.classificacaoAbcde ??
                tarefaOriginal
                    ?.classificacaoAbcde,

            tempoEstimado:
                item.tempoEstimado ??
                tarefaOriginal
                    ?.tempoEstimado,

            dataPlanejada:
                item.dataPlanejada ??
                tarefaOriginal
                    ?.dataPlanejada
        };
    }


    /*
     * Caso já venha com os campos
     * da tarefa no formato normal.
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
     *
     * {
     *   tarefa: {...}
     * }
     */
    if (
        item.tarefa &&
        item.tarefa.idTarefa
    ) {

        return {

            ...item,

            ...item.tarefa,

            idTarefa:
                Number(
                    item.tarefa.idTarefa
                )
        };
    }


    /*
     * Se vier basicamente com o ID,
     * usamos os dados da lista de tarefas.
     */
    const idTarefa =
        Number(
            item.idTarefa ??
            item.tarefaId
        );


    const tarefa =
        tarefas.find(
            itemTarefa =>
                Number(
                    itemTarefa.idTarefa
                ) === idTarefa
        );


    if (!tarefa) {
        return item;
    }


    return {

        ...item,
        ...tarefa,

        idTarefa
    };
}


/* =====================================================
   9. RENDERIZAR PRIORIDADE ATUAL
===================================================== */

function renderizarPrioridadeAtual() {

    if (!prioridadeAtual) {

        secaoPrioridadeAtual.innerHTML = `

            <div class="prioridade-empty">

                <div class="prioridade-star">
                    ★
                </div>


                <div>

                    <p class="card-eyebrow">
                        PRIORIDADE #1 DE HOJE
                    </p>


                    <h2>
                        Nenhuma prioridade definida
                    </h2>


                    <p>
                        Escolha abaixo a tarefa que mais
                        contribuirá para o seu avanço hoje.
                    </p>

                </div>

            </div>
        `;


        atualizarBotaoDefinir();

        return;
    }


    const classificacao =
        prioridadeAtual
            .classificacaoAbcde || "A";


    secaoPrioridadeAtual.innerHTML = `

        <div class="prioridade-atual-header">

            <div>

                <p class="card-eyebrow">
                    ★ PRIORIDADE #1 DE HOJE
                </p>

                <span class="prioridade-data">
                    ${formatarData(
        prioridadeAtual.dataPrioridade ??
        prioridadeAtual.data
    )}
                </span>

            </div>

        </div>


        <div class="prioridade-principal">

            <div
                class="
                    abcde-badge
                    badge-${String(
        classificacao
    ).toLowerCase()}
                "
            >
                ${escaparHtml(
        classificacao
    )}
            </div>


            <div class="prioridade-principal-content">

                <h2>
                    ${escaparHtml(
        prioridadeAtual.titulo ||
        "Tarefa prioritária"
    )}
                </h2>


                <div class="prioridade-meta">

                    ${
        prioridadeAtual.status
            ? `
                                <span>
                                    ${escaparHtml(
                formatarStatus(
                    prioridadeAtual.status
                )
            )}
                                </span>
                              `
            : ""
    }


                    ${
        prioridadeAtual.tempoEstimado
            ? `
                                <span>
                                    ⏱
                                    ${prioridadeAtual.tempoEstimado}
                                    min
                                </span>
                              `
            : ""
    }


                    ${
        prioridadeAtual.dataPlanejada
            ? `
                                <span>
                                    📅
                                    ${formatarData(
                prioridadeAtual.dataPlanejada
            )}
                                </span>
                              `
            : ""
    }

                </div>

            </div>

        </div>


        <div class="prioridade-frase">

            <span>
                FOCO
            </span>

            <p>
                Faça primeiro aquilo que realmente importa.
            </p>

        </div>


        <div class="prioridade-atual-actions">

            ${
        prioridadeAtual.status !==
        "CONCLUIDA"
            ? `
                        <button
                            type="button"
                            class="secondary-focus-button"
                            data-action="concluir-prioridade"
                        >
                            ✓ Concluir tarefa
                        </button>
                      `
            : ""
    }


            <button
                type="button"
                class="button-outline"
                data-action="remover-prioridade"
            >
                Remover prioridade
            </button>

        </div>
    `;


    atualizarBotaoDefinir();
}


/* =====================================================
   10. DEFINIR / TROCAR PRIORIDADE
===================================================== */

async function definirPrioridade() {

    const idTarefa =
        Number(
            selectTarefa.value
        );


    if (!idTarefa) {

        mostrarMensagem(
            "Selecione uma tarefa."
        );

        return;
    }


    limparMensagem();


    try {

        /*
         * Se já existe prioridade:
         * PATCH.
         *
         * Se ainda não existe:
         * POST.
         */
        if (prioridadeAtual) {

            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria/hoje`,
                {
                    method: "PATCH",

                    body:
                        JSON.stringify({
                            idTarefa
                        })
                }
            );

        } else {

            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria`,
                {
                    method: "POST",

                    body:
                        JSON.stringify({
                            idTarefa
                        })
                }
            );
        }


        selectTarefa.value = "";


        await carregarPrioridadeAtual();

        await carregarHistorico();


    } catch (erro) {

        mostrarMensagem(
            erro.message
        );
    }
}


/* =====================================================
   11. CONCLUIR TAREFA PRIORITÁRIA
===================================================== */

async function concluirPrioridade() {

    if (
        !prioridadeAtual?.idTarefa
    ) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/tarefas/${prioridadeAtual.idTarefa}`,
            {
                method: "PATCH",

                body:
                    JSON.stringify({
                        status:
                            "CONCLUIDA"
                    })
            }
        );


        /*
         * Atualiza a lista geral de tarefas
         * para refletir o novo status.
         */
        await carregarTarefas();

        await carregarPrioridadeAtual();

        await carregarHistorico();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   12. REMOVER PRIORIDADE
===================================================== */

async function removerPrioridade() {

    if (!prioridadeAtual) {
        return;
    }


    const confirmar =
        window.confirm(
            `Deseja remover "${prioridadeAtual.titulo}" como Prioridade #1 de hoje?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria/hoje`,
            {
                method: "DELETE"
            }
        );


        prioridadeAtual = null;


        renderizarPrioridadeAtual();

        await carregarHistorico();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   13. HISTÓRICO
===================================================== */

async function carregarHistorico() {

    try {

        historicoPrioridades =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria/historico`
            );


        if (
            !Array.isArray(
                historicoPrioridades
            )
        ) {

            historicoPrioridades = [];
        }


        renderizarHistorico();


    } catch (erro) {

        console.error(
            "Erro ao carregar histórico:",
            erro
        );


        listaHistorico.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                o histórico de prioridades.
            </div>
        `;
    }
}


/* =====================================================
   14. RENDERIZAR HISTÓRICO
===================================================== */

function renderizarHistorico() {

    if (!historicoPrioridades.length) {

        listaHistorico.innerHTML = `
            <div class="empty-state">
                Você ainda não possui
                histórico de prioridades.
            </div>
        `;

        return;
    }


    const normalizadas =
        historicoPrioridades
            .map(
                normalizarPrioridade
            );


    listaHistorico.innerHTML =
        normalizadas
            .map(
                prioridade =>
                    criarHtmlHistorico(
                        prioridade
                    )
            )
            .join("");
}


/* =====================================================
   15. ITEM DO HISTÓRICO
===================================================== */

function criarHtmlHistorico(
    prioridade
) {

    const classificacao =
        prioridade
            .classificacaoAbcde || "A";


    const data =
        prioridade.dataPrioridade ??
        prioridade.data;


    return `

        <article class="prioridade-historico-item">

            <div class="prioridade-historico-data">

                <strong>
                    ${obterDia(
        data
    )}
                </strong>

                <span>
                    ${obterMesCurto(
        data
    )}
                </span>

            </div>


            <div
                class="
                    abcde-badge
                    badge-${String(
        classificacao
    ).toLowerCase()}
                "
            >

                ${escaparHtml(
        classificacao
    )}

            </div>


            <div class="prioridade-historico-content">

                <strong>
                    ${escaparHtml(
        prioridade.titulo ||
        "Tarefa"
    )}
                </strong>


                <span>
                    ${escaparHtml(
        formatarStatus(
            prioridade.status ||
            prioridade.statusTarefa
        )
    )}
                </span>

            </div>

        </article>
    `;
}


/* =====================================================
   16. BOTÃO DEFINIR / TROCAR
===================================================== */

function atualizarBotaoDefinir() {

    const botao =
        document.getElementById(
            "btn-definir-prioridade"
        );


    if (!botao) {
        return;
    }


    botao.textContent =
        prioridadeAtual
            ? "★ Trocar Prioridade #1"
            : "★ Definir como Prioridade #1";
}


/* =====================================================
   17. AÇÕES NO CARD
===================================================== */

secaoPrioridadeAtual
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


            const acao =
                botao.dataset.action;


            if (
                acao ===
                "concluir-prioridade"
            ) {

                await concluirPrioridade();

                return;
            }


            if (
                acao ===
                "remover-prioridade"
            ) {

                await removerPrioridade();
            }
        }
    );


/* =====================================================
   18. MENSAGENS
===================================================== */

function mostrarMensagem(
    mensagem
) {

    if (mensagemPrioridade) {

        mensagemPrioridade.textContent =
            mensagem || "";
    }
}


function limparMensagem() {

    mostrarMensagem("");
}


/* =====================================================
   19. 404 DE PRIORIDADE
===================================================== */

function ehPrioridadeNaoDefinida(
    erro
) {

    const mensagem =
        String(
            erro?.message || ""
        ).toLowerCase();


    return (
        mensagem.includes(
            "nenhuma prioridade"
        ) ||
        mensagem.includes(
            "prioridade #1"
        ) ||
        mensagem.includes(
            "não foi definida"
        ) ||
        mensagem.includes(
            "nao foi definida"
        )
    );
}


/* =====================================================
   20. DATAS
===================================================== */

function obterDia(data) {

    if (!data) {
        return "--";
    }


    return String(data)
        .substring(8, 10);
}


function obterMesCurto(data) {

    if (!data) {
        return "";
    }


    const meses = [
        "JAN",
        "FEV",
        "MAR",
        "ABR",
        "MAI",
        "JUN",
        "JUL",
        "AGO",
        "SET",
        "OUT",
        "NOV",
        "DEZ"
    ];


    const mes =
        Number(
            String(data)
                .substring(5, 7)
        );


    return (
        meses[mes - 1] || ""
    );
}


function formatarData(data) {

    if (!data) {
        return "—";
    }


    const valor =
        String(data)
            .substring(0, 10);


    const [
        ano,
        mes,
        dia
    ] =
        valor.split("-");


    return `${dia}/${mes}/${ano}`;
}


/* =====================================================
   21. TEXTO
===================================================== */

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


function escaparHtml(valor) {

    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/* =====================================================
   22. LISTENERS
===================================================== */

document
    .getElementById(
        "btn-definir-prioridade"
    )
    .addEventListener(
        "click",
        definirPrioridade
    );


/* =====================================================
   23. START
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    carregarPagina
);