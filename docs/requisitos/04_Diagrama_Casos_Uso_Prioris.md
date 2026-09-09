# Diagrama de Casos de Uso — PRIORIS

O diagrama abaixo apresenta o ator principal da V1 e os principais casos de uso.

```mermaid
flowchart LR
    U[👤 Usuário]

    subgraph P["Sistema PRIORIS"]
        UC1([Cadastrar-se])
        UC2([Realizar login])
        UC3([Gerenciar objetivos])
        UC4([Gerenciar metas])
        UC5([Gerenciar tarefas])
        UC6([Definir Prioridade #1])
        UC7([Gerenciar ciclo de 12 semanas])
        UC8([Criar planejamento semanal])
        UC9([Executar sessão de foco])
        UC10([Fazer revisão semanal])
        UC11([Consultar Dashboard])
        UC12([Consultar históricos])
    end

    U --- UC1
    U --- UC2
    U --- UC3
    U --- UC4
    U --- UC5
    U --- UC6
    U --- UC7
    U --- UC8
    U --- UC9
    U --- UC10
    U --- UC11
    U --- UC12

    UC3 --> UC4
    UC3 --> UC7
    UC4 --> UC5
    UC5 --> UC6
    UC5 --> UC8
    UC5 --> UC9
    UC8 --> UC10
    UC6 --> UC11
    UC8 --> UC11
    UC9 --> UC11
    UC10 --> UC11
```

## Leitura do diagrama

O **Usuário** é o ator principal da V1 do Prioris.

Ele interage com os módulos de cadastro/login, objetivos, metas, tarefas, Prioridade #1, ciclos, planejamento semanal, foco, revisão e Dashboard.

As setas internas indicam dependências conceituais do fluxo do sistema e não significam obrigatoriamente `include` ou `extend` da UML.
