/* =====================================================
   PRIORIS — CONFIGURAÇÃO DA API
===================================================== */

const API_BASE_URL =
    "http://localhost:8080/api";


/* =====================================================
   FETCH PADRÃO DA API
===================================================== */

export async function apiFetch(
    endpoint,
    options = {}
) {

    const headers = {
        ...options.headers
    };


    /*
     * Só envia Content-Type JSON quando
     * realmente existe um corpo na requisição.
     */

    if (options.body) {

        headers["Content-Type"] =
            "application/json";
    }


    try {

        const response =
            await fetch(
                `${API_BASE_URL}${endpoint}`,
                {
                    ...options,
                    headers
                }
            );


        /* =============================================
           TRATAMENTO DE ERROS HTTP
        ============================================= */

        if (!response.ok) {

            let erro;

            try {

                erro =
                    await response.json();

            } catch {

                erro = {
                    mensagem:
                        "Erro inesperado ao acessar a API."
                };
            }


            const erroApi =
                new Error(
                    erro.mensagem ||
                    erro.message ||
                    "Erro ao realizar requisição."
                );


            erroApi.status =
                response.status;


            erroApi.dados =
                erro;


            throw erroApi;


            /*
             * Guardamos informações adicionais
             * para facilitar o tratamento no app.js.
             */

            apiError.status =
                response.status;

            apiError.statusText =
                response.statusText;

            apiError.dados =
                erro;

            throw apiError;
        }


        /* =============================================
           RESPOSTAS SEM CONTEÚDO
        ============================================= */

        if (response.status === 204) {
            return null;
        }


        /*
         * Alguns endpoints podem responder
         * sucesso sem possuir JSON no corpo.
         */

        const contentType =
            response.headers.get(
                "content-type"
            );


        if (
            !contentType ||
            !contentType.includes(
                "application/json"
            )
        ) {

            return null;
        }


        return await response.json();


    } catch (erro) {

        /*
         * Erros HTTP criados acima continuam
         * sendo enviados para quem chamou apiFetch.
         *
         * Também captura erros como:
         * - backend desligado;
         * - erro de rede;
         * - CORS;
         * - conexão recusada.
         */

        throw erro;
    }
}