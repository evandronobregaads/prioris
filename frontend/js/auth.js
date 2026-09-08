/* =====================================================
   PRIORIS — CONTROLE DE AUTENTICAÇÃO
===================================================== */


/* =====================================================
   DADOS DO USUÁRIO
===================================================== */

export function getUsuarioId() {

    const autenticado =
        localStorage.getItem(
            "priorisAutenticado"
        );


    const idUsuario =
        Number(
            localStorage.getItem(
                "idUsuario"
            )
        );


    if (
        autenticado !== "true" ||
        !idUsuario ||
        Number.isNaN(idUsuario)
    ) {

        return null;
    }


    return idUsuario;
}


export function getUsuarioNome() {

    return (
        localStorage.getItem(
            "nomeUsuario"
        ) || ""
    );
}


export function getUsuarioEmail() {

    return (
        localStorage.getItem(
            "emailUsuario"
        ) || ""
    );
}


/* =====================================================
   VERIFICAR AUTENTICAÇÃO
===================================================== */

export function exigirAutenticacao() {

    const idUsuario =
        getUsuarioId();


    if (!idUsuario) {

        limparSessao();


        redirecionarParaLogin();


        return null;
    }


    return idUsuario;
}


/* =====================================================
   LOGOUT
===================================================== */

export function logout() {

    limparSessao();


    redirecionarParaLogin();
}


/* =====================================================
   LIMPAR SESSÃO
===================================================== */

export function limparSessao() {

    localStorage.removeItem(
        "idUsuario"
    );

    localStorage.removeItem(
        "nomeUsuario"
    );

    localStorage.removeItem(
        "emailUsuario"
    );

    localStorage.removeItem(
        "priorisAutenticado"
    );
}


/* =====================================================
   REDIRECIONAMENTO
===================================================== */

function redirecionarParaLogin() {

    /*
     * auth.js sempre está dentro de:
     *
     * frontend/js/auth.js
     *
     * Então usamos a própria localização
     * do arquivo JS como referência.
     *
     * ../login.html
     * sempre será:
     *
     * frontend/login.html
     */
    const urlLogin =
        new URL(
            "../pages/login.html",
            import.meta.url
        );


    window.location.href =
        urlLogin.href;
}