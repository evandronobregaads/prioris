import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";

/* =====================================================
   PRIORIS — CICLO DE 12 SEMANAS
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

let ciclos = [];
let objetivos = [];
let cicloAtual = null;
let objetivosDoCiclo = [];


/* =====================================================
   ELEMENTOS
===================================================== */

const secaoCicloAtual =
    document.getElementById("secao-ciclo-atual");

const secaoObjetivosCiclo =
    document.getElementById("secao-objetivos-ciclo");

const listaCiclos =
    document.getElementById("lista-ciclos");

const listaObjetivosCiclo =
    document.getElementById("lista-objetivos-ciclo");

const selectObjetivo =
    document.getElementById("select-objetivo-ciclo");

const modalCiclo =
    document.getElementById("modal-ciclo");

const formCiclo =
    document.getElementById("form-ciclo");


/* =====================================================
   INICIALIZAÇÃO
===================================================== */

async function carregarPagina() {

    console.log(
        "Ciclo.js iniciado. Usuário:",
        ID_USUARIO_ATUAL
    );

    await carregarUsuario();

    await carregarObjetivos();

    await carregarCiclos();

    console.log(
        "Página de ciclos carregada."
    );
}


/* =====================================================
   USUÁRIO
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
   OBJETIVOS DO USUÁRIO
===================================================== */

async function carregarObjetivos() {

    try {

        objetivos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos`
            );


        if (!Array.isArray(objetivos)) {
            objetivos = [];
        }


        console.log(
            "Objetivos do usuário:",
            objetivos
        );

    } catch (erro) {

        console.error(
            "Erro ao carregar objetivos:",
            erro
        );

        objetivos = [];
    }
}


/* =====================================================
   CICLOS
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


        console.log(
            "Ciclos encontrados:",
            ciclos
        );


        definirTexto(
            "titulo-contagem-ciclos",
            `${ciclos.length} ciclo(s)`
        );


        cicloAtual =
            encontrarCicloAtual(
                ciclos
            );


        console.log(
            "Ciclo atual:",
            cicloAtual
        );


        await renderizarCicloAtual();

        renderizarHistorico();


    } catch (erro) {

        console.error(
            "Erro ao carregar ciclos:",
            erro
        );


        secaoCicloAtual.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar os ciclos.
            </div>
        `;


        listaCiclos.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar o histórico.
            </div>
        `;
    }
}


/* =====================================================
   LOCALIZAR CICLO ATUAL
===================================================== */

function encontrarCicloAtual(lista) {

    const emAndamento =
        lista.find(
            ciclo =>
                ciclo.status ===
                "EM_ANDAMENTO"
        );


    if (emAndamento) {
        return emAndamento;
    }


    const hoje =
        obterDataLocalISO();


    return (
        lista.find(
            ciclo => {

                if (
                    !ciclo.dataInicio ||
                    !ciclo.dataFim
                ) {
                    return false;
                }


                return (
                    hoje >= ciclo.dataInicio &&
                    hoje <= ciclo.dataFim
                );
            }
        ) || null
    );
}


/* =====================================================
   RENDERIZAR CICLO ATUAL
===================================================== */

async function renderizarCicloAtual() {

    if (!cicloAtual) {

        secaoCicloAtual.innerHTML = `
            <div class="empty-state ciclo-empty-state">

                <strong>
                    Nenhum ciclo em andamento.
                </strong>

                <span>
                    Crie um ciclo de 12 semanas
                    para iniciar sua execução estratégica.
                </span>

            </div>
        `;


        secaoObjetivosCiclo
            .classList
            .add("hidden");

        return;
    }


    const semana =
        calcularSemanaAtual(
            cicloAtual
        );


    const progresso =
        calcularProgressoTemporal(
            cicloAtual
        );


    secaoCicloAtual.innerHTML = `

        <div class="ciclo-atual-header">

            <div>

                <p class="card-eyebrow">
                    CICLO ATUAL
                </p>

                <h2>
                    ${escaparHtml(
        cicloAtual.titulo
    )}
                </h2>

            </div>


            <span
                class="
                    ciclo-status-badge
                    ciclo-status-${String(
        cicloAtual.status
    ).toLowerCase()}
                "
            >

                ${escaparHtml(
        formatarStatus(
            cicloAtual.status
        )
    )}

            </span>

        </div>


        ${
        cicloAtual.descricao
            ? `
                    <p class="ciclo-atual-descricao">
                        ${escaparHtml(
                cicloAtual.descricao
            )}
                    </p>
                  `
            : ""
    }


        <div class="ciclo-semana-destaque">

            <div>

                <span>
                    SEMANA ATUAL
                </span>

                <strong>
                    ${
        semana
            ? `${semana} de 12`
            : "Fora do período"
    }
                </strong>

            </div>


            <div class="ciclo-percentual">

                <strong>
                    ${progresso}%
                </strong>

                <span>
                    do período
                </span>

            </div>

        </div>


        <div class="progress-track">

            <div
                class="progress-bar"
                style="width: ${progresso}%"
            ></div>

        </div>


        <div class="ciclo-datas">

            <div>

                <span>
                    INÍCIO
                </span>

                <strong>
                    ${formatarData(
        cicloAtual.dataInicio
    )}
                </strong>

            </div>


            <span class="ciclo-data-arrow">
                →
            </span>


            <div>

                <span>
                    FIM
                </span>

                <strong>
                    ${formatarData(
        cicloAtual.dataFim
    )}
                </strong>

            </div>

        </div>


        <div class="ciclo-atual-actions">

            <button
                type="button"
                class="button-icon-edit"
                data-action="editar-ciclo-atual"
            >
                Editar ciclo
            </button>

        </div>
    `;


    secaoObjetivosCiclo
        .classList
        .remove("hidden");


    await carregarObjetivosDoCiclo();
}


/* =====================================================
   OBJETIVOS ASSOCIADOS AO CICLO
===================================================== */

async function carregarObjetivosDoCiclo() {

    if (!cicloAtual) {
        return;
    }


    try {

        objetivosDoCiclo =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/ciclos/${cicloAtual.idCiclo}/objetivos`
            );


        if (!Array.isArray(objetivosDoCiclo)) {
            objetivosDoCiclo = [];
        }


        console.log(
            "Objetivos associados ao ciclo:",
            objetivosDoCiclo
        );


        renderizarObjetivosDoCiclo();

        preencherSelectObjetivos();


    } catch (erro) {

        console.error(
            "Erro ao carregar objetivos do ciclo:",
            erro
        );


        objetivosDoCiclo = [];

        renderizarObjetivosDoCiclo();

        preencherSelectObjetivos();
    }
}


/* =====================================================
   NORMALIZAR DTO DO BACKEND
===================================================== */

function normalizarObjetivoAssociado(item) {

    if (!item) {
        return null;
    }


    /*
     * FORMATO REAL DO SEU BACKEND:
     *
     * {
     *   idCicloObjetivo: 4,
     *   idCiclo: 3,
     *   idObjetivo: 4,
     *   tituloObjetivo: "...",
     *   area: "CARREIRA",
     *   statusObjetivo: "ATIVO"
     * }
     */
    if (
        item.idObjetivo &&
        item.tituloObjetivo
    ) {

        return {

            idObjetivo:
                Number(
                    item.idObjetivo
                ),

            titulo:
            item.tituloObjetivo,

            area:
            item.area,

            status:
            item.statusObjetivo
        };
    }


    /*
     * Caso seja retornado
     * o objetivo completo.
     */
    if (
        item.idObjetivo &&
        item.titulo
    ) {

        return {

            ...item,

            idObjetivo:
                Number(
                    item.idObjetivo
                )
        };
    }


    /*
     * Caso exista:
     * { objetivo: {...} }
     */
    if (
        item.objetivo &&
        item.objetivo.idObjetivo
    ) {

        return {

            ...item.objetivo,

            idObjetivo:
                Number(
                    item.objetivo.idObjetivo
                )
        };
    }


    const id =
        Number(
            item.idObjetivo ??
            item.objetivoId ??
            item.id
        );


    if (!id) {
        return null;
    }


    return (
        objetivos.find(
            objetivo =>
                Number(
                    objetivo.idObjetivo
                ) === id
        ) || null
    );
}


/* =====================================================
   RENDERIZAR OBJETIVOS ASSOCIADOS
===================================================== */

function renderizarObjetivosDoCiclo() {

    if (!objetivosDoCiclo.length) {

        listaObjetivosCiclo.innerHTML = `
            <div class="empty-state">
                Nenhum objetivo associado a este ciclo.
            </div>
        `;

        return;
    }


    const html =
        objetivosDoCiclo
            .map(
                item => {

                    const objetivo =
                        normalizarObjetivoAssociado(
                            item
                        );


                    if (!objetivo) {
                        return "";
                    }


                    return `

                        <div
                            class="objetivo-ciclo-item"
                            data-objetivo-id="${objetivo.idObjetivo}"
                        >

                            <div class="objetivo-ciclo-icon">
                                ${iconeArea(
                        objetivo.area
                    )}
                            </div>


                            <div class="objetivo-ciclo-content">

                                <span>
                                    ${escaparHtml(
                        formatarStatus(
                            objetivo.area
                        )
                    )}
                                </span>

                                <strong>
                                    ${escaparHtml(
                        objetivo.titulo
                    )}
                                </strong>

                            </div>


                            <button
                                type="button"
                                class="button-icon-delete"
                                data-action="remover-objetivo"
                            >
                                Remover
                            </button>

                        </div>
                    `;
                }
            )
            .join("");


    listaObjetivosCiclo.innerHTML =
        html ||
        `
            <div class="empty-state">
                Nenhum objetivo associado.
            </div>
        `;
}


/* =====================================================
   SELECT DE OBJETIVOS DISPONÍVEIS
===================================================== */

function preencherSelectObjetivos() {

    const idsAssociados =
        new Set(
            objetivosDoCiclo
                .map(
                    item =>
                        normalizarObjetivoAssociado(
                            item
                        )?.idObjetivo
                )
                .filter(Boolean)
                .map(Number)
        );


    console.log(
        "IDs associados:",
        idsAssociados
    );


    const disponiveis =
        objetivos.filter(
            objetivo => {

                const status =
                    String(
                        objetivo.status || ""
                    )
                        .trim()
                        .toUpperCase();


                return (
                    status === "ATIVO" &&
                    !idsAssociados.has(
                        Number(
                            objetivo.idObjetivo
                        )
                    )
                );
            }
        );


    console.log(
        "Objetivos disponíveis:",
        disponiveis
    );


    if (!disponiveis.length) {

        selectObjetivo.innerHTML = `
            <option value="">
                Nenhum objetivo ativo disponível
            </option>
        `;


        document
            .getElementById(
                "btn-associar-objetivo"
            )
            .disabled = true;


        return;
    }


    selectObjetivo.innerHTML = `

        <option value="">
            Selecione um objetivo...
        </option>

        ${
        disponiveis
            .map(
                objetivo => `

                        <option
                            value="${objetivo.idObjetivo}"
                        >
                            ${escaparHtml(
                    objetivo.titulo
                )}
                        </option>

                    `
            )
            .join("")
    }
    `;


    document
        .getElementById(
            "btn-associar-objetivo"
        )
        .disabled = false;
}


/* =====================================================
   ASSOCIAR OBJETIVO
===================================================== */

async function associarObjetivo() {

    if (!cicloAtual) {
        return;
    }


    const idObjetivo =
        Number(
            selectObjetivo.value
        );


    if (!idObjetivo) {

        alert(
            "Selecione um objetivo."
        );

        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/ciclos/${cicloAtual.idCiclo}/objetivos/${idObjetivo}`,
            {
                method: "POST"
            }
        );


        await carregarObjetivosDoCiclo();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   REMOVER OBJETIVO DO CICLO
===================================================== */

async function removerObjetivo(
    idObjetivo
) {

    const objetivo =
        objetivos.find(
            item =>
                Number(
                    item.idObjetivo
                ) === Number(
                    idObjetivo
                )
        );


    const confirmar =
        window.confirm(
            `Deseja remover o objetivo "${objetivo?.titulo || "selecionado"}" deste ciclo?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/ciclos/${cicloAtual.idCiclo}/objetivos/${idObjetivo}`,
            {
                method: "DELETE"
            }
        );


        await carregarObjetivosDoCiclo();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   HISTÓRICO
===================================================== */

function renderizarHistorico() {

    if (!ciclos.length) {

        listaCiclos.innerHTML = `
            <div class="empty-state">
                Nenhum ciclo cadastrado.
            </div>
        `;

        return;
    }


    const ordenados =
        [...ciclos].sort(
            (a, b) =>
                String(
                    b.dataInicio || ""
                ).localeCompare(
                    String(
                        a.dataInicio || ""
                    )
                )
        );


    listaCiclos.innerHTML =
        ordenados
            .map(
                criarHtmlCicloHistorico
            )
            .join("");
}


/* =====================================================
   CARD DO HISTÓRICO
===================================================== */

function criarHtmlCicloHistorico(ciclo) {

    const atual =
        cicloAtual &&
        Number(ciclo.idCiclo) ===
        Number(cicloAtual.idCiclo);


    return `

        <article
            class="
                ciclo-historico-card
                ${atual
        ? "ciclo-historico-atual"
        : ""}
            "
            data-ciclo-id="${ciclo.idCiclo}"
        >

            <div class="ciclo-historico-header">

                <div>

                    <span class="card-eyebrow">
                        ${
        atual
            ? "CICLO ATUAL"
            : "CICLO DE 12 SEMANAS"
    }
                    </span>

                    <h3>
                        ${escaparHtml(
        ciclo.titulo
    )}
                    </h3>

                </div>


                <span
                    class="
                        ciclo-status-badge
                        ciclo-status-${String(
        ciclo.status
    ).toLowerCase()}
                    "
                >

                    ${escaparHtml(
        formatarStatus(
            ciclo.status
        )
    )}

                </span>

            </div>


            ${
        ciclo.descricao
            ? `
                        <p>
                            ${escaparHtml(
                ciclo.descricao
            )}
                        </p>
                    `
            : ""
    }


            <div class="ciclo-historico-datas">

                <span>
                    📅
                    ${formatarData(
        ciclo.dataInicio
    )}
                </span>

                <span>→</span>

                <span>
                    ${formatarData(
        ciclo.dataFim
    )}
                </span>

            </div>


            <div class="ciclo-historico-actions">

                <button
                    type="button"
                    class="button-icon-edit"
                    data-action="editar-ciclo"
                >
                    Editar
                </button>


                ${
        ciclo.status !==
        "CANCELADO"
            ? `
                            <button
                                type="button"
                                class="button-icon-delete"
                                data-action="cancelar-ciclo"
                            >
                                Cancelar
                            </button>
                          `
            : ""
    }

            </div>

        </article>
    `;
}


/* =====================================================
   NOVO CICLO
===================================================== */

function abrirModalNovoCiclo() {

    formCiclo.reset();


    document
        .getElementById(
            "ciclo-id"
        )
        .value = "";


    definirTexto(
        "modal-ciclo-titulo",
        "Novo ciclo"
    );


    document
        .getElementById(
            "ciclo-status-form"
        )
        .value =
        "PLANEJADO";


    limparMensagemCiclo();


    modalCiclo
        .classList
        .remove("hidden");
}


/* =====================================================
   EDITAR CICLO
===================================================== */

function abrirModalEditarCiclo(
    ciclo
) {

    definirTexto(
        "modal-ciclo-titulo",
        "Editar ciclo"
    );


    document
        .getElementById(
            "ciclo-id"
        )
        .value =
        ciclo.idCiclo;


    document
        .getElementById(
            "ciclo-titulo"
        )
        .value =
        ciclo.titulo || "";


    document
        .getElementById(
            "ciclo-descricao"
        )
        .value =
        ciclo.descricao || "";


    document
        .getElementById(
            "ciclo-data-inicio"
        )
        .value =
        ciclo.dataInicio
            ? String(
                ciclo.dataInicio
            ).substring(0, 10)
            : "";


    document
        .getElementById(
            "ciclo-status-form"
        )
        .value =
        ciclo.status ||
        "PLANEJADO";


    limparMensagemCiclo();


    modalCiclo
        .classList
        .remove("hidden");
}


/* =====================================================
   SALVAR CICLO
===================================================== */

async function salvarCiclo(event) {

    event.preventDefault();


    const id =
        document
            .getElementById(
                "ciclo-id"
            )
            .value;


    const body = {

        titulo:
            document
                .getElementById(
                    "ciclo-titulo"
                )
                .value
                .trim(),

        descricao:
            document
                .getElementById(
                    "ciclo-descricao"
                )
                .value
                .trim() || null,

        dataInicio:
        document
            .getElementById(
                "ciclo-data-inicio"
            )
            .value,

        status:
        document
            .getElementById(
                "ciclo-status-form"
            )
            .value
    };


    try {

        if (id) {

            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/ciclos/${id}`,
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
                `/usuarios/${ID_USUARIO_ATUAL}/ciclos`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );
        }


        fecharModalCiclo();

        await carregarCiclos();


    } catch (erro) {

        mostrarMensagem(
            "mensagem-form-ciclo",
            erro.message
        );
    }
}


/* =====================================================
   CANCELAR CICLO
===================================================== */

async function cancelarCiclo(
    ciclo
) {

    const confirmar =
        window.confirm(
            `Deseja realmente cancelar o ciclo "${ciclo.titulo}"?`
        );


    if (!confirmar) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/ciclos/${ciclo.idCiclo}`,
            {
                method: "DELETE"
            }
        );


        await carregarCiclos();


    } catch (erro) {

        alert(
            erro.message
        );
    }
}


/* =====================================================
   SEMANA ATUAL
===================================================== */

function calcularSemanaAtual(ciclo) {

    if (
        !ciclo?.dataInicio ||
        !ciclo?.dataFim
    ) {
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

    const fim =
        criarDataLocal(
            ciclo.dataFim
        );


    if (
        hoje < inicio ||
        hoje > fim
    ) {
        return null;
    }


    const diferenca =
        hoje.getTime() -
        inicio.getTime();


    const dias =
        Math.floor(
            diferenca / 86400000
        );


    return Math.min(
        Math.floor(
            dias / 7
        ) + 1,
        12
    );
}


/* =====================================================
   PROGRESSO TEMPORAL
===================================================== */

function calcularProgressoTemporal(ciclo) {

    if (
        !ciclo?.dataInicio ||
        !ciclo?.dataFim
    ) {
        return 0;
    }


    const hoje =
        criarDataLocal(
            obterDataLocalISO()
        );

    const inicio =
        criarDataLocal(
            ciclo.dataInicio
        );

    const fim =
        criarDataLocal(
            ciclo.dataFim
        );


    if (hoje <= inicio) {
        return 0;
    }


    if (hoje >= fim) {
        return 100;
    }


    const total =
        fim.getTime() -
        inicio.getTime();


    const decorrido =
        hoje.getTime() -
        inicio.getTime();


    return Math.round(
        (
            decorrido /
            total
        ) * 100
    );
}


/* =====================================================
   EVENTOS
===================================================== */

secaoCicloAtual
    .addEventListener(
        "click",
        event => {

            const botao =
                event.target.closest(
                    "[data-action='editar-ciclo-atual']"
                );


            if (
                botao &&
                cicloAtual
            ) {

                abrirModalEditarCiclo(
                    cicloAtual
                );
            }
        }
    );


listaCiclos
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
                    "[data-ciclo-id]"
                );


            if (!card) {
                return;
            }


            const idCiclo =
                Number(
                    card.dataset
                        .cicloId
                );


            const ciclo =
                ciclos.find(
                    item =>
                        Number(
                            item.idCiclo
                        ) === idCiclo
                );


            if (!ciclo) {
                return;
            }


            if (
                botao.dataset.action ===
                "editar-ciclo"
            ) {

                abrirModalEditarCiclo(
                    ciclo
                );

                return;
            }


            if (
                botao.dataset.action ===
                "cancelar-ciclo"
            ) {

                await cancelarCiclo(
                    ciclo
                );
            }
        }
    );


listaObjetivosCiclo
    .addEventListener(
        "click",
        async event => {

            const botao =
                event.target.closest(
                    "[data-action='remover-objetivo']"
                );


            if (!botao) {
                return;
            }


            const item =
                botao.closest(
                    "[data-objetivo-id]"
                );


            if (!item) {
                return;
            }


            await removerObjetivo(
                Number(
                    item.dataset
                        .objetivoId
                )
            );
        }
    );


document
    .getElementById(
        "btn-associar-objetivo"
    )
    .addEventListener(
        "click",
        associarObjetivo
    );


document
    .getElementById(
        "btn-novo-ciclo"
    )
    .addEventListener(
        "click",
        abrirModalNovoCiclo
    );


document
    .getElementById(
        "btn-fechar-ciclo"
    )
    .addEventListener(
        "click",
        fecharModalCiclo
    );


document
    .getElementById(
        "btn-cancelar-ciclo"
    )
    .addEventListener(
        "click",
        fecharModalCiclo
    );


formCiclo
    .addEventListener(
        "submit",
        salvarCiclo
    );


/* =====================================================
   MODAL
===================================================== */

function fecharModalCiclo() {

    modalCiclo
        .classList
        .add("hidden");


    formCiclo.reset();

    limparMensagemCiclo();
}


/* =====================================================
   UTILITÁRIOS
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


function limparMensagemCiclo() {

    definirTexto(
        "mensagem-form-ciclo",
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


function iconeArea(area) {

    const valor =
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
        icones[valor] ||
        "🎯"
    );
}


/* =====================================================
   START
===================================================== */

document.addEventListener(
    "DOMContentLoaded",
    carregarPagina
);