import { apiFetch } from "./api.js";

import {
    exigirAutenticacao,
    logout
} from "./auth.js";


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
let objetivos = [];
let metas = [];

/* =====================================================
   CARREGAR METAS
===================================================== */

async function carregarMetas() {

    metas = [];

    try {

        objetivos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos`
            );


        if (!Array.isArray(objetivos)) {
            objetivos = [];
        }


        for (const objetivo of objetivos) {

            try {

                const metasDoObjetivo =
                    await apiFetch(
                        `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${objetivo.idObjetivo}/metas`
                    );


                if (!Array.isArray(metasDoObjetivo)) {
                    continue;
                }


                metasDoObjetivo.forEach(
                    meta => {

                        metas.push({

                            ...meta,

                            idObjetivo:
                            objetivo.idObjetivo,

                            tituloObjetivo:
                            objetivo.titulo
                        });

                    }
                );


            } catch (erro) {

                console.warn(
                    "Erro ao carregar metas do objetivo:",
                    objetivo.idObjetivo,
                    erro
                );
            }
        }


        preencherSelectObjetivos();

        preencherSelectMetas();


    } catch (erro) {

        console.error(
            "Erro ao carregar metas:",
            erro
        );
    }
}

/* =====================================================
   CONTROLAR TIPO DE VÍNCULO
===================================================== */

function atualizarCamposVinculo() {

    const tipo =
        document
            .getElementById(
                "tarefa-vinculo-tipo"
            )
            .value;

    const grupoObjetivo =
        document.getElementById(
            "grupo-tarefa-objetivo"
        );

    const grupoMeta =
        document.getElementById(
            "grupo-tarefa-meta"
        );

    const selectObjetivo =
        document.getElementById(
            "tarefa-objetivo"
        );

    const selectMeta =
        document.getElementById(
            "tarefa-meta"
        );


    /*
     * OBJETIVO
     */
    if (tipo === "OBJETIVO") {

        grupoObjetivo.classList.remove(
            "hidden"
        );

        grupoMeta.classList.add(
            "hidden"
        );

        /*
         * Garante que Meta e Objetivo
         * nunca fiquem selecionados juntos.
         */
        selectMeta.value = "";

        return;
    }


    /*
     * META
     */
    if (tipo === "META") {

        grupoMeta.classList.remove(
            "hidden"
        );

        grupoObjetivo.classList.add(
            "hidden"
        );

        selectObjetivo.value = "";

        return;
    }


    /*
     * SEM VÍNCULO
     */
    grupoObjetivo.classList.add(
        "hidden"
    );

    grupoMeta.classList.add(
        "hidden"
    );

    selectObjetivo.value = "";
    selectMeta.value = "";
}

/* =====================================================
   PREENCHER SELECT DE METAS
===================================================== */

function preencherSelectMetas() {

    const select =
        document.getElementById(
            "tarefa-meta"
        );


    if (!select) {
        return;
    }


    select.innerHTML = `

        <option value="">
            Nenhuma meta
        </option>

        ${
        metas
            .filter(
                meta =>
                    String(
                        meta.status || ""
                    ).toUpperCase() !==
                    "CANCELADA"
            )
            .map(
                meta => `

                        <option
                            value="${meta.idMeta}"
                        >
                            ${escaparHtml(meta.titulo)}
                            — ${escaparHtml(meta.tituloObjetivo)}
                        </option>

                    `
            )
            .join("")
    }
    `;
}

/* =====================================================
   PREENCHER SELECT DE OBJETIVOS
===================================================== */

function preencherSelectObjetivos() {

    const select =
        document.getElementById(
            "tarefa-objetivo"
        );

    if (!select) {
        return;
    }

    select.innerHTML = `
        <option value="">
            Selecione um objetivo
        </option>

        ${
        objetivos
            .filter(
                objetivo =>
                    String(
                        objetivo.status || ""
                    ).toUpperCase() !==
                    "CANCELADO"
            )
            .map(
                objetivo => `
                        <option
                            value="${objetivo.idObjetivo}"
                        >
                            ${escaparHtml(objetivo.titulo)}
                        </option>
                    `
            )
            .join("")
    }
    `;
}

/* =====================================================
   ELEMENTOS
===================================================== */

const lista =
    document.getElementById("lista-tarefas");

const filtroBusca =
    document.getElementById("filtro-busca");

const filtroStatus =
    document.getElementById("filtro-status");

const filtroAbcde =
    document.getElementById("filtro-abcde");

const modal =
    document.getElementById("modal-tarefa");

const formulario =
    document.getElementById("form-tarefa");


/* =====================================================
   CARREGAR TAREFAS
===================================================== */

async function carregarTarefas() {

    try {

        tarefas =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/tarefas`
            );

        renderizarTarefas();

    } catch (erro) {

        console.error(
            "Erro ao carregar tarefas:",
            erro
        );

        lista.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar as tarefas.
            </div>
        `;
    }
}

/* =====================================================
   CARREGAR USUÁRIO
===================================================== */

async function carregarUsuario() {

    try {

        const usuario =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}`
            );

        const nomeUsuario =
            document.getElementById(
                "nome-usuario"
            );

        if (nomeUsuario) {

            nomeUsuario.textContent =
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
   CARREGAR PÁGINA
===================================================== */

async function carregarPagina() {

    await carregarUsuario();

    await carregarMetas();

    await carregarTarefas();
}

/* =====================================================
   RENDERIZAÇÃO
===================================================== */

function renderizarTarefas() {

    const busca =
        filtroBusca.value
            .trim()
            .toLowerCase();

    const status =
        filtroStatus.value;

    const abcde =
        filtroAbcde.value;


    const filtradas =
        tarefas.filter(tarefa => {

            const correspondeBusca =
                !busca ||
                tarefa.titulo
                    .toLowerCase()
                    .includes(busca);

            const correspondeStatus =
                !status ||
                tarefa.status === status;

            const correspondeAbcde =
                !abcde ||
                tarefa.classificacaoAbcde === abcde;


            return (
                correspondeBusca &&
                correspondeStatus &&
                correspondeAbcde
            );
        });


    document
        .getElementById("titulo-contagem")
        .textContent =
        `${filtradas.length} tarefa(s)`;


    if (!filtradas.length) {

        lista.innerHTML = `
            <div class="empty-state">
                Nenhuma tarefa encontrada.
            </div>
        `;

        return;
    }


    lista.innerHTML =
        filtradas
            .map(criarHtmlTarefa)
            .join("");
}


/* =====================================================
   CARD DA TAREFA
===================================================== */

function criarHtmlTarefa(tarefa) {

    const concluida =
        tarefa.status === "CONCLUIDA";

    const abcde =
        tarefa.classificacaoAbcde || "-";


    return `
        <article
            class="tarefa-page-item"
            data-id="${tarefa.idTarefa}"
        >

            <button
                class="
                    task-check
                    ${concluida ? "completed" : ""}
                "
                data-action="concluir"
                title="Concluir tarefa"
                type="button"
            >
                ${concluida ? "✓" : ""}
            </button>


            <div class="tarefa-page-content">

                <div class="tarefa-page-title">

                    <strong
                        class="${concluida
        ? "task-completed"
        : ""}"
                    >
                        ${escaparHtml(tarefa.titulo)}
                    </strong>

                    <span
                        class="
                            abcde-badge
                            badge-${abcde.toLowerCase()}
                        "
                    >
                        ${abcde}
                    </span>

                </div>


                <div class="tarefa-page-meta">

                    <span>
                        ${formatarStatus(tarefa.status)}
                    </span>

                    ${
        tarefa.tempoEstimado
            ? `
                                <span>
                                    ⏱ ${tarefa.tempoEstimado} min
                                </span>
                              `
            : ""
    }

                    ${
        tarefa.dataPlanejada
            ? `
                                <span>
                                    📅 ${formatarData(tarefa.dataPlanejada)}
                                </span>
                              `
            : ""
    }

                </div>


                ${
        tarefa.descricao
            ? `
                            <p>
                                ${escaparHtml(tarefa.descricao)}
                            </p>
                          `
            : ""
    }

            </div>


            <div class="tarefa-page-actions">

                <button
                    data-action="editar"
                    class="button-icon-edit"
                    type="button"
                >
                    Editar
                </button>

                <button
                    data-action="excluir"
                    class="button-icon-delete"
                    type="button"
                >
                    Excluir
                </button>

            </div>

        </article>
    `;
}


/* =====================================================
   MODAL
===================================================== */

function abrirModalNovaTarefa() {

    formulario.reset();

    document
        .getElementById("tarefa-id")
        .value = "";

    document
        .getElementById("modal-titulo")
        .textContent =
        "Nova tarefa";

    document
        .getElementById("tarefa-status")
        .value =
        "PENDENTE";


    document
        .getElementById(
            "tarefa-vinculo-tipo"
        )
        .value = "";

    atualizarCamposVinculo();


    modal.classList.remove("hidden");
}

function fecharModal() {

    modal.classList.add(
        "hidden"
    );

    formulario.reset();

    document
        .getElementById(
            "mensagem-form"
        )
        .textContent = "";

    document
        .getElementById(
            "tarefa-vinculo-tipo"
        )
        .value = "";

    atualizarCamposVinculo();
}

/* =====================================================
   SALVAR
===================================================== */

async function salvarTarefa(event) {

    event.preventDefault();


    const id =
        document
            .getElementById(
                "tarefa-id"
            )
            .value;


    const tipoVinculo =
        document
            .getElementById(
                "tarefa-vinculo-tipo"
            )
            .value;


    const valorObjetivo =
        document
            .getElementById(
                "tarefa-objetivo"
            )
            .value;


    const valorMeta =
        document
            .getElementById(
                "tarefa-meta"
            )
            .value;


    /*
     * VALIDAÇÃO DO VÍNCULO
     */

    if (
        tipoVinculo === "OBJETIVO" &&
        !valorObjetivo
    ) {

        document
            .getElementById(
                "mensagem-form"
            )
            .textContent =
            "Selecione um objetivo.";

        return;
    }


    if (
        tipoVinculo === "META" &&
        !valorMeta
    ) {

        document
            .getElementById(
                "mensagem-form"
            )
            .textContent =
            "Selecione uma meta.";

        return;
    }


    /*
     * Apenas UM dos dois poderá
     * receber valor.
     */

    const idObjetivo =
        tipoVinculo === "OBJETIVO"
        && valorObjetivo
            ? Number(valorObjetivo)
            : null;


    const idMeta =
        tipoVinculo === "META"
        && valorMeta
            ? Number(valorMeta)
            : null;


    const body = {

        idMeta:
        idMeta,

        idObjetivo:
        idObjetivo,

        titulo:
            document
                .getElementById(
                    "tarefa-titulo"
                )
                .value
                .trim(),

        descricao:
            document
                .getElementById(
                    "tarefa-descricao"
                )
                .value
                .trim() || null,

        classificacaoAbcde:
            document
                .getElementById(
                    "tarefa-abcde"
                )
                .value || null,

        tempoEstimado:
            Number(
                document
                    .getElementById(
                        "tarefa-tempo"
                    )
                    .value
            ) || null,

        dataPlanejada:
            document
                .getElementById(
                    "tarefa-data"
                )
                .value || null
    };


    try {

        /*
         * EDIÇÃO COMPLETA
         *
         * PUT permite inclusive remover
         * Meta ou Objetivo.
         */
        if (id) {

            body.status =
                document
                    .getElementById(
                        "tarefa-status"
                    )
                    .value;


            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/tarefas/${id}`,
                {
                    method: "PUT",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );

        } else {

            /*
             * CADASTRO
             *
             * O backend define a tarefa
             * nova como PENDENTE.
             */
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/tarefas`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );
        }


        fecharModal();

        await carregarTarefas();


    } catch (erro) {

        document
            .getElementById(
                "mensagem-form"
            )
            .textContent =
            erro.message;

        console.error(erro);
    }
}


/* =====================================================
   AÇÕES
===================================================== */

async function tratarAcaoTarefa(event) {

    const botao =
        event.target.closest(
            "[data-action]"
        );

    if (!botao) {
        return;
    }


    const item =
        botao.closest(
            ".tarefa-page-item"
        );

    if (!item) {
        return;
    }


    const id =
        Number(item.dataset.id);

    const tarefa =
        tarefas.find(
            tarefa =>
                tarefa.idTarefa === id
        );


    if (!tarefa) {
        return;
    }


    const acao =
        botao.dataset.action;


    if (acao === "editar") {

        preencherFormulario(
            tarefa
        );

        return;
    }


    if (acao === "concluir") {

        await concluirTarefa(
            tarefa
        );

        return;
    }


    if (acao === "excluir") {

        await excluirTarefa(
            tarefa
        );
    }
}


/* =====================================================
   EDITAR
===================================================== */

function preencherFormulario(tarefa) {

    document
        .getElementById("modal-titulo")
        .textContent =
        "Editar tarefa";


    document
        .getElementById("tarefa-id")
        .value =
        tarefa.idTarefa;


    document
        .getElementById("tarefa-titulo")
        .value =
        tarefa.titulo || "";


    document
        .getElementById("tarefa-descricao")
        .value =
        tarefa.descricao || "";


    document
        .getElementById("tarefa-abcde")
        .value =
        tarefa.classificacaoAbcde || "";


    document
        .getElementById("tarefa-tempo")
        .value =
        tarefa.tempoEstimado || "";


    document
        .getElementById("tarefa-data")
        .value =
        tarefa.dataPlanejada || "";


    document
        .getElementById("tarefa-status")
        .value =
        tarefa.status || "PENDENTE";


    /* =====================================================
       VÍNCULO DA TAREFA
    ===================================================== */

    const tipoVinculo =
        document.getElementById(
            "tarefa-vinculo-tipo"
        );


    if (tarefa.idMeta) {

        tipoVinculo.value =
            "META";

        atualizarCamposVinculo();

        document
            .getElementById(
                "tarefa-meta"
            )
            .value =
            String(tarefa.idMeta);


    } else if (tarefa.idObjetivo) {

        tipoVinculo.value =
            "OBJETIVO";

        atualizarCamposVinculo();

        document
            .getElementById(
                "tarefa-objetivo"
            )
            .value =
            String(tarefa.idObjetivo);


    } else {

        tipoVinculo.value = "";

        atualizarCamposVinculo();
    }


    modal.classList.remove(
        "hidden"
    );
}
/* =====================================================
   CONCLUIR
===================================================== */

async function concluirTarefa(tarefa) {

    /*
     * Se já estiver concluída, não fazemos nada.
     */
    if (
        tarefa.status ===
        "CONCLUIDA"
    ) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/tarefas/${tarefa.idTarefa}`,
            {
                method: "PATCH",

                body:
                    JSON.stringify({
                        status:
                            "CONCLUIDA"
                    })
            }
        );


        await carregarTarefas();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   EXCLUIR
===================================================== */

async function excluirTarefa(tarefa) {

    const confirmar =
        window.confirm(
            `Deseja realmente eliminar a tarefa "${tarefa.titulo}"?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/tarefas/${tarefa.idTarefa}`,
            {
                method: "DELETE"
            }
        );


        await carregarTarefas();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   UTILITÁRIOS
===================================================== */

function formatarStatus(status) {

    if (!status) {
        return "";
    }

    return status
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(
            /\b\w/g,
            letra =>
                letra.toUpperCase()
        );
}


function formatarData(data) {

    if (!data) {
        return "";
    }

    const [
        ano,
        mes,
        dia
    ] =
        data
            .substring(0, 10)
            .split("-");


    return `${dia}/${mes}/${ano}`;
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
   EVENTOS
===================================================== */

document
    .getElementById("btn-nova-tarefa")
    .addEventListener(
        "click",
        abrirModalNovaTarefa
    );


document
    .getElementById("btn-fechar-modal")
    .addEventListener(
        "click",
        fecharModal
    );


document
    .getElementById("btn-cancelar")
    .addEventListener(
        "click",
        fecharModal
    );

document
    .getElementById(
        "tarefa-vinculo-tipo"
    )
    .addEventListener(
        "change",
        atualizarCamposVinculo
    );


formulario.addEventListener(
    "submit",
    salvarTarefa
);


lista.addEventListener(
    "click",
    tratarAcaoTarefa
);


filtroBusca.addEventListener(
    "input",
    renderizarTarefas
);


filtroStatus.addEventListener(
    "change",
    renderizarTarefas
);


filtroAbcde.addEventListener(
    "change",
    renderizarTarefas
);


/* =====================================================
   INICIALIZAÇÃO
===================================================== */

carregarPagina();