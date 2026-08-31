import { apiFetch } from "./api.js";

async function testarApi() {

    const statusApi = document.getElementById("status-api");

    if (!statusApi) {
        console.error("Elemento #status-api não encontrado.");
        return;
    }

    try {

        const usuarios = await apiFetch("/usuarios");

        statusApi.textContent =
            `API conectada com sucesso. ${usuarios.length} usuário(s) encontrado(s).`;

        console.log("API conectada:", usuarios);

    } catch (erro) {

        statusApi.textContent =
            "Não foi possível conectar com a API.";

        console.error("Erro ao acessar a API:", erro);
    }
}

testarApi();