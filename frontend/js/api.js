const API_BASE_URL = "http://localhost:8080/api";

export async function apiFetch(endpoint, options = {}) {

    const response = await fetch(
        `${API_BASE_URL}${endpoint}`,
        {
            headers: {
                "Content-Type": "application/json",
                ...options.headers
            },

            ...options
        }
    );

    if (!response.ok) {

        let erro;

        try {
            erro = await response.json();
        } catch {
            erro = {
                mensagem: "Erro inesperado ao acessar a API."
            };
        }

        throw new Error(
            erro.mensagem || "Erro ao realizar requisição."
        );
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}