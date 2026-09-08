import { apiFetch } from "./api.js";
import {
    exigirAutenticacao,
    logout
} from "./auth.js";

/* =====================================================
   PRIORIS — SESSÃO DE FOCO
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

const TIMER_STORAGE_KEY =
    `prioris_foco_timer_usuario_${ID_USUARIO_ATUAL}`;


/* =====================================================
   2. ESTADO
===================================================== */

let tarefas = [];
let sessoes = [];

let sessaoAtual = null;

let timerInterval = null;

let segundosTotais = 25 * 60;
let segundosRestantes = 25 * 60;

let timestampReferencia = null;

let timerRodando = false;
let processandoEncerramento = false;

let faseAtual = "FOCO";

let audioContextoAlerta = null;

/* =====================================================
   YOUTUBE
===================================================== */

const YOUTUBE_CHANNEL_ID =
    "UCg5L6P3E5MN8uZidiUg2dqA";

let youtubePlayer = null;

let youtubeReady = false;

let audioEstavaTocandoAntesDaPausa =
    false;


/*
 * Neste primeiro momento usamos diferentes
 * posições do feed do canal.
 *
 * Depois poderemos substituir cada preset
 * por uma playlist específica.
 */
const YOUTUBE_PRESETS = {

    focus: {
        videoId: "5LXhPbmoHmU",
        nome: "Foco profundo"
    },

    nature: {
        videoId: "JTtlEwFNA-E",
        nome: "Natureza"
    },

    lofi: {
        videoId: "n61ULEU7CO0",
        nome: "Lo-fi"
    }
};

/* =====================================================
   ALERTA SONORO DO POMODORO
===================================================== */

function prepararAudioAlerta() {

    const AudioContextClass =
        window.AudioContext ||
        window.webkitAudioContext;

    if (!AudioContextClass) {
        return;
    }

    if (!audioContextoAlerta) {

        audioContextoAlerta =
            new AudioContextClass();
    }

    if (
        audioContextoAlerta.state ===
        "suspended"
    ) {

        audioContextoAlerta
            .resume()
            .catch(() => {});
    }
}


function tocarAlertaFimPeriodo() {

    if (!audioContextoAlerta) {
        return;
    }

    const contexto =
        audioContextoAlerta;

    const inicio =
        contexto.currentTime;

    /*
     * Três sinais curtos.
     */
    [0, 0.22, 0.44]
        .forEach(
            (atraso, indice) => {

                const oscilador =
                    contexto.createOscillator();

                const ganho =
                    contexto.createGain();

                oscilador.connect(
                    ganho
                );

                ganho.connect(
                    contexto.destination
                );

                const momento =
                    inicio + atraso;

                oscilador.type =
                    "sine";

                oscilador.frequency
                    .setValueAtTime(
                        indice === 2
                            ? 1046
                            : 880,
                        momento
                    );

                ganho.gain
                    .setValueAtTime(
                        0.0001,
                        momento
                    );

                ganho.gain
                    .exponentialRampToValueAtTime(
                        0.14,
                        momento + 0.02
                    );

                ganho.gain
                    .exponentialRampToValueAtTime(
                        0.0001,
                        momento + 0.18
                    );

                oscilador.start(
                    momento
                );

                oscilador.stop(
                    momento + 0.19
                );
            }
        );
}

/* =====================================================
   3. ELEMENTOS
===================================================== */

const timerElemento =
    document.getElementById("foco-timer");

const statusTexto =
    document.getElementById("foco-status-texto");

const modoElemento =
    document.getElementById("foco-modo");

const tarefaTitulo =
    document.getElementById("foco-tarefa-titulo");

const selectTarefa =
    document.getElementById("foco-tarefa-select");

const selectTempo =
    document.getElementById("foco-tempo");

const selectDescanso =
    document.getElementById("foco-descanso");

const mensagemFoco =
    document.getElementById("mensagem-foco");

const listaSessoes =
    document.getElementById("lista-sessoes-foco");


const btnIniciar =
    document.getElementById("btn-iniciar-foco");

const btnPausar =
    document.getElementById("btn-pausar-foco");

const btnRetomar =
    document.getElementById("btn-retomar-foco");

const btnFinalizar =
    document.getElementById("btn-finalizar-foco");

const btnInterromper =
    document.getElementById("btn-interromper-foco");


/* =====================================================
   4. INICIALIZAÇÃO
===================================================== */

async function carregarPagina() {

    console.log(
        "Foco.js iniciado. Usuário:",
        ID_USUARIO_ATUAL
    );


    await Promise.allSettled([
        carregarUsuario(),
        carregarTarefas()
    ]);


    await carregarSessoes();


    /*
     * Se a Prioridade #1 existir,
     * tentamos selecioná-la automaticamente.
     */
    await selecionarPrioridadeDoDia();

    configurarTimerInicial();

    carregarYouTubeIframeApi();

    console.log(
        "Página Sessão de Foco carregada."
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
   6. CARREGAR TAREFAS
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
         * Não oferecemos tarefas eliminadas,
         * canceladas ou já concluídas.
         */
        tarefas =
            tarefas.filter(
                tarefa => {

                    const status =
                        String(
                            tarefa.status || ""
                        ).toUpperCase();


                    return (
                        status !== "ELIMINADA" &&
                        status !== "CANCELADA" &&
                        status !== "CONCLUIDA"
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
   7. SELECT DE TAREFAS
===================================================== */

function preencherSelectTarefas() {

    selectTarefa.innerHTML = `

        <option value="">
            Sessão sem tarefa específica
        </option>

        ${
        tarefas
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
}


/* =====================================================
   8. PRIORIDADE #1 → FOCO
===================================================== */

async function selecionarPrioridadeDoDia() {

    /*
     * Não altera tarefa de uma sessão
     * que já esteja ativa.
     */
    if (sessaoAtual) {
        return;
    }


    try {

        const prioridade =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/prioridade-diaria/hoje`
            );


        const idTarefa =
            Number(
                prioridade?.idTarefa ??
                prioridade?.tarefa?.idTarefa
            );


        if (!idTarefa) {
            return;
        }


        const existe =
            tarefas.some(
                tarefa =>
                    Number(
                        tarefa.idTarefa
                    ) === idTarefa
            );


        if (existe) {

            selectTarefa.value =
                String(idTarefa);

            atualizarTituloTarefa();
        }


    } catch (erro) {

        /*
         * Sem prioridade hoje:
         * nada precisa acontecer.
         */
        console.log(
            "Nenhuma Prioridade #1 disponível para pré-seleção."
        );
    }
}


/* =====================================================
   9. CARREGAR SESSÕES
===================================================== */

async function carregarSessoes() {

    try {

        sessoes =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco`
            );


        if (!Array.isArray(sessoes)) {
            sessoes = [];
        }


        /*
         * Procura uma sessão ainda ativa.
         */
        sessaoAtual =
            sessoes.find(
                sessao => {

                    const status =
                        String(
                            sessao.status || ""
                        ).toUpperCase();


                    return (
                        status === "EM_ANDAMENTO" ||
                        status === "PAUSADA"
                    );
                }
            ) || null;


        renderizarResumo();

        renderizarHistorico();


    } catch (erro) {

        console.error(
            "Erro ao carregar sessões:",
            erro
        );


        sessoes = [];

        sessaoAtual = null;


        renderizarResumo();


        listaSessoes.innerHTML = `
            <div class="empty-state error-state">
                Não foi possível carregar
                o histórico de foco.
            </div>
        `;
    }
}


/* =====================================================
   10. CONFIGURAÇÃO INICIAL DO TIMER
===================================================== */

function configurarTimerInicial() {

    if (sessaoAtual) {

        restaurarSessaoAtual();

        return;
    }


    resetarTimerVisual();
}


/* =====================================================
   11. RESTAURAR SESSÃO ATIVA
===================================================== */

function restaurarSessaoAtual() {
    faseAtual =
        "FOCO";

    const tempoPlanejado =
        Number(
            sessaoAtual
                .tempoFocoPlanejado ?? 25
        );


    segundosTotais =
        tempoPlanejado * 60;


    const estadoSalvo =
        lerEstadoTimer();


    const idSessao =
        obterIdSessao(
            sessaoAtual
        );


    if (
        estadoSalvo &&
        Number(
            estadoSalvo.idSessao
        ) === Number(idSessao)
    ) {

        segundosTotais =
            Number(
                estadoSalvo.segundosTotais
            ) || segundosTotais;


        segundosRestantes =
            Number(
                estadoSalvo.segundosRestantes
            );


        timestampReferencia =
            estadoSalvo.timestampReferencia
                ? Number(
                    estadoSalvo.timestampReferencia
                )
                : null;


        timerRodando =
            Boolean(
                estadoSalvo.timerRodando
            );


        /*
         * Se estava rodando antes de atualizar
         * a página, descontamos o tempo real
         * transcorrido.
         */
        if (
            timerRodando &&
            timestampReferencia
        ) {

            segundosRestantes =
                calcularSegundosRestantes();
        }

    } else {

        /*
         * Se não houver estado local,
         * ao menos recuperamos a configuração.
         */
        segundosRestantes =
            segundosTotais;


        timerRodando =
            String(
                sessaoAtual.status
            ).toUpperCase() ===
            "EM_ANDAMENTO";


        timestampReferencia =
            timerRodando
                ? Date.now()
                : null;
    }


    preencherConfiguracaoDaSessao();

    atualizarTituloTarefaDaSessao();

    atualizarTimerVisual();

    atualizarControles();


    if (timerRodando) {
        iniciarIntervalo();
    }
}


/* =====================================================
   12. PREENCHER CONFIGURAÇÃO DA SESSÃO
===================================================== */

function preencherConfiguracaoDaSessao() {

    if (!sessaoAtual) {
        return;
    }


    const idTarefa =
        sessaoAtual.idTarefa ??
        sessaoAtual.tarefa?.idTarefa;


    selectTarefa.value =
        idTarefa
            ? String(idTarefa)
            : "";


    selectTempo.value =
        String(
            sessaoAtual
                .tempoFocoPlanejado ?? 25
        );


    selectDescanso.value =
        String(
            sessaoAtual
                .tempoDescansoPlanejado ?? 5
        );
}


/* =====================================================
   13. INICIAR SESSÃO
===================================================== */

async function iniciarSessao() {

    limparMensagem();
    /*
     * Precisamos preparar o áudio dentro
     * do clique do usuário para que o
     * navegador permita a reprodução.
     */
    prepararAudioAlerta();

    /*
     * Inicia também o ambiente sonoro.
     */
    iniciarAudioComSessao();

    faseAtual =
        "FOCO";

    const idTarefaTexto =
        selectTarefa.value;


    const tempoFoco =
        Number(
            selectTempo.value
        );


    const tempoDescanso =
        Number(
            selectDescanso.value
        );


    const body = {

        idTarefa:
            idTarefaTexto
                ? Number(idTarefaTexto)
                : null,

        tempoFocoPlanejado:
        tempoFoco,

        tempoDescansoPlanejado:
        tempoDescanso
    };


    try {

        bloquearBotao(
            btnIniciar,
            true
        );


        sessaoAtual =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco`,
                {
                    method: "POST",

                    body:
                        JSON.stringify(
                            body
                        )
                }
            );


        segundosTotais =
            tempoFoco * 60;


        segundosRestantes =
            segundosTotais;


        timestampReferencia =
            Date.now();


        timerRodando =
            true;


        atualizarTituloTarefa();

        atualizarTituloTarefaDaSessao();


        salvarEstadoTimer();

        atualizarControles();

        atualizarTimerVisual();

        iniciarIntervalo();


        await atualizarListaSessoes();


    } catch (erro) {
        pararAudioComSessao();
        mostrarMensagem(
            erro.message
        );

    } finally {

        bloquearBotao(
            btnIniciar,
            false
        );
    }
}


/* =====================================================
   14. TIMER REAL
===================================================== */

function iniciarIntervalo() {

    pararIntervalo();


    timerInterval =
        setInterval(
            atualizarContagem,
            250
        );
}


/* =====================================================
   15. ATUALIZAR CONTAGEM
===================================================== */

async function atualizarContagem() {

    if (
        !timerRodando ||
        !timestampReferencia
    ) {
        return;
    }

    segundosRestantes =
        calcularSegundosRestantes();

    atualizarTimerVisual();

    /*
     * Durante o foco salvamos o estado
     * porque existe uma sessão no backend.
     */
    if (sessaoAtual) {

        salvarEstadoTimer();
    }

    if (
        segundosRestantes <= 0 &&
        !processandoEncerramento
    ) {

        segundosRestantes =
            0;

        atualizarTimerVisual();

        if (
            faseAtual ===
            "DESCANSO"
        ) {

            finalizarDescanso();

            return;
        }

        await finalizarSessao(
            true
        );
    }
}


/* =====================================================
   16. CÁLCULO POR TIMESTAMP
===================================================== */

function calcularSegundosRestantes() {

    if (!timestampReferencia) {
        return segundosRestantes;
    }


    const agora =
        Date.now();


    const decorrido =
        Math.floor(
            (
                agora -
                timestampReferencia
            ) / 1000
        );


    /*
     * Ainda não passou 1 segundo completo.
     */
    if (decorrido <= 0) {

        return segundosRestantes;
    }


    /*
     * IMPORTANTE:
     *
     * Avançamos a referência somente pelos
     * segundos inteiros que já contabilizamos.
     *
     * Isso impede que o mesmo tempo seja
     * descontado várias vezes.
     */
    timestampReferencia +=
        decorrido * 1000;


    return Math.max(
        0,
        segundosRestantes -
        decorrido
    );
}


/* =====================================================
   17. PAUSAR
===================================================== */

async function pausarSessao() {

    if (!sessaoAtual) {
        return;
    }


    try {

        /*
         * Primeiro congelamos o relógio
         * no instante exato.
         */
        segundosRestantes =
            calcularSegundosRestantes();


        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco/${obterIdSessao(sessaoAtual)}/pausar`,
            {
                method: "PATCH"
            }
        );


        timerRodando =
            false;


        timestampReferencia =
            null;


        sessaoAtual.status =
            "PAUSADA";


        pararIntervalo();

        pausarAudioComSessao();

        salvarEstadoTimer();

        atualizarTimerVisual();

        atualizarControles();

        renderizarResumo();


    } catch (erro) {

        mostrarMensagem(
            erro.message
        );
    }
}


/* =====================================================
   18. RETOMAR
===================================================== */

async function retomarSessao() {

    if (!sessaoAtual) {
        return;
    }


    try {

        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco/${obterIdSessao(sessaoAtual)}/retomar`,
            {
                method: "PATCH"
            }
        );


        timestampReferencia =
            Date.now();


        timerRodando =
            true;


        sessaoAtual.status =
            "EM_ANDAMENTO";


        salvarEstadoTimer();

        atualizarControles();

        iniciarIntervalo();

        retomarAudioComSessao();

        renderizarResumo();


    } catch (erro) {

        mostrarMensagem(
            erro.message
        );
    }
}


/* =====================================================
   19. FINALIZAR
===================================================== */

async function finalizarSessao(
    automaticamente = false
) {

    if (
        !sessaoAtual ||
        processandoEncerramento
    ) {
        return;
    }


    if (
        !automaticamente &&
        !window.confirm(
            "Deseja finalizar esta sessão de foco?"
        )
    ) {
        return;
    }


    processandoEncerramento =
        true;


    /*
     * Guardamos o descanso ANTES de
     * limpar sessaoAtual.
     */
    const minutosDescanso =
        Number(
            sessaoAtual
                .tempoDescansoPlanejado ??
            selectDescanso.value ??
            5
        );


    try {

        if (timerRodando) {

            segundosRestantes =
                calcularSegundosRestantes();
        }


        const minutosRealizados =
            calcularMinutosRealizados();


        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco/${obterIdSessao(sessaoAtual)}/finalizar`,
            {
                method: "PATCH",

                body:
                    JSON.stringify({
                        tempoFocoRealizado:
                        minutosRealizados
                    })
            }
        );


        timerRodando =
            false;

        timestampReferencia =
            null;


        pararIntervalo();

        pararAudioComSessao();

        limparEstadoTimer();


        /*
         * O foco terminou por tempo.
         */
        if (automaticamente) {

            tocarAlertaFimPeriodo();
        }


        sessaoAtual =
            null;


        await atualizarListaSessoes();

        renderizarResumo();


        /*
         * Se terminou naturalmente em 00:00,
         * começa o descanso.
         */
        if (automaticamente) {

            iniciarDescansoAutomatico(
                minutosDescanso
            );

        } else {

            faseAtual =
                "FOCO";

            resetarTimerVisual();

            statusTexto.textContent =
                "Sessão finalizada com sucesso.";
        }


    } catch (erro) {

        mostrarMensagem(
            erro.message
        );

    } finally {

        processandoEncerramento =
            false;
    }
}

/* =====================================================
   DESCANSO AUTOMÁTICO
===================================================== */

function iniciarDescansoAutomatico(
    minutosDescanso
) {

    faseAtual =
        "DESCANSO";

    const minutos =
        Math.max(
            1,
            Number(
                minutosDescanso || 5
            )
        );

    segundosTotais =
        minutos * 60;

    segundosRestantes =
        segundosTotais;

    timestampReferencia =
        Date.now();

    timerRodando =
        true;

    modoElemento.textContent =
        "DESCANSO";

    tarefaTitulo.textContent =
        "Hora de recuperar a energia";

    atualizarTimerVisual();

    atualizarControles();

    iniciarIntervalo();
}


function finalizarDescanso() {

    if (processandoEncerramento) {
        return;
    }

    processandoEncerramento =
        true;

    try {

        timerRodando =
            false;

        timestampReferencia =
            null;

        segundosRestantes =
            0;

        pararIntervalo();

        atualizarTimerVisual();

        /*
         * Avisa que o descanso terminou.
         */
        tocarAlertaFimPeriodo();

        faseAtual =
            "FOCO";

        resetarTimerVisual();

        statusTexto.textContent =
            "Descanso concluído! Quando quiser, inicie uma nova sessão de foco.";

    } finally {

        processandoEncerramento =
            false;
    }
}

/* =====================================================
   20. INTERROMPER
===================================================== */

async function interromperSessao() {

    if (!sessaoAtual) {
        return;
    }


    const confirmar =
        window.confirm(
            "Deseja realmente interromper esta sessão?"
        );


    if (!confirmar) {
        return;
    }


    try {

        if (timerRodando) {

            segundosRestantes =
                calcularSegundosRestantes();
        }


        const minutosRealizados =
            calcularMinutosRealizados();


        await apiFetch(
            `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco/${obterIdSessao(sessaoAtual)}/interromper`,
            {
                method: "PATCH",

                body:
                    JSON.stringify({
                        tempoFocoRealizado:
                        minutosRealizados
                    })
            }
        );


        timerRodando =
            false;


        pararIntervalo();

        pararAudioComSessao();

        limparEstadoTimer();


        sessaoAtual = null;


        statusTexto.textContent =
            "Sessão interrompida.";


        await atualizarListaSessoes();


        resetarTimerVisual();


    } catch (erro) {

        mostrarMensagem(
            erro.message
        );
    }
}


/* =====================================================
   21. MINUTOS REALIZADOS
===================================================== */

function calcularMinutosRealizados() {

    const segundosRealizados =
        Math.max(
            0,
            segundosTotais -
            segundosRestantes
        );


    /*
     * O backend exige inteiro positivo.
     *
     * Uma sessão encerrada antes de completar
     * um minuto será registrada como 1 minuto.
     */
    return Math.max(
        1,
        Math.round(
            segundosRealizados / 60
        )
    );
}


/* =====================================================
   22. PARAR INTERVALO
===================================================== */

function pararIntervalo() {

    if (timerInterval) {

        clearInterval(
            timerInterval
        );


        timerInterval = null;
    }
}


/* =====================================================
   23. TIMER VISUAL
===================================================== */

function atualizarTimerVisual() {

    const minutos =
        Math.floor(
            segundosRestantes / 60
        );

    const segundos =
        segundosRestantes % 60;


    timerElemento.textContent =
        `${String(minutos).padStart(2, "0")}:${String(segundos).padStart(2, "0")}`;


    document.title =
        `${timerElemento.textContent} — Prioris`;


    /*
     * DESCANSO
     */
    if (
        faseAtual ===
        "DESCANSO"
    ) {

        modoElemento.textContent =
            "DESCANSO";

        statusTexto.textContent =
            timerRodando
                ? "Descanso em andamento. Respire e recupere a energia."
                : "Descanso concluído.";

        return;
    }


    /*
     * FOCO
     */
    modoElemento.textContent =
        "FOCO";


    if (!sessaoAtual) {

        statusTexto.textContent =
            "Configure sua sessão e pressione iniciar.";

        return;
    }


    const status =
        String(
            sessaoAtual.status || ""
        ).toUpperCase();


    if (
        status === "PAUSADA"
    ) {

        statusTexto.textContent =
            "Sessão pausada. Retome quando estiver pronto.";

        return;
    }


    if (timerRodando) {

        statusTexto.textContent =
            "Proteja este tempo. Uma tarefa por vez.";

        return;
    }


    statusTexto.textContent =
        formatarStatus(
            status
        );
}


/* =====================================================
   24. RESET VISUAL
===================================================== */

function resetarTimerVisual() {

    faseAtual =
        "FOCO";

    const minutos =
        Number(
            selectTempo.value || 25
        );


    segundosTotais =
        minutos * 60;


    segundosRestantes =
        segundosTotais;


    timestampReferencia =
        null;


    timerRodando =
        false;


    tarefaTitulo.textContent =
        obterTituloTarefaSelecionada();


    modoElemento.textContent =
        "FOCO";


    atualizarTimerVisual();

    atualizarControles();


    document.title =
        "Sessão de Foco — Prioris";
}


/* =====================================================
   25. CONTROLES
===================================================== */

function atualizarControles() {

    /*
 * DESCANSO
 */
    if (
        faseAtual ===
        "DESCANSO"
    ) {

        esconder(btnIniciar);
        esconder(btnPausar);
        esconder(btnRetomar);
        esconder(btnFinalizar);
        esconder(btnInterromper);

        bloquearConfiguracao(
            true
        );

        definirTexto(
            "foco-sessao-status",
            "Descanso"
        );

        return;
    }
    const existeSessao =
        Boolean(
            sessaoAtual
        );


    const status =
        String(
            sessaoAtual?.status || ""
        ).toUpperCase();


    /*
     * SEM SESSÃO
     */
    if (!existeSessao) {

        mostrar(btnIniciar);

        esconder(btnPausar);
        esconder(btnRetomar);
        esconder(btnFinalizar);
        esconder(btnInterromper);


        bloquearConfiguracao(
            false
        );


        definirTexto(
            "foco-sessao-status",
            "—"
        );


        return;
    }


    /*
     * EM ANDAMENTO
     */
    if (
        status === "EM_ANDAMENTO"
    ) {

        esconder(btnIniciar);
        mostrar(btnPausar);
        esconder(btnRetomar);
        mostrar(btnFinalizar);
        mostrar(btnInterromper);


        bloquearConfiguracao(
            true
        );


        definirTexto(
            "foco-sessao-status",
            "Em andamento"
        );


        return;
    }


    /*
     * PAUSADA
     */
    if (
        status === "PAUSADA"
    ) {

        esconder(btnIniciar);
        esconder(btnPausar);
        mostrar(btnRetomar);
        mostrar(btnFinalizar);
        mostrar(btnInterromper);


        bloquearConfiguracao(
            true
        );


        definirTexto(
            "foco-sessao-status",
            "Pausada"
        );
    }
}


/* =====================================================
   26. BLOQUEAR CONFIGURAÇÃO
===================================================== */

function bloquearConfiguracao(
    bloquear
) {

    selectTarefa.disabled =
        bloquear;

    selectTempo.disabled =
        bloquear;

    selectDescanso.disabled =
        bloquear;
}


/* =====================================================
   27. TÍTULO DA TAREFA
===================================================== */

function atualizarTituloTarefa() {

    tarefaTitulo.textContent =
        obterTituloTarefaSelecionada();
}


function atualizarTituloTarefaDaSessao() {

    if (!sessaoAtual) {

        atualizarTituloTarefa();

        return;
    }


    const idTarefa =
        Number(
            sessaoAtual.idTarefa ??
            sessaoAtual.tarefa?.idTarefa
        );


    const tarefa =
        tarefas.find(
            item =>
                Number(
                    item.idTarefa
                ) === idTarefa
        );


    tarefaTitulo.textContent =
        sessaoAtual.tituloTarefa ??
        tarefa?.titulo ??
        "Sessão sem tarefa específica";
}


function obterTituloTarefaSelecionada() {

    const id =
        Number(
            selectTarefa.value
        );


    if (!id) {

        return "Sessão sem tarefa específica";
    }


    const tarefa =
        tarefas.find(
            item =>
                Number(
                    item.idTarefa
                ) === id
        );


    return (
        tarefa?.titulo ||
        "Tarefa selecionada"
    );
}


/* =====================================================
   28. ATUALIZAR LISTA APÓS AÇÃO
===================================================== */

async function atualizarListaSessoes() {

    try {

        sessoes =
            await apiFetch(
                `/usuarios/${ID_USUARIO_ATUAL}/sessoes-foco`
            );


        if (!Array.isArray(sessoes)) {
            sessoes = [];
        }


        renderizarResumo();

        renderizarHistorico();


    } catch (erro) {

        console.error(
            "Erro ao atualizar sessões:",
            erro
        );
    }
}


/* =====================================================
   29. RESUMO
===================================================== */

function renderizarResumo() {

    const concluidas =
        sessoes.filter(
            sessao =>
                String(
                    sessao.status
                ).toUpperCase() ===
                "CONCLUIDA"
        );


    const totalMinutos =
        sessoes.reduce(
            (
                acumulado,
                sessao
            ) => {

                return (
                    acumulado +
                    Number(
                        sessao
                            .tempoFocoRealizado ?? 0
                    )
                );

            },
            0
        );


    definirTexto(
        "foco-total-sessoes",
        concluidas.length
    );


    definirTexto(
        "foco-total-minutos",
        totalMinutos
    );


    if (sessaoAtual) {

        definirTexto(
            "foco-sessao-status",
            formatarStatus(
                sessaoAtual.status
            )
        );

    } else {

        definirTexto(
            "foco-sessao-status",
            "—"
        );
    }
}


/* =====================================================
   30. HISTÓRICO
===================================================== */

function renderizarHistorico() {

    if (!sessoes.length) {

        listaSessoes.innerHTML = `
            <div class="empty-state">
                Nenhuma sessão de foco registrada.
            </div>
        `;

        return;
    }


    const ordenadas =
        [...sessoes]
            .sort(
                (a, b) => {

                    const idA =
                        obterIdSessao(a);

                    const idB =
                        obterIdSessao(b);


                    return (
                        Number(idB) -
                        Number(idA)
                    );
                }
            );


    listaSessoes.innerHTML =
        ordenadas
            .map(
                criarHtmlSessao
            )
            .join("");
}


/* =====================================================
   31. ITEM DO HISTÓRICO
===================================================== */

function criarHtmlSessao(
    sessao
) {

    const idTarefa =
        Number(
            sessao.idTarefa ??
            sessao.tarefa?.idTarefa
        );


    const tarefa =
        tarefas.find(
            item =>
                Number(
                    item.idTarefa
                ) === idTarefa
        );


    const titulo =
        sessao.tituloTarefa ??
        tarefa?.titulo ??
        "Sessão sem tarefa específica";


    const status =
        String(
            sessao.status || ""
        ).toUpperCase();


    return `

        <article class="sessao-foco-item">

            <div class="sessao-foco-icon">
                ◉
            </div>


            <div class="sessao-foco-content">

                <strong>
                    ${escaparHtml(
        titulo
    )}
                </strong>


                <div class="sessao-foco-meta">

                    <span>
                        ${escaparHtml(
        formatarStatus(
            status
        )
    )}
                    </span>


                    <span>
                        ⏱ Planejado:
                        ${sessao.tempoFocoPlanejado ?? 0}
                        min
                    </span>


                    ${
        sessao.tempoFocoRealizado != null
            ? `
                                <span>
                                    ✓ Realizado:
                                    ${sessao.tempoFocoRealizado}
                                    min
                                </span>
                              `
            : ""
    }

                </div>

            </div>


            <span
                class="
                    sessao-status-badge
                    sessao-status-${status.toLowerCase()}
                "
            >
                ${escaparHtml(
        formatarStatus(
            status
        )
    )}
            </span>

        </article>
    `;
}


/* =====================================================
   32. LOCAL STORAGE DO TIMER
===================================================== */

function salvarEstadoTimer() {

    if (!sessaoAtual) {
        return;
    }


    const estado = {

        idSessao:
            obterIdSessao(
                sessaoAtual
            ),

        segundosTotais,

        segundosRestantes,

        timestampReferencia,

        timerRodando
    };


    localStorage.setItem(
        TIMER_STORAGE_KEY,
        JSON.stringify(
            estado
        )
    );
}


function lerEstadoTimer() {

    try {

        const valor =
            localStorage.getItem(
                TIMER_STORAGE_KEY
            );


        if (!valor) {
            return null;
        }


        return JSON.parse(
            valor
        );


    } catch {

        return null;
    }
}


function limparEstadoTimer() {

    localStorage.removeItem(
        TIMER_STORAGE_KEY
    );
}


/* =====================================================
   33. ID DA SESSÃO
===================================================== */

function obterIdSessao(
    sessao
) {

    return (
        sessao?.idSessaoFoco ??
        sessao?.idSessao ??
        sessao?.id
    );
}


/* =====================================================
   34. HELPERS DE UI
===================================================== */

function mostrar(
    elemento
) {

    elemento
        ?.classList
        .remove("hidden");
}


function esconder(
    elemento
) {

    elemento
        ?.classList
        .add("hidden");
}


function bloquearBotao(
    botao,
    bloquear
) {

    if (botao) {
        botao.disabled =
            bloquear;
    }
}


function mostrarMensagem(
    mensagem
) {

    if (mensagemFoco) {

        mensagemFoco.textContent =
            mensagem || "";
    }
}


function limparMensagem() {

    mostrarMensagem("");
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


/* =====================================================
   35. FORMATAÇÃO
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
   YOUTUBE IFRAME PLAYER API
===================================================== */

function carregarYouTubeIframeApi() {

    /*
     * Se a API já estiver disponível,
     * cria o player imediatamente.
     */
    if (
        window.YT &&
        window.YT.Player
    ) {

        criarPlayerYouTube();

        return;
    }


    /*
     * Função global exigida pela
     * YouTube IFrame Player API.
     */
    window.onYouTubeIframeAPIReady =
        criarPlayerYouTube;


    /*
     * Evita carregar o script duas vezes.
     */
    if (
        document.querySelector(
            "script[data-prioris-youtube-api]"
        )
    ) {

        return;
    }


    const script =
        document.createElement(
            "script"
        );


    script.src =
        "https://www.youtube.com/iframe_api";


    script.dataset.priorisYoutubeApi =
        "true";


    document.head.appendChild(
        script
    );
}


/* =====================================================
   CRIAR PLAYER
===================================================== */

function criarPlayerYouTube() {

    if (
        youtubePlayer ||
        !document.getElementById(
            "youtube-player"
        )
    ) {

        return;
    }


    youtubePlayer =
        new window.YT.Player(
            "youtube-player",
            {

                width: 640,

                height: 360,


                playerVars: {

                    autoplay: 0,

                    controls: 1,

                    playsinline: 1,

                    rel: 0,

                    origin:
                    window.location.origin
                },


                events: {

                    onReady:
                    aoPlayerYouTubePronto,

                    onError:
                    aoPlayerYouTubeErro
                }
            }
        );
}


/* =====================================================
   PLAYER PRONTO
===================================================== */

function aoPlayerYouTubePronto(event) {

    youtubeReady = true;


    const loading =
        document.getElementById(
            "youtube-player-loading"
        );


    if (loading) {
        loading.classList.add(
            "hidden"
        );
    }


    /*
     * Apenas prepara o vídeo inicial.
     * Não inicia automaticamente.
     */
    event.target.cueVideoById(
        YOUTUBE_PRESETS.focus.videoId
    );


    mostrarMensagemAudio(
        "Player pronto. Escolha um ambiente para começar."
    );


    console.log(
        "YouTube Player conectado ao Prioris."
    );
}

/* =====================================================
   ERRO DO YOUTUBE
===================================================== */

function aoPlayerYouTubeErro(
    event
) {

    console.error(
        "Erro no YouTube Player:",
        event.data
    );


    mostrarMensagemAudio(
        "Não foi possível reproduzir este conteúdo do YouTube."
    );
}


/* =====================================================
   ESCOLHER SOM
===================================================== */

function selecionarAudio(
    tipo,
    botao
) {

    if (
        !youtubeReady ||
        !youtubePlayer
    ) {

        mostrarMensagemAudio(
            "O player ainda está sendo carregado."
        );

        return;
    }


    const preset =
        YOUTUBE_PRESETS[tipo];


    if (!preset) {
        return;
    }


    try {

        youtubePlayer.loadVideoById({

            videoId:
            preset.videoId,

            startSeconds:
                0
        });


        document
            .querySelectorAll(
                ".foco-audio-option"
            )
            .forEach(
                item => {

                    item.classList.remove(
                        "active"
                    );
                }
            );


        botao.classList.add(
            "active"
        );


        mostrarMensagemAudio(
            `Reproduzindo: ${preset.nome}`
        );


    } catch (erro) {

        console.error(
            "Erro ao carregar áudio:",
            erro
        );


        mostrarMensagemAudio(
            "Não foi possível iniciar este som."
        );
    }
}

/* =====================================================
   INTEGRAR ÁUDIO AO POMODORO
===================================================== */

function iniciarAudioComSessao() {

    if (
        !youtubeReady ||
        !youtubePlayer
    ) {

        mostrarMensagemAudio(
            "O player ainda está sendo carregado."
        );

        return;
    }

    try {

        youtubePlayer.playVideo();

        mostrarMensagemAudio(
            "Áudio iniciado com a sessão de foco."
        );

    } catch (erro) {

        console.warn(
            "Não foi possível iniciar o áudio com a sessão:",
            erro
        );
    }
}

function pausarAudioComSessao() {

    if (
        !youtubeReady ||
        !youtubePlayer
    ) {

        return;
    }


    try {

        audioEstavaTocandoAntesDaPausa =
            youtubePlayer.getPlayerState() ===
            window.YT.PlayerState.PLAYING;


        if (
            audioEstavaTocandoAntesDaPausa
        ) {

            youtubePlayer.pauseVideo();
        }

    } catch (erro) {

        console.warn(
            "Não foi possível pausar o áudio:",
            erro
        );
    }
}


function retomarAudioComSessao() {

    if (
        !youtubeReady ||
        !youtubePlayer ||
        !audioEstavaTocandoAntesDaPausa
    ) {

        return;
    }


    try {

        youtubePlayer.playVideo();


        audioEstavaTocandoAntesDaPausa =
            false;

    } catch (erro) {

        console.warn(
            "Não foi possível retomar o áudio:",
            erro
        );
    }
}


function pararAudioComSessao() {

    if (
        !youtubeReady ||
        !youtubePlayer
    ) {

        return;
    }


    try {

        youtubePlayer.pauseVideo();


        audioEstavaTocandoAntesDaPausa =
            false;

    } catch (erro) {

        console.warn(
            "Não foi possível pausar o áudio:",
            erro
        );
    }
}


/* =====================================================
   MENSAGEM DO PLAYER
===================================================== */

function mostrarMensagemAudio(
    mensagem
) {

    const elemento =
        document.getElementById(
            "mensagem-audio"
        );


    if (elemento) {

        elemento.textContent =
            mensagem || "";
    }
}

/* =====================================================
   36. EVENTOS
===================================================== */

btnIniciar
    .addEventListener(
        "click",
        iniciarSessao
    );


btnPausar
    .addEventListener(
        "click",
        pausarSessao
    );


btnRetomar
    .addEventListener(
        "click",
        retomarSessao
    );


btnFinalizar
    .addEventListener(
        "click",
        () =>
            finalizarSessao(false)
    );


btnInterromper
    .addEventListener(
        "click",
        interromperSessao
    );


selectTarefa
    .addEventListener(
        "change",
        atualizarTituloTarefa
    );


selectTempo
    .addEventListener(
        "change",
        () => {

            if (!sessaoAtual) {
                resetarTimerVisual();
            }
        }
    );


/* =====================================================
   37. START
===================================================== */

document
    .querySelectorAll(
        ".foco-audio-option"
    )
    .forEach(
        botao => {

            botao.addEventListener(
                "click",
                () => {

                    selecionarAudio(
                        botao.dataset.audio,
                        botao
                    );
                }
            );
        }
    );

document.addEventListener(
    "DOMContentLoaded",
    carregarPagina
);