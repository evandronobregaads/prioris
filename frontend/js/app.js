import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";


/* =====================================================
   PRIORIS
   Dashboard principal
===================================================== */


/* =====================================================
   1. CONFIGURAÇÕES E ESTADO GLOBAL
===================================================== */

/*
 * Enquanto o login ainda não estiver implementado,
 * usamos um usuário padrão.
 *
 * Se existir idUsuario no localStorage, ele terá prioridade.
 */
const ID_USUARIO_ATUAL =
    exigirAutenticacao();

let ID_TAREFA_PRIORIDADE_ATUAL = null;

/*
 * Guardamos o planejamento atual para que outras
 * funcionalidades do Dashboard possam utilizá-lo.
 */
let planejamentoAtual = null;

function iniciarFocoDaPrioridade() {

    /*
     * Se existir uma Prioridade #1,
     * guardamos sua tarefa para a sessão.
     */
    if (ID_TAREFA_PRIORIDADE_ATUAL) {

        localStorage.setItem(
            "idTarefaFoco",
            String(
                ID_TAREFA_PRIORIDADE_ATUAL
            )
        );

    } else {

        /*
         * Sem prioridade:
         * entramos no modo foco normalmente.
         */
        localStorage.removeItem(
            "idTarefaFoco"
        );
    }

    window.location.href =
        "./pages/foco.html";
}

/* =====================================================
   2. FUNÇÕES UTILITÁRIAS
===================================================== */

function normalizarCodigo(valor) {

    return String(valor ?? "")
        .trim()
        .toUpperCase()
        .normalize("NFD")
        .replace(
            /[\u0300-\u036f]/g,
            ""
        )
        .replace(
            /[\s-]+/g,
            "_"
        );
}

/*
 * Retorna um elemento do HTML pelo ID.
 */
function obterElemento(id) {

    return document.getElementById(id);
}


/*
 * Altera o texto de um elemento somente se ele existir.
 *
 * Isso evita erros do tipo:
 *
 * Cannot set properties of null
 */
function definirTexto(id, valor) {

    const elemento =
        obterElemento(id);

    if (!elemento) {
        return;
    }

    elemento.textContent =
        valor ?? "";
}


/*
 * Carrega a data atual exibida no cabeçalho.
 */
function carregarDataAtual() {

    const elementoData =
        obterElemento("current-date");

    if (!elementoData) {
        return;
    }

    const hoje =
        new Date();

    const dataFormatada =
        hoje
            .toLocaleDateString(
                "pt-BR",
                {
                    day: "2-digit",
                    month: "short"
                }
            )
            .replace(".", "")
            .toUpperCase();

    elementoData.textContent =
        dataFormatada;
}


/*
 * Retorna a data atual no formato:
 *
 * YYYY-MM-DD
 *
 * sem problemas relacionados a UTC.
 */
function obterDataLocalISO() {

    const hoje =
        new Date();

    const ano =
        hoje.getFullYear();

    const mes =
        String(
            hoje.getMonth() + 1
        ).padStart(2, "0");

    const dia =
        String(
            hoje.getDate()
        ).padStart(2, "0");

    return `${ano}-${mes}-${dia}`;
}


/*
 * Converte:
 *
 * EM_ANDAMENTO
 *
 * para:
 *
 * Em Andamento
 */
function formatarStatus(status) {

    if (!status) {
        return "";
    }

    return String(status)
        .replaceAll("_", " ")
        .toLowerCase()
        .replace(
            /\b\w/g,
            letra =>
                letra.toUpperCase()
        );
}


/*
 * Converte:
 *
 * 2026-08-31
 *
 * para:
 *
 * 31 AGO 2026
 */
function formatarData(data) {

    if (!data) {
        return "--";
    }

    const partes =
        String(data)
            .substring(0, 10)
            .split("-");

    if (partes.length !== 3) {
        return data;
    }

    const [
        ano,
        mes,
        dia
    ] = partes;

    const dataLocal =
        new Date(
            Number(ano),
            Number(mes) - 1,
            Number(dia)
        );

    return dataLocal
        .toLocaleDateString(
            "pt-BR",
            {
                day: "2-digit",
                month: "short",
                year: "numeric"
            }
        )
        .replace(".", "")
        .toUpperCase();
}


/*
 * Protege conteúdo vindo do banco antes de inseri-lo
 * usando innerHTML.
 */
function escaparHtml(valor) {

    if (
        valor === null ||
        valor === undefined
    ) {
        return "";
    }

    return String(valor)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


/*
 * Retorna um ícone de acordo com a área do objetivo.
 */
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

    return icones[areaNormalizada] || "🎯";
}


/*
 * Retorna a primeira palavra de um nome.
 */
function obterPrimeiroNome(nome) {

    if (!nome) {
        return "Usuário";
    }

    return nome
        .trim()
        .split(/\s+/)[0];
}


/* =====================================================
   3. STATUS DA API
===================================================== */

async function testarApi() {

    const statusApi =
        obterElemento("api-status");

    if (!statusApi) {
        return;
    }

    try {

        const usuarios =
            await apiFetch("/usuarios");

        statusApi.classList.remove(
            "error"
        );

        statusApi.classList.add(
            "connected"
        );

        statusApi.innerHTML = `
            <span class="api-dot"></span>
            API conectada
        `;

        console.log(
            "API conectada:",
            usuarios
        );

    } catch (erro) {

        statusApi.classList.remove(
            "connected"
        );

        statusApi.classList.add(
            "error"
        );

        statusApi.innerHTML = `
            <span class="api-dot"></span>
            API indisponível
        `;

        console.error(
            "Erro ao acessar a API:",
            erro
        );
    }
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

        definirTexto(
            "nome-usuario",
            usuario.nome
        );

        const primeiroNome =
            obterPrimeiroNome(
                usuario.nome
            );

        definirTexto(
            "saudacao-usuario",
            `Bom dia, ${primeiroNome}! 👋`
        );

        /*
         * Atualiza também a letra do avatar,
         * caso exista no HTML.
         */
        const avatar =
            document.querySelector(
                ".user-avatar"
            );

        if (
            avatar &&
            primeiroNome
        ) {

            avatar.textContent =
                primeiroNome
                    .charAt(0)
                    .toUpperCase();
        }

    } catch (erro) {

        console.error(
            "Erro ao carregar usuário:",
            erro
        );

        definirTexto(
            "nome-usuario",
            "Usuário"
        );

        definirTexto(
            "saudacao-usuario",
            "Bom dia! 👋"
        );
    }
}

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
   5. PRIORIDADE DIÁRIA
===================================================== */

async function carregarPrioridade() {

    const titulo =
        obterElemento(
            "prioridade-titulo"
        );

    const abcde =
        obterElemento(
            "prioridade-abcde"
        );

    const status =
        obterElemento(
            "prioridade-status"
        );

    const tempo =
        obterElemento(
            "prioridade-tempo"
        );

    const botaoFoco =
        obterElemento(
            "btn-iniciar-foco-prioridade"
        );

    const descricaoFoco =
        obterElemento(
            "descricao-foco-prioridade"
        );


    /*
     * Enquanto carrega, mantém o botão escondido.
     */

    if (botaoFoco) {

        botaoFoco.style.display =
            "inline-flex";

        botaoFoco.disabled =
            false;
    }

    if (descricaoFoco) {

        descricaoFoco.style.display =
            "inline";

        descricaoFoco.textContent =
            "Pomodoro 25 / 5";
    }


    try {

        const prioridade =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria/hoje`
            );


        if (titulo) {
            titulo.textContent =
                prioridade.tituloTarefa;
        }


        if (abcde) {
            abcde.textContent =
                prioridade.classificacaoAbcde || "-";
        }


        if (status) {
            status.textContent =
                `Status: ${formatarStatus(
                    prioridade.statusTarefa
                )}`;
        }


        if (tempo) {
            tempo.textContent =
                prioridade.tempoEstimado ?? "--";
        }


        /*
         * Guarda a tarefa da prioridade atual.
         */

        ID_TAREFA_PRIORIDADE_ATUAL =
            prioridade.idTarefa ?? null;


        /*
         * Só mostra o botão se realmente
         * existir uma tarefa vinculada.
         */

        if (
            botaoFoco &&
            ID_TAREFA_PRIORIDADE_ATUAL
        ) {

            botaoFoco.style.display =
                "inline-flex";

            botaoFoco.disabled =
                false;
        }


        if (
            descricaoFoco &&
            ID_TAREFA_PRIORIDADE_ATUAL
        ) {

            descricaoFoco.style.display =
                "inline";
        }


        console.log(
            "✅ Prioridade carregada:",
            prioridade
        );


    } catch (erro) {

        ID_TAREFA_PRIORIDADE_ATUAL =
            null;


        if (titulo) {
            titulo.textContent =
                "Nenhuma Prioridade #1 definida para hoje";
        }


        if (abcde) {
            abcde.textContent = "-";
        }


        if (status) {
            status.textContent =
                "Escolha a tarefa mais importante do seu dia.";
        }


        if (tempo) {
            tempo.textContent =
                "--";
        }


        /*
         * Sem prioridade = sem botão de foco.
         */

        if (botaoFoco) {
            botaoFoco.style.display =
                "none";

            botaoFoco.disabled =
                true;
        }


        if (descricaoFoco) {
            descricaoFoco.style.display =
                "none";
        }


        console.log(
            "ℹ️ Nenhuma prioridade definida para hoje."
        );
    }
}


/* =====================================================
   6. CICLO DE 12 SEMANAS + PLANEJAMENTO
===================================================== */

async function carregarCicloEPlanejamento() {

    try {

        /* ==============================================
           CICLOS
        ============================================== */

        const respostaCiclos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/ciclos`
            );


        const ciclos =
            Array.isArray(respostaCiclos)
                ? respostaCiclos
                : [];


        console.log(
            "🔎 Ciclos recebidos pelo Dashboard:",
            ciclos
        );


        const hoje =
            obterDataLocalISO();


        /*
         * 1ª tentativa:
         * ciclo explicitamente EM_ANDAMENTO.
         */
        let cicloAtual =
            ciclos.find(
                ciclo => {

                    const status =
                        normalizarCodigo(
                            ciclo.status ??
                            ciclo.statusCiclo
                        );


                    return (
                        status ===
                        "EM_ANDAMENTO"
                    );
                }
            );


        /*
         * 2ª tentativa:
         * se o DTO/status vier diferente,
         * procuramos um ciclo cuja data
         * contenha o dia de hoje.
         */
        if (!cicloAtual) {

            cicloAtual =
                ciclos.find(
                    ciclo => {

                        const inicio =
                            String(
                                ciclo.dataInicio ?? ""
                            ).substring(0, 10);


                        const fim =
                            String(
                                ciclo.dataFim ?? ""
                            ).substring(0, 10);


                        const status =
                            normalizarCodigo(
                                ciclo.status ??
                                ciclo.statusCiclo
                            );


                        return (
                            inicio &&
                            fim &&
                            inicio <= hoje &&
                            fim >= hoje &&
                            status !== "CANCELADO"
                        );
                    }
                );
        }


        if (!cicloAtual) {

            console.warn(
                "⚠️ Nenhum ciclo atual encontrado."
            );


            mostrarSemCiclo();

            mostrarSemPlanejamento();

            mostrarSemTarefasEstrategicas();

            return;
        }


        console.log(
            "✅ Ciclo atual encontrado:",
            cicloAtual
        );


        preencherCiclo(
            cicloAtual
        );


        const idCicloAtual =
            cicloAtual.idCiclo ??
            cicloAtual.id;


        /* ==============================================
           PLANEJAMENTOS
        ============================================== */

        const respostaPlanejamentos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais`
            );


        const planejamentos =
            Array.isArray(
                respostaPlanejamentos
            )
                ? respostaPlanejamentos
                : [];


        console.log(
            "🔎 Planejamentos recebidos:",
            planejamentos
        );


        planejamentoAtual =
            encontrarPlanejamentoAtual(
                planejamentos,
                idCicloAtual
            );


        if (!planejamentoAtual) {

            console.warn(
                "⚠️ Nenhum planejamento atual encontrado."
            );


            mostrarSemPlanejamento();

            mostrarSemTarefasEstrategicas();

            return;
        }


        console.log(
            "✅ Planejamento localizado:",
            planejamentoAtual
        );


        const idPlanejamento =
            planejamentoAtual
                .idPlanejamentoSemanal ??
            planejamentoAtual
                .idPlanejamento ??
            planejamentoAtual.id;


        /*
         * Busca os dados completos para
         * garantir Score e totais atualizados.
         */
        try {

            const planejamentoCompleto =
                await apiFetch(
                    `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${idPlanejamento}`
                );


            planejamentoAtual =
                planejamentoCompleto;


            console.log(
                "✅ Planejamento completo:",
                planejamentoAtual
            );


        } catch (erro) {

            console.warn(
                "⚠️ Usando planejamento resumido:",
                erro
            );
        }


        preencherPlanejamento(
            planejamentoAtual
        );


        await carregarTarefasEstrategicas(
            idPlanejamento
        );


    } catch (erro) {

        console.error(
            "❌ Erro ao carregar ciclo e planejamento:",
            erro
        );


        mostrarSemCiclo();

        mostrarSemPlanejamento();

        mostrarSemTarefasEstrategicas();
    }
}


/*
 * Localiza o planejamento correspondente à semana atual.
 *
 * Se não existir exatamente um planejamento contendo
 * a data de hoje, utilizamos o mais recente do ciclo.
 */
function encontrarPlanejamentoAtual(
    planejamentos,
    idCiclo
) {

    if (
        !Array.isArray(planejamentos) ||
        planejamentos.length === 0
    ) {
        return null;
    }


    const hoje =
        obterDataLocalISO();


    const doCiclo =
        planejamentos.filter(
            planejamento => {

                /*
                 * Alguns DTOs podem retornar idCiclo,
                 * enquanto outros podem retornar
                 * um objeto ciclo.
                 */
                const planejamentoIdCiclo =
                    planejamento.idCiclo ??
                    planejamento.ciclo?.idCiclo;

                /*
                 * Caso a listagem não exponha o ciclo,
                 * não eliminamos o planejamento.
                 */
                return (
                    planejamentoIdCiclo ===
                    undefined ||
                    planejamentoIdCiclo ===
                    null ||
                    Number(
                        planejamentoIdCiclo
                    ) === Number(idCiclo)
                );
            }
        );


    const planejamentoDaSemana =
        doCiclo.find(
            planejamento =>
                planejamento.dataInicioSemana &&
                planejamento.dataFimSemana &&
                planejamento.dataInicioSemana <= hoje &&
                planejamento.dataFimSemana >= hoje
        );


    if (planejamentoDaSemana) {
        return planejamentoDaSemana;
    }


    /*
     * Ordenamos pelas datas antes de pegar o último.
     */
    const ordenados =
        [...doCiclo]
            .sort(
                (a, b) =>
                    String(
                        a.dataInicioSemana || ""
                    )
                        .localeCompare(
                            String(
                                b.dataInicioSemana || ""
                            )
                        )
            );


    return ordenados.at(-1) || null;
}


/*
 * Preenche os dados gerais do ciclo.
 */
function preencherCiclo(ciclo) {

    definirTexto(
        "ciclo-titulo",
        ciclo.titulo ||
        "Ciclo de 12 Semanas"
    );

    definirTexto(
        "ciclo-status",
        formatarStatus(
            ciclo.status ??
            ciclo.statusCiclo
        )
    );

    definirTexto(
        "ciclo-data-inicio",
        formatarData(
            ciclo.dataInicio
        )
    );

    definirTexto(
        "ciclo-data-fim",
        formatarData(
            ciclo.dataFim
        )
    );
}


/*
 * Preenche:
 *
 * semana
 * percentual do ciclo
 * score
 * tarefas planejadas
 * tarefas concluídas
 */
function preencherPlanejamento(
    planejamento
) {

    const semana =
        Number(
            planejamento.semanaCiclo
        ) || 1;


    const percentualCiclo =
        Math.min(
            Math.max(
                (semana / 12) * 100,
                0
            ),
            100
        );


    definirTexto(
        "ciclo-semana",
        `${semana} de 12`
    );

    definirTexto(
        "ciclo-percentual",
        `${Math.round(
            percentualCiclo
        )}%`
    );


    const cicloBarra =
        obterElemento(
            "ciclo-barra"
        );

    if (cicloBarra) {

        cicloBarra.style.width =
            `${percentualCiclo}%`;
    }


    const score =
        Number(
            planejamento.scoreExecucao ??
            0
        );


    definirTexto(
        "score-valor",
        `${score.toFixed(0)}%`
    );


    const scoreRing =
        obterElemento(
            "score-ring"
        );

    if (scoreRing) {

        const scoreSeguro =
            Math.min(
                Math.max(
                    score,
                    0
                ),
                100
            );

        scoreRing.style.setProperty(
            "--score",
            `${scoreSeguro}%`
        );
    }


    definirTexto(
        "total-planejadas",
        planejamento
            .totalTarefasPlanejadas ??
        0
    );

    definirTexto(
        "total-concluidas",
        planejamento
            .totalTarefasConcluidas ??
        0
    );


    atualizarMensagemScore(
        score
    );
}


/*
 * Mensagem do card de Score.
 */
function atualizarMensagemScore(
    score
) {

    let mensagem;

    if (score >= 85) {

        mensagem =
            "Excelente ritmo!";

    } else if (score >= 60) {

        mensagem =
            "Bom progresso!";

    } else if (score > 0) {

        mensagem =
            "Ainda dá para avançar!";

    } else {

        mensagem =
            "Comece pela prioridade #1.";
    }


    definirTexto(
        "score-mensagem",
        mensagem
    );
}


/*
 * Estado vazio do card de Ciclo.
 */
function mostrarSemCiclo() {

    definirTexto(
        "ciclo-titulo",
        "Nenhum ciclo em andamento"
    );

    definirTexto(
        "ciclo-status",
        "SEM CICLO"
    );

    definirTexto(
        "ciclo-semana",
        "- de 12"
    );

    definirTexto(
        "ciclo-percentual",
        "0%"
    );

    definirTexto(
        "ciclo-data-inicio",
        "--"
    );

    definirTexto(
        "ciclo-data-fim",
        "--"
    );


    const barra =
        obterElemento(
            "ciclo-barra"
        );

    if (barra) {

        barra.style.width =
            "0%";
    }
}


/*
 * Estado vazio do planejamento.
 */
function mostrarSemPlanejamento() {

    planejamentoAtual =
        null;


    definirTexto(
        "score-valor",
        "0%"
    );

    definirTexto(
        "score-mensagem",
        "Planeje sua semana."
    );

    definirTexto(
        "total-planejadas",
        "0"
    );

    definirTexto(
        "total-concluidas",
        "0"
    );


    const ring =
        obterElemento(
            "score-ring"
        );

    if (ring) {

        ring.style.setProperty(
            "--score",
            "0%"
        );
    }
}


/* =====================================================
   7. TAREFAS ESTRATÉGICAS
===================================================== */

async function carregarTarefasEstrategicas(
    idPlanejamento
) {

    const lista =
        obterElemento(
            "lista-tarefas-estrategicas"
        );


    if (!lista) {
        return;
    }


    try {

        const resposta =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/planejamentos-semanais/${idPlanejamento}/tarefas`
            );


        const tarefas =
            Array.isArray(resposta)
                ? resposta
                : [];


        lista.innerHTML = "";


        if (tarefas.length === 0) {

            mostrarSemTarefasEstrategicas();

            return;
        }


        tarefas
            .slice(0, 4)
            .forEach(item => {

                /*
                 * Compatibilidade com diferentes formatos
                 * possíveis do DTO.
                 *
                 * Pode ser:
                 *
                 * item.tarefa.titulo
                 *
                 * ou:
                 *
                 * item.titulo
                 *
                 * ou:
                 *
                 * item.tituloTarefa
                 */
                const tarefa =
                    item.tarefa || item;


                const titulo =
                    tarefa.titulo ??
                    item.tituloTarefa ??
                    "Tarefa sem título";


                const status =
                    tarefa.status ??
                    item.statusTarefa ??
                    "PENDENTE";


                const classificacao =
                    tarefa.classificacaoAbcde ??
                    item.classificacaoAbcde ??
                    "-";


                const concluida =
                    status ===
                    "CONCLUIDA";


                const elemento =
                    document.createElement(
                        "div"
                    );


                elemento.className =
                    "task-item";


                const classeAbcde =
                    ["A", "B", "C", "D", "E"]
                        .includes(
                            String(
                                classificacao
                            ).toUpperCase()
                        )
                        ? `badge-${String(
                            classificacao
                        ).toLowerCase()}`
                        : "";


                elemento.innerHTML = `
                    <button
                        class="task-check ${concluida ? "completed" : ""}"
                        type="button"
                        aria-label="Status da tarefa"
                        disabled
                    >
                        ${concluida ? "✓" : ""}
                    </button>

                    <div class="task-content">

                        <strong
                            class="${concluida ? "task-completed" : ""}"
                        >
                            ${escaparHtml(titulo)}
                        </strong>

                        <span>
                            ${escaparHtml(
                    formatarStatus(status)
                )}
                        </span>

                    </div>

                    <span
                        class="abcde-badge ${classeAbcde}"
                    >
                        ${escaparHtml(
                    classificacao
                )}
                    </span>
                `;


                lista.appendChild(
                    elemento
                );
            });

    } catch (erro) {

        console.error(
            "Erro ao carregar tarefas estratégicas:",
            erro
        );

        lista.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                as tarefas da semana.
            </div>
        `;
    }
}


/*
 * Estado vazio da lista de tarefas.
 */
function mostrarSemTarefasEstrategicas() {

    const lista =
        obterElemento(
            "lista-tarefas-estrategicas"
        );

    if (!lista) {
        return;
    }


    lista.innerHTML = `
        <div class="empty-state">
            Nenhuma tarefa estratégica
            adicionada nesta semana.
        </div>
    `;
}


/* =====================================================
   8. MINUTOS DE FOCO
===================================================== */

async function carregarMinutosFoco() {

    const elemento =
        obterElemento(
            "minutos-foco"
        );


    if (!elemento) {
        return;
    }


    try {

        const sessoes =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco`
            );


        if (!Array.isArray(sessoes)) {

            elemento.textContent =
                "0";

            return;
        }


        /*
         * Somamos somente o tempo efetivamente realizado.
         *
         * Sessões sem tempo realizado contam como zero.
         */
        const minutos =
            sessoes.reduce(
                (
                    total,
                    sessao
                ) => {

                    const realizado =
                        Number(
                            sessao
                                .tempoFocoRealizado ??
                            0
                        );


                    return (
                        total +
                        (
                            Number.isFinite(
                                realizado
                            )
                                ? realizado
                                : 0
                        )
                    );
                },
                0
            );


        elemento.textContent =
            minutos;


    } catch (erro) {

        console.error(
            "Erro ao carregar minutos de foco:",
            erro
        );

        elemento.textContent =
            "0";
    }
}


/* =====================================================
   9. OBJETIVOS
===================================================== */

async function carregarObjetivos() {

    const lista =
        obterElemento(
            "lista-objetivos"
        );


    if (!lista) {
        return;
    }


    try {

        const objetivos =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos`
            );


        if (!Array.isArray(objetivos)) {

            mostrarSemObjetivos();

            return;
        }


        const ativos =
            objetivos
                .filter(
                    objetivo =>
                        objetivo.status ===
                        "ATIVO"
                )
                .slice(0, 3);


        lista.innerHTML = "";


        /*
         * Atualiza também o indicador numérico.
         *
         * Aqui usamos todos os objetivos ativos,
         * e não apenas os três mostrados no Dashboard.
         */
        const totalAtivos =
            objetivos.filter(
                objetivo =>
                    objetivo.status ===
                    "ATIVO"
            ).length;


        definirTexto(
            "total-objetivos-ativos",
            totalAtivos
        );


        if (ativos.length === 0) {

            mostrarSemObjetivos();

            return;
        }


        /*
         * Calculamos o progresso de cada objetivo
         * consultando suas metas.
         */
        for (
            const objetivo
            of ativos
            ) {

            const progresso =
                await calcularProgressoObjetivo(
                    objetivo.idObjetivo
                );


            const elemento =
                criarCardObjetivo(
                    objetivo,
                    progresso
                );


            lista.appendChild(
                elemento
            );
        }


    } catch (erro) {

        console.error(
            "Erro ao carregar objetivos:",
            erro
        );


        definirTexto(
            "total-objetivos-ativos",
            "0"
        );


        lista.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                os objetivos.
            </div>
        `;
    }
}


/*
 * Calcula o progresso de um objetivo através
 * de suas metas:
 *
 * metas concluídas / metas válidas × 100
 */
async function calcularProgressoObjetivo(
    idObjetivo
) {

    try {

        const metas =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/objetivos/${idObjetivo}/metas`
            );


        if (!Array.isArray(metas)) {
            return 0;
        }


        /*
         * Metas canceladas não entram no cálculo.
         */
        const metasValidas =
            metas.filter(
                meta =>
                    meta.status !==
                    "CANCELADA"
            );


        if (
            metasValidas.length === 0
        ) {
            return 0;
        }


        const concluidas =
            metasValidas.filter(
                meta =>
                    meta.status ===
                    "CONCLUIDA"
            ).length;


        const progresso =
            (
                concluidas /
                metasValidas.length
            ) * 100;


        return Math.round(
            progresso
        );


    } catch (erro) {

        console.error(
            `Erro ao calcular progresso do objetivo ${idObjetivo}:`,
            erro
        );

        return 0;
    }
}


/*
 * Cria visualmente um card de objetivo.
 */
function criarCardObjetivo(
    objetivo,
    progresso
) {

    const elemento =
        document.createElement(
            "div"
        );


    elemento.className =
        "goal-item";


    const area =
        objetivo.area ||
        "GERAL";


    const progressoSeguro =
        Math.min(
            Math.max(
                Number(progresso) || 0,
                0
            ),
            100
        );


    elemento.innerHTML = `

        <div class="goal-header">

            <div class="goal-icon">
                ${iconeArea(area)}
            </div>

            <div>

                <span>
                    ${escaparHtml(
        formatarStatus(area)
    )}
                </span>

                <strong>
                    ${escaparHtml(
        objetivo.titulo ||
        "Objetivo sem título"
    )}
                </strong>

            </div>

        </div>


        <div class="goal-progress">

            <div class="goal-progress-header">

                <span>
                    Progresso
                </span>

                <strong>
                    ${progressoSeguro}%
                </strong>

            </div>


            <div class="progress-track small">

                <div
                    class="progress-bar"
                    style="width: ${progressoSeguro}%"
                ></div>

            </div>

        </div>
    `;


    return elemento;
}


/*
 * Estado sem objetivos.
 */
function mostrarSemObjetivos() {

    const lista =
        obterElemento(
            "lista-objetivos"
        );


    if (!lista) {
        return;
    }


    lista.innerHTML = `
        <div class="empty-state">
            Nenhum objetivo ativo.
        </div>
    `;
}

/* =====================================================
   10. EVENTOS DO DASHBOARD
===================================================== */

function configurarEventosDashboard() {

    const botaoFocoPrioridade =
        obterElemento(
            "btn-iniciar-foco-prioridade"
        );

    if (botaoFocoPrioridade) {

        botaoFocoPrioridade.addEventListener(
            "click",
            iniciarFocoDaPrioridade
        );
    }
}

const btnIniciarSessaoDashboard =
    document.getElementById(
        "btn-iniciar-sessao-dashboard"
    );


btnIniciarSessaoDashboard
    ?.addEventListener(
        "click",
        () => {

            window.location.href =
                "./pages/foco.html";
        }
    );

/* =====================================================
   11. INICIALIZAÇÃO DO DASHBOARD
===================================================== */

async function inicializarDashboard() {

    /*
     * Configura eventos depois que
     * o HTML já estiver carregado.
     */

    configurarEventosDashboard();


    /*
     * Funções locais que não dependem da API.
     */

    carregarDataAtual();


    /*
     * Teste da API.
     */

    testarApi();


    /*
     * Consultas independentes.
     */

    await Promise.allSettled([

        carregarUsuario(),

        carregarPrioridade(),

        carregarCicloEPlanejamento(),

        carregarMinutosFoco(),

        carregarObjetivos()

    ]);


    console.log(
        "Dashboard Prioris carregado."
    );
}


/*
 * Só inicia depois que o HTML estiver carregado.
 */

document.addEventListener(
    "DOMContentLoaded",
    inicializarDashboard
);

