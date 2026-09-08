import { apiFetch } from "./api.js";


/* =====================================================
   PRIORIS — LOGIN
===================================================== */


/* =====================================================
   ELEMENTOS
===================================================== */

const formLogin =
    document.getElementById(
        "login-form"
    );


const inputEmail =
    document.getElementById(
        "login-email"
    );


const inputSenha =
    document.getElementById(
        "login-senha"
    );


const btnTogglePassword =
    document.getElementById(
        "btn-toggle-password"
    );


const btnLogin =
    document.getElementById(
        "btn-login"
    );


const mensagemLogin =
    document.getElementById(
        "login-message"
    );


/* =====================================================
   USUÁRIO JÁ LOGADO
===================================================== */

const idUsuarioLogado =
    localStorage.getItem(
        "idUsuario"
    );


if (idUsuarioLogado) {

    /*
     * Se já existe usuário autenticado,
     * não faz sentido mostrar Login.
     */
    window.location.href =
        "../index.html";
}


/* =====================================================
   CADASTRO REALIZADO COM SUCESSO
===================================================== */

const cadastroSucesso =
    sessionStorage.getItem(
        "priorisCadastroSucesso"
    );


if (cadastroSucesso === "true") {

    mostrarMensagem(
        "Conta criada com sucesso! Agora entre com seus dados.",
        "success"
    );


    /*
     * Remove a flag para a mensagem
     * não aparecer novamente depois.
     */
    sessionStorage.removeItem(
        "priorisCadastroSucesso"
    );
}


/* =====================================================
   MOSTRAR / OCULTAR SENHA
===================================================== */

btnTogglePassword.addEventListener(
    "click",
    () => {

        const senhaVisivel =
            inputSenha.type === "text";


        inputSenha.type =
            senhaVisivel
                ? "password"
                : "text";


        btnTogglePassword.textContent =
            senhaVisivel
                ? "Mostrar"
                : "Ocultar";
    }
);


/* =====================================================
   LOGIN
===================================================== */

formLogin.addEventListener(
    "submit",
    async event => {

        event.preventDefault();


        limparMensagem();


        const email =
            inputEmail.value
                .trim()
                .toLowerCase();


        const senha =
            inputSenha.value;


        if (!email) {

            mostrarMensagem(
                "Informe seu e-mail."
            );

            return;
        }


        if (!senha) {

            mostrarMensagem(
                "Informe sua senha."
            );

            return;
        }


        const body = {

            email,
            senha
        };


        try {

            bloquearFormulario(
                true
            );


            const usuario =
                await apiFetch(
                    "/auth/login",
                    {

                        method:
                            "POST",

                        body:
                            JSON.stringify(
                                body
                            )
                    }
                );


            console.log(
                "Login realizado:",
                usuario
            );


            /*
             * Guarda apenas os dados necessários
             * para identificar o usuário no front.
             */
            localStorage.setItem(
                "idUsuario",
                String(
                    usuario.idUsuario
                )
            );


            localStorage.setItem(
                "nomeUsuario",
                usuario.nome || ""
            );


            localStorage.setItem(
                "emailUsuario",
                usuario.email || ""
            );


            /*
             * Marca a sessão do front
             * como autenticada.
             */
            localStorage.setItem(
                "priorisAutenticado",
                "true"
            );


            mostrarMensagem(
                "Login realizado com sucesso!",
                "success"
            );


            /*
             * Pequeno atraso apenas para
             * o usuário perceber o feedback.
             */
            setTimeout(
                () => {

                    window.location.href =
                        "../index.html";
                },
                450
            );


        } catch (erro) {

            console.error(
                "Erro no login:",
                erro
            );


            mostrarMensagem(
                erro.message ||
                "E-mail ou senha inválidos."
            );


        } finally {

            bloquearFormulario(
                false
            );
        }
    }
);


/* =====================================================
   MENSAGENS
===================================================== */

function mostrarMensagem(
    mensagem,
    tipo = "error"
) {

    mensagemLogin.textContent =
        mensagem || "";


    mensagemLogin.classList.remove(
        "error",
        "success"
    );


    mensagemLogin.classList.add(
        tipo
    );
}


function limparMensagem() {

    mensagemLogin.textContent =
        "";


    mensagemLogin.classList.remove(
        "error",
        "success"
    );
}


/* =====================================================
   BLOQUEAR FORMULÁRIO
===================================================== */

function bloquearFormulario(
    bloquear
) {

    inputEmail.disabled =
        bloquear;


    inputSenha.disabled =
        bloquear;


    btnTogglePassword.disabled =
        bloquear;


    btnLogin.disabled =
        bloquear;


    btnLogin.innerHTML =
        bloquear
            ? `
                Entrando...
              `
            : `
                Entrar no Prioris
                <span>→</span>
              `;
}


/* =====================================================
   START
===================================================== */

console.log(
    "Tela de Login do Prioris carregada."
);