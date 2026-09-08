import { apiFetch } from "./api.js";


/* =====================================================
   PRIORIS — CADASTRO
===================================================== */


const formCadastro =
    document.getElementById(
        "cadastro-form"
    );


const inputNome =
    document.getElementById(
        "cadastro-nome"
    );


const inputEmail =
    document.getElementById(
        "cadastro-email"
    );


const inputSenha =
    document.getElementById(
        "cadastro-senha"
    );


const inputConfirmarSenha =
    document.getElementById(
        "cadastro-confirmar-senha"
    );


const mensagemCadastro =
    document.getElementById(
        "cadastro-message"
    );


const btnCadastrar =
    document.getElementById(
        "btn-cadastrar"
    );


const btnToggleSenha =
    document.getElementById(
        "btn-toggle-cadastro-password"
    );


/* =====================================================
   MOSTRAR / OCULTAR SENHA
===================================================== */

btnToggleSenha.addEventListener(
    "click",
    () => {

        const senhaVisivel =
            inputSenha.type === "text";


        inputSenha.type =
            senhaVisivel
                ? "password"
                : "text";


        inputConfirmarSenha.type =
            senhaVisivel
                ? "password"
                : "text";


        btnToggleSenha.textContent =
            senhaVisivel
                ? "Mostrar"
                : "Ocultar";
    }
);


/* =====================================================
   CADASTRAR
===================================================== */

formCadastro.addEventListener(
    "submit",
    async event => {

        event.preventDefault();


        limparMensagem();


        const nome =
            inputNome.value.trim();


        const email =
            inputEmail.value
                .trim()
                .toLowerCase();


        const senha =
            inputSenha.value;


        const confirmarSenha =
            inputConfirmarSenha.value;


        /* ==============================================
           VALIDAÇÕES DO FRONT
        ============================================== */

        if (nome.length < 2) {

            mostrarMensagem(
                "Informe um nome válido."
            );

            return;
        }


        if (senha.length < 6) {

            mostrarMensagem(
                "A senha deve possuir pelo menos 6 caracteres."
            );

            return;
        }


        if (
            senha !==
            confirmarSenha
        ) {

            mostrarMensagem(
                "As senhas não coincidem."
            );

            return;
        }


        const body = {

            nome,

            email,

            senha
        };


        try {

            bloquearFormulario(
                true
            );


            /*
             * Este endpoint já existe
             * no módulo de usuários.
             */
            const usuario =
                await apiFetch(
                    "/usuarios",
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
                "Usuário criado:",
                usuario
            );


            /*
             * Não vamos autenticar automaticamente.
             *
             * O usuário será enviado para
             * a tela de login.
             */
            sessionStorage.setItem(
                "priorisCadastroSucesso",
                "true"
            );


            window.location.href =
                "./login.html";


        } catch (erro) {

            mostrarMensagem(
                erro.message ||
                "Não foi possível criar sua conta."
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
    mensagem
) {

    mensagemCadastro.textContent =
        mensagem || "";


    mensagemCadastro.classList.add(
        "error"
    );
}


function limparMensagem() {

    mensagemCadastro.textContent =
        "";


    mensagemCadastro.classList.remove(
        "error"
    );
}


/* =====================================================
   BLOQUEAR FORM
===================================================== */

function bloquearFormulario(
    bloquear
) {

    btnCadastrar.disabled =
        bloquear;


    inputNome.disabled =
        bloquear;


    inputEmail.disabled =
        bloquear;


    inputSenha.disabled =
        bloquear;


    inputConfirmarSenha.disabled =
        bloquear;


    btnCadastrar.innerHTML =
        bloquear
            ? "Criando conta..."
            : `
                Criar minha conta
                <span>→</span>
              `;
}

console.log(
    "Tela de Cadastro do Prioris carregada."
);