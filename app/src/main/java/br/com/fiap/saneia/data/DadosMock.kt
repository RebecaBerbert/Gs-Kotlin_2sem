package br.com.fiap.saneia.data

import java.util.concurrent.TimeUnit

/**
 * Relatos fictícios inseridos na primeira vez que o banco é criado,
 * para o app não começar vazio na demonstração.
 */
object DadosMock {

    private fun diasAtras(dias: Long): Long =
        System.currentTimeMillis() - TimeUnit.DAYS.toMillis(dias)

    val relatos = listOf(
        Relato(
            categoria = Categoria.ESGOTO,
            descricao = "Esgoto vazando pela calçada há semanas, cheiro forte perto da escola.",
            endereco = "Rua das Palmeiras, 120",
            bairro = "Itaquera",
            gravidade = Gravidade.ALTA,
            confirmacoes = 12,
            dataCriacao = diasAtras(21)
        ),
        Relato(
            categoria = Categoria.VAZAMENTO,
            descricao = "Cano estourado jorrando água limpa na esquina.",
            endereco = "Av. Jacu-Pêssego, 2300",
            bairro = "Guaianases",
            gravidade = Gravidade.MEDIA,
            confirmacoes = 5,
            status = StatusRelato.EM_ANALISE,
            dataCriacao = diasAtras(4)
        ),
        Relato(
            categoria = Categoria.FALTA_DAGUA,
            descricao = "Rua inteira sem água desde ontem à noite, sem aviso prévio.",
            endereco = "Rua Sapopemba, 45",
            bairro = "Sapopemba",
            gravidade = Gravidade.ALTA,
            confirmacoes = 8,
            dataCriacao = diasAtras(1)
        ),
        Relato(
            categoria = Categoria.BUEIRO,
            descricao = "Bueiro entupido com lixo, alaga a rua quando chove.",
            endereco = "Rua Coronel Rodovalho, 300",
            bairro = "Penha",
            gravidade = Gravidade.BAIXA,
            confirmacoes = 2,
            dataCriacao = diasAtras(9)
        ),
        Relato(
            categoria = Categoria.VAZAMENTO,
            descricao = "Vazamento pequeno na tampa do hidrômetro da praça.",
            endereco = "Praça Silvio Romero, s/n",
            bairro = "Tatuapé",
            gravidade = Gravidade.BAIXA,
            confirmacoes = 1,
            status = StatusRelato.RESOLVIDO,
            dataCriacao = diasAtras(15)
        )
    )
}
