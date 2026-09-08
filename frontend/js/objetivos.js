import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";

/* =====================================================
   PRIORIS — OBJETIVOS E METAS
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

let objetivos = [];

const metasPorObjetivo = new Map();

let objetivoExpandidoId = null;


/* =====================================================
   2. ELEMENTOS
===================================================== */

const listaObjetivos =
    document.getElementById("lista-objetivos-page");

const filtroObjetivo =
    document.getElementById("filtro-objetivo");

const filtroArea =
    document.getElementById("filtro-area");

const filtroStatus =
    document.getElementById("filtro-status-objetivo");


const modalObjetivo =
    document.getElementById("modal-objetivo");

const formObjetivo =
    document.getElementById("form-objetivo");


const modalMeta =
    document.getElementById("modal-meta");

const formMeta =
    document.getElementById("form-meta");


/* =====================================================
   3. CARREGAMENTO PRINCIPAL
===================================================== */

async function carregarPagina() {

    await Promise.allSettled([
        carregarUsuario(),
        carregarObjetivos()
    ]);
}


/* =====================================================
   4. USUÁRIO DA SIDEBAR
===================================================== */

async function carregarUsuario() {

    try {

        const usuario =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}`
            );

        const nome =
            document.getElementById("nome-usuario");

        if (nome) {
            nome.textContent = usuario.nome;
        }


        const avatar =
            document.querySelector(".user-avatar");

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
   5. CARREGAR OBJETIVOS + METAS
===================================================== */

async function carregarObjetivos() {

    try {

        objetivos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos`
            );


        metasPorObjetivo.clear();


        /*
         * Carrega as metas de todos os objetivos.
         */
        await Promise.all(

            objetivos.map(
                async objetivo => {

                    try {

                        const metas =
                            await apiFetch(
                                `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${objetivo.idObjetivo}/metas`
                            );

                        metasPorObjetivo.set(
                            objetivo.idObjetivo,
                            metas
                        );

                    } catch (erro) {

                        console.error(
                            `Erro ao carregar metas do objetivo ${objetivo.idObjetivo}:`,
                            erro
                        );

                        metasPorObjetivo.set(
                            objetivo.idObjetivo,
                            []
                        );
                    }
                }
            )
        );


        atualizarResumo();

        renderizarObjetivos();


    } catch (erro) {

        console.error(
            "Erro ao carregar objetivos:",
            erro
        );

        listaObjetivos.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar os objetivos.
            </div>
        `;
    }
}


/* =====================================================
   6. RESUMO
===================================================== */

function atualizarResumo() {

    const ativos =
        objetivos.filter(
            objetivo =>
                objetivo.status === "ATIVO"
        ).length;


    let metasConcluidas = 0;

    let metasAndamento = 0;


    metasPorObjetivo.forEach(
        metas => {

            metas.forEach(
                meta => {

                    if (
                        meta.status ===
                        "CONCLUIDA"
                    ) {
                        metasConcluidas++;
                    }

                    if (
                        meta.status ===
                        "EM_ANDAMENTO"
                    ) {
                        metasAndamento++;
                    }
                }
            );
        }
    );


    definirTexto(
        "resumo-objetivos-ativos",
        ativos
    );

    definirTexto(
        "resumo-metas-concluidas",
        metasConcluidas
    );

    definirTexto(
        "resumo-metas-andamento",
        metasAndamento
    );
}


/* =====================================================
   7. FILTROS E RENDERIZAÇÃO
===================================================== */

function renderizarObjetivos() {

    const busca =
        filtroObjetivo
            .value
            .trim()
            .toLowerCase();

    const area =
        filtroArea.value;

    const status =
        filtroStatus.value;


    const filtrados =
        objetivos.filter(
            objetivo => {

                const titulo =
                    String(
                        objetivo.titulo || ""
                    ).toLowerCase();


                const correspondeBusca =
                    !busca ||
                    titulo.includes(busca);


                const correspondeArea =
                    !area ||
                    objetivo.area === area;


                const correspondeStatus =
                    !status ||
                    objetivo.status === status;


                return (
                    correspondeBusca &&
                    correspondeArea &&
                    correspondeStatus
                );
            }
        );


    definirTexto(
        "titulo-contagem-objetivos",
        `${filtrados.length} objetivo(s)`
    );


    if (!filtrados.length) {

        listaObjetivos.innerHTML = `
            <div class="empty-state">
                Nenhum objetivo encontrado.
            </div>
        `;

        return;
    }


    listaObjetivos.innerHTML =
        filtrados
            .map(criarHtmlObjetivo)
            .join("");
}


/* =====================================================
   8. CARD DO OBJETIVO
===================================================== */

function criarHtmlObjetivo(objetivo) {

    const metas =
        metasPorObjetivo.get(
            objetivo.idObjetivo
        ) || [];


    const progresso =
        calcularProgresso(metas);


    const metasValidas =
        metas.filter(
            meta =>
                meta.status !==
                "CANCELADA"
        );


    const concluidas =
        metasValidas.filter(
            meta =>
                meta.status ===
                "CONCLUIDA"
        ).length;


    const expandido =
        objetivoExpandidoId ===
        objetivo.idObjetivo;


    return `
        <article
            class="objetivo-page-card"
            data-objetivo-id="${objetivo.idObjetivo}"
        >

            <div class="objetivo-page-header">

                <div class="objetivo-identidade">

                    <div class="objetivo-area-icon">
                        ${iconeArea(objetivo.area)}
                    </div>


                    <div>

                        <span class="objetivo-area">
                            ${escaparHtml(
        formatarStatus(
            objetivo.area
        )
    )}
                        </span>

                        <h3>
                            ${escaparHtml(
        objetivo.titulo
    )}
                        </h3>

                    </div>

                </div>


                <span
                    class="objetivo-status objetivo-status-${String(
        objetivo.status
    ).toLowerCase()}"
                >
                    ${escaparHtml(
        formatarStatus(
            objetivo.status
        )
    )}
                </span>

            </div>


            ${
        objetivo.descricao
            ? `
                        <p class="objetivo-descricao">
                            ${escaparHtml(
                objetivo.descricao
            )}
                        </p>
                      `
            : ""
    }


            ${
        objetivo.motivo
            ? `
                        <div class="objetivo-motivo">
                            <strong>Por quê?</strong>
                            ${escaparHtml(
                objetivo.motivo
            )}
                        </div>
                      `
            : ""
    }


            <div class="objetivo-progress-section">

                <div class="objetivo-progress-header">

                    <span>
                        ${concluidas} de
                        ${metasValidas.length}
                        meta(s) concluída(s)
                    </span>

                    <strong>
                        ${progresso}%
                    </strong>

                </div>


                <div class="progress-track">

                    <div
                        class="progress-bar"
                        style="width: ${progresso}%"
                    ></div>

                </div>

            </div>


            <div class="objetivo-info-footer">

                <span>
                    📅
                    ${
        objetivo.prazo
            ? formatarData(
                objetivo.prazo
            )
            : "Sem prazo"
    }
                </span>

            </div>


            <div class="objetivo-actions">

                <button
                    type="button"
                    class="button-outline"
                    data-action="toggle-metas"
                >
                    ${expandido
        ? "Ocultar metas"
        : "Ver metas"}
                </button>


                <button
                    type="button"
                    class="button-meta"
                    data-action="nova-meta"
                >
                    + Meta
                </button>


                <button
                    type="button"
                    class="button-icon-edit"
                    data-action="editar-objetivo"
                >
                    Editar
                </button>


                <button
                    type="button"
                    class="button-icon-delete"
                    data-action="excluir-objetivo"
                >
                    Cancelar
                </button>

            </div>


            ${
        expandido
            ? criarHtmlListaMetas(
                objetivo,
                metas
            )
            : ""
    }

        </article>
    `;
}


/* =====================================================
   9. PROGRESSO
===================================================== */

function calcularProgresso(metas) {

    const validas =
        metas.filter(
            meta =>
                meta.status !==
                "CANCELADA"
        );


    if (!validas.length) {
        return 0;
    }


    const concluidas =
        validas.filter(
            meta =>
                meta.status ===
                "CONCLUIDA"
        ).length;


    return Math.round(
        (
            concluidas /
            validas.length
        ) * 100
    );
}


/* =====================================================
   10. LISTA DE METAS
===================================================== */

function criarHtmlListaMetas(
    objetivo,
    metas
) {

    const metasValidas =
        metas.filter(
            meta =>
                meta.status !==
                "CANCELADA"
        );


    if (!metasValidas.length) {

        return `
            <div class="metas-container">

                <div class="metas-header">

                    <div>
                        <span>METAS</span>
                        <strong>
                            ${escaparHtml(
            objetivo.titulo
        )}
                        </strong>
                    </div>

                </div>

                <div class="empty-state">
                    Nenhuma meta cadastrada neste objetivo.
                </div>

            </div>
        `;
    }


    return `
        <div class="metas-container">

            <div class="metas-header">

                <div>
                    <span>METAS</span>

                    <strong>
                        Caminho para alcançar este objetivo
                    </strong>
                </div>

            </div>


            <div class="metas-list">

                ${metasValidas
        .map(
            meta =>
                criarHtmlMeta(
                    objetivo.idObjetivo,
                    meta
                )
        )
        .join("")
    }

            </div>

        </div>
    `;
}


/* =====================================================
   11. ITEM DE META
===================================================== */

function criarHtmlMeta(
    idObjetivo,
    meta
) {

    const concluida =
        meta.status ===
        "CONCLUIDA";


    return `
        <div
            class="meta-item"
            data-meta-id="${meta.idMeta}"
            data-objetivo-id="${idObjetivo}"
        >

            <button
                type="button"
                class="task-check ${concluida ? "completed" : ""}"
                data-action="concluir-meta"
                ${concluida ? "disabled" : ""}
            >
                ${concluida ? "✓" : ""}
            </button>


            <div class="meta-content">

                <strong
                    class="${concluida
        ? "task-completed"
        : ""}"
                >
                    ${escaparHtml(
        meta.titulo
    )}
                </strong>


                <div class="meta-details">

                    <span>
                        ${escaparHtml(
        formatarStatus(
            meta.status
        )
    )}
                    </span>


                    <span>
                        📅
                        ${
        meta.prazo
            ? formatarData(
                meta.prazo
            )
            : "Sem prazo"
    }
                    </span>

                </div>


                ${
        meta.descricao
            ? `
                            <p>
                                ${escaparHtml(
                meta.descricao
            )}
                            </p>
                          `
            : ""
    }

            </div>


            <div class="meta-actions">

                <button
                    type="button"
                    class="button-icon-edit"
                    data-action="editar-meta"
                >
                    Editar
                </button>


                <button
                    type="button"
                    class="button-icon-delete"
                    data-action="excluir-meta"
                >
                    Cancelar
                </button>

            </div>

        </div>
    `;
}


/* =====================================================
   12. MODAL — NOVO OBJETIVO
===================================================== */

function abrirModalNovoObjetivo() {

    formObjetivo.reset();


    document
        .getElementById(
            "objetivo-id"
        )
        .value = "";


    definirTexto(
        "modal-objetivo-titulo",
        "Novo objetivo"
    );


    const status =
        document.getElementById(
            "objetivo-status"
        );

    status.value =
        "ATIVO";

    /*
     * Objetivos novos sempre começam ativos.
     */
    status.disabled =
        true;


    limparMensagemObjetivo();


    modalObjetivo
        .classList
        .remove("hidden");
}


/* =====================================================
   13. MODAL — EDITAR OBJETIVO
===================================================== */

function abrirModalEditarObjetivo(
    objetivo
) {

    definirTexto(
        "modal-objetivo-titulo",
        "Editar objetivo"
    );


    document
        .getElementById("objetivo-id")
        .value =
        objetivo.idObjetivo;


    document
        .getElementById("objetivo-titulo")
        .value =
        objetivo.titulo || "";


    document
        .getElementById("objetivo-descricao")
        .value =
        objetivo.descricao || "";


    document
        .getElementById("objetivo-motivo")
        .value =
        objetivo.motivo || "";


    document
        .getElementById("objetivo-area")
        .value =
        objetivo.area || "CARREIRA";


    document
        .getElementById("objetivo-prazo")
        .value =
        objetivo.prazo
            ? String(
                objetivo.prazo
            ).substring(0, 10)
            : "";


    const status =
        document.getElementById(
            "objetivo-status"
        );

    status.disabled =
        false;

    status.value =
        objetivo.status || "ATIVO";


    limparMensagemObjetivo();


    modalObjetivo
        .classList
        .remove("hidden");
}


/* =====================================================
   14. SALVAR OBJETIVO
===================================================== */

async function salvarObjetivo(event) {

    event.preventDefault();


    const id =
        document
            .getElementById(
                "objetivo-id"
            )
            .value;


    const body = {

        titulo:
            document
                .getElementById(
                    "objetivo-titulo"
                )
                .value
                .trim(),

        descricao:
            document
                .getElementById(
                    "objetivo-descricao"
                )
                .value
                .trim() || null,

        motivo:
            document
                .getElementById(
                    "objetivo-motivo"
                )
                .value
                .trim() || null,

        area:
        document
            .getElementById(
                "objetivo-area"
            )
            .value,

        prazo:
            document
                .getElementById(
                    "objetivo-prazo"
                )
                .value || null
    };


    try {

        if (id) {

            body.status =
                document
                    .getElementById(
                        "objetivo-status"
                    )
                    .value;


            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${id}`,
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
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );
        }


        fecharModalObjetivo();

        await carregarObjetivos();


    } catch (erro) {

        mostrarMensagem(
            "mensagem-form-objetivo",
            erro.message
        );
    }
}


/* =====================================================
   15. CANCELAR OBJETIVO
===================================================== */

async function excluirObjetivo(
    objetivo
) {

    const confirmar =
        window.confirm(
            `Deseja realmente cancelar o objetivo "${objetivo.titulo}"?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${objetivo.idObjetivo}`,
            {
                method: "DELETE"
            }
        );


        if (
            objetivoExpandidoId ===
            objetivo.idObjetivo
        ) {
            objetivoExpandidoId =
                null;
        }


        await carregarObjetivos();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   16. NOVA META
===================================================== */

function abrirModalNovaMeta(
    objetivo
) {

    formMeta.reset();


    document
        .getElementById(
            "meta-id"
        )
        .value = "";


    document
        .getElementById(
            "meta-id-objetivo"
        )
        .value =
        objetivo.idObjetivo;


    definirTexto(
        "modal-meta-titulo",
        "Nova meta"
    );


    definirTexto(
        "meta-objetivo-nome",
        objetivo.titulo
    );


    const status =
        document.getElementById(
            "meta-status"
        );

    /*
     * Meta nova nasce PENDENTE.
     */
    status.value =
        "PENDENTE";

    status.disabled =
        true;


    limparMensagemMeta();


    modalMeta
        .classList
        .remove("hidden");
}


/* =====================================================
   17. EDITAR META
===================================================== */

function abrirModalEditarMeta(
    idObjetivo,
    meta
) {

    document
        .getElementById(
            "meta-id"
        )
        .value =
        meta.idMeta;


    document
        .getElementById(
            "meta-id-objetivo"
        )
        .value =
        idObjetivo;


    definirTexto(
        "modal-meta-titulo",
        "Editar meta"
    );


    const objetivo =
        objetivos.find(
            item =>
                item.idObjetivo ===
                idObjetivo
        );


    definirTexto(
        "meta-objetivo-nome",
        objetivo?.titulo || ""
    );


    document
        .getElementById(
            "meta-titulo"
        )
        .value =
        meta.titulo || "";


    document
        .getElementById(
            "meta-descricao"
        )
        .value =
        meta.descricao || "";


    document
        .getElementById(
            "meta-prazo"
        )
        .value =
        meta.prazo
            ? String(
                meta.prazo
            ).substring(0, 10)
            : "";


    const status =
        document.getElementById(
            "meta-status"
        );

    status.disabled =
        false;

    status.value =
        meta.status ||
        "PENDENTE";


    limparMensagemMeta();


    modalMeta
        .classList
        .remove("hidden");
}


/* =====================================================
   18. SALVAR META
===================================================== */

async function salvarMeta(event) {

    event.preventDefault();


    const idMeta =
        document
            .getElementById(
                "meta-id"
            )
            .value;


    const idObjetivo =
        document
            .getElementById(
                "meta-id-objetivo"
            )
            .value;


    const body = {

        titulo:
            document
                .getElementById(
                    "meta-titulo"
                )
                .value
                .trim(),

        descricao:
            document
                .getElementById(
                    "meta-descricao"
                )
                .value
                .trim() || null,

        prazo:
            document
                .getElementById(
                    "meta-prazo"
                )
                .value || null
    };


    try {

        if (idMeta) {

            body.status =
                document
                    .getElementById(
                        "meta-status"
                    )
                    .value;


            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${idObjetivo}/metas/${idMeta}`,
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
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${idObjetivo}/metas`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );
        }


        fecharModalMeta();


        /*
         * Mantém o objetivo aberto depois da operação.
         */
        objetivoExpandidoId =
            Number(idObjetivo);


        await carregarObjetivos();


    } catch (erro) {

        mostrarMensagem(
            "mensagem-form-meta",
            erro.message
        );
    }
}


/* =====================================================
   19. CONCLUIR META
===================================================== */

async function concluirMeta(
    idObjetivo,
    meta
) {

    if (
        meta.status ===
        "CONCLUIDA"
    ) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${idObjetivo}/metas/${meta.idMeta}`,
            {
                method: "PATCH",

                body:
                    JSON.stringify({
                        status:
                            "CONCLUIDA"
                    })
            }
        );


        objetivoExpandidoId =
            idObjetivo;


        await carregarObjetivos();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   20. CANCELAR META
===================================================== */

async function excluirMeta(
    idObjetivo,
    meta
) {

    const confirmar =
        window.confirm(
            `Deseja realmente cancelar a meta "${meta.titulo}"?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${idObjetivo}/metas/${meta.idMeta}`,
            {
                method: "DELETE"
            }
        );


        objetivoExpandidoId =
            idObjetivo;


        await carregarObjetivos();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   21. EVENTOS DOS CARDS
===================================================== */

async function tratarAcao(
    event
) {

    const botao =
        event.target.closest(
            "[data-action]"
        );


    if (!botao) {
        return;
    }


    const acao =
        botao.dataset.action;


    /*
     * Ação dentro de uma meta.
     */
    const metaItem =
        botao.closest(
            ".meta-item"
        );


    if (metaItem) {

        const idObjetivo =
            Number(
                metaItem.dataset
                    .objetivoId
            );

        const idMeta =
            Number(
                metaItem.dataset
                    .metaId
            );


        const metas =
            metasPorObjetivo.get(
                idObjetivo
            ) || [];


        const meta =
            metas.find(
                item =>
                    item.idMeta ===
                    idMeta
            );


        if (!meta) {
            return;
        }


        if (
            acao ===
            "editar-meta"
        ) {

            abrirModalEditarMeta(
                idObjetivo,
                meta
            );

            return;
        }


        if (
            acao ===
            "concluir-meta"
        ) {

            await concluirMeta(
                idObjetivo,
                meta
            );

            return;
        }


        if (
            acao ===
            "excluir-meta"
        ) {

            await excluirMeta(
                idObjetivo,
                meta
            );

            return;
        }
    }


    /*
     * Ação do objetivo.
     */
    const card =
        botao.closest(
            ".objetivo-page-card"
        );


    if (!card) {
        return;
    }


    const idObjetivo =
        Number(
            card.dataset
                .objetivoId
        );


    const objetivo =
        objetivos.find(
            item =>
                item.idObjetivo ===
                idObjetivo
        );


    if (!objetivo) {
        return;
    }


    if (
        acao ===
        "toggle-metas"
    ) {

        objetivoExpandidoId =
            objetivoExpandidoId ===
            idObjetivo
                ? null
                : idObjetivo;


        renderizarObjetivos();

        return;
    }


    if (
        acao ===
        "nova-meta"
    ) {

        abrirModalNovaMeta(
            objetivo
        );

        return;
    }


    if (
        acao ===
        "editar-objetivo"
    ) {

        abrirModalEditarObjetivo(
            objetivo
        );

        return;
    }


    if (
        acao ===
        "excluir-objetivo"
    ) {

        await excluirObjetivo(
            objetivo
        );
    }
}


/* =====================================================
   22. FECHAR MODAIS
===================================================== */

function fecharModalObjetivo() {

    modalObjetivo
        .classList
        .add("hidden");

    formObjetivo.reset();

    limparMensagemObjetivo();
}


function fecharModalMeta() {

    modalMeta
        .classList
        .add("hidden");

    formMeta.reset();

    limparMensagemMeta();
}


/* =====================================================
   23. UTILITÁRIOS
===================================================== */

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


function limparMensagemObjetivo() {

    definirTexto(
        "mensagem-form-objetivo",
        ""
    );
}


function limparMensagemMeta() {

    definirTexto(
        "mensagem-form-meta",
        ""
    );
}


function formatarStatus(
    valor
) {

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


function formatarData(data) {

    if (!data) {
        return "";
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


function escaparHtml(valor) {

    return String(valor ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


function iconeArea(area) {

    const areaNormalizada =
        String(area || "")
            .toUpperCase()
            .normalize("NFD")
            .replace(
                /[\u0300-\u036f]/g,
                ""
            );


    const icones = {

        CARREIRA: "💼",

        FAMILIA: "🏡",

        FINANCAS: "💰",

        SAUDE: "🏃",

        DESENVOLVIMENTO: "📚",

        DESENVOLVIMENTO_PESSOAL: "📚",

        DESENVOLVIMENTO_PROFISSIONAL: "💻",

        SOCIAL: "🤝",

        COMUNITARIO: "🌎",

        SOCIAL_COMUNITARIO: "🌎"
    };


    return (
        icones[areaNormalizada] ||
        "🎯"
    );
}


/* =====================================================
   24. EVENT LISTENERS
===================================================== */

document
    .getElementById(
        "btn-novo-objetivo"
    )
    .addEventListener(
        "click",
        abrirModalNovoObjetivo
    );


document
    .getElementById(
        "btn-fechar-objetivo"
    )
    .addEventListener(
        "click",
        fecharModalObjetivo
    );


document
    .getElementById(
        "btn-cancelar-objetivo"
    )
    .addEventListener(
        "click",
        fecharModalObjetivo
    );


document
    .getElementById(
        "btn-fechar-meta"
    )
    .addEventListener(
        "click",
        fecharModalMeta
    );


document
    .getElementById(
        "btn-cancelar-meta"
    )
    .addEventListener(
        "click",
        fecharModalMeta
    );


formObjetivo
    .addEventListener(
        "submit",
        salvarObjetivo
    );


formMeta
    .addEventListener(
        "submit",
        salvarMeta
    );


listaObjetivos
    .addEventListener(
        "click",
        tratarAcao
    );


filtroObjetivo
    .addEventListener(
        "input",
        renderizarObjetivos
    );


filtroArea
    .addEventListener(
        "change",
        renderizarObjetivos
    );


filtroStatus
    .addEventListener(
        "change",
        renderizarObjetivos
    );


/* =====================================================
   25. INICIALIZAÇÃO
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    carregarPagina
);