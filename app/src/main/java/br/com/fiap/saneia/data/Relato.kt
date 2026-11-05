package br.com.fiap.saneia.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.TimeUnit

/** Tipo de problema de saneamento reportado pelo morador. */
enum class Categoria(val rotulo: String) {
    ESGOTO("Esgoto a céu aberto"),
    VAZAMENTO("Vazamento de água"),
    FALTA_DAGUA("Falta d'água"),
    BUEIRO("Bueiro entupido")
}

/** Ciclo de vida de um relato, do envio até a resolução. */
enum class StatusRelato(val rotulo: String) {
    ABERTO("Aberto"),
    EM_ANALISE("Em análise"),
    RESOLVIDO("Resolvido");

    /** Próximo status do fluxo; o último permanece igual. */
    fun proximo(): StatusRelato = StatusRelato.entries.getOrElse(ordinal + 1) { this }
}

/** Gravidade informada pelo morador (1 = baixa, 2 = média, 3 = alta). */
enum class Gravidade(val rotulo: String, val peso: Int) {
    BAIXA("Baixa", 1),
    MEDIA("Média", 2),
    ALTA("Alta", 3)
}

/**
 * Entidade principal do app: um relato de problema de saneamento.
 * Fica salva localmente no Room (tabela "relatos").
 */
@Entity(tableName = "relatos")
data class Relato(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoria: Categoria,
    val descricao: String,
    val endereco: String,
    val bairro: String,
    val gravidade: Gravidade,
    val confirmacoes: Int = 0,
    val status: StatusRelato = StatusRelato.ABERTO,
    val dataCriacao: Long = System.currentTimeMillis()
) {
    /** Dias desde que o relato foi criado. */
    fun diasEmAberto(agora: Long = System.currentTimeMillis()): Long =
        TimeUnit.MILLISECONDS.toDays(agora - dataCriacao).coerceAtLeast(0)

    /**
     * Prioridade = gravidade + confirmações da vizinhança + tempo em aberto.
     * Relatos resolvidos vão para o fim da fila (prioridade 0).
     */
    fun prioridade(agora: Long = System.currentTimeMillis()): Int {
        if (status == StatusRelato.RESOLVIDO) return 0
        return gravidade.peso * 10 + confirmacoes * 2 + diasEmAberto(agora).toInt()
    }
}
