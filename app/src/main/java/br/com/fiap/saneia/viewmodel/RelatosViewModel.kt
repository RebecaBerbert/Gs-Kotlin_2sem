package br.com.fiap.saneia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.saneia.data.Relato
import br.com.fiap.saneia.data.RelatoRepository
import br.com.fiap.saneia.data.SaneiaDatabase
import br.com.fiap.saneia.data.StatusRelato
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado da tela de detalhe: carregando, encontrado ou excluído/inexistente. */
sealed interface DetalheEstado {
    data object Carregando : DetalheEstado
    data class Encontrado(val relato: Relato) : DetalheEstado
    data object NaoEncontrado : DetalheEstado
}

/** Números exibidos na tela inicial. */
data class Resumo(val total: Int = 0, val abertos: Int = 0, val resolvidos: Int = 0)

/**
 * ViewModel compartilhado pelas telas Início, Relatos e Detalhe.
 * Mantém a lista (ordenada por prioridade), o filtro de status,
 * as ações sobre os relatos e as mensagens de feedback (Snackbar).
 */
class RelatosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        RelatoRepository(SaneiaDatabase.getInstance(application).relatoDao())

    /** Todos os relatos, do mais prioritário para o menos. */
    private val relatosOrdenados: StateFlow<List<Relato>> = repository.observarTodos()
        .map { lista -> lista.sortedByDescending { it.prioridade() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _filtro = MutableStateFlow<StatusRelato?>(null)
    val filtro: StateFlow<StatusRelato?> = _filtro.asStateFlow()

    /** Lista mostrada na tela Relatos, já com o filtro aplicado. */
    val relatosFiltrados: StateFlow<List<Relato>> =
        combine(relatosOrdenados, _filtro) { lista, filtro ->
            if (filtro == null) lista else lista.filter { it.status == filtro }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val resumo: StateFlow<Resumo> = relatosOrdenados
        .map { lista ->
            Resumo(
                total = lista.size,
                abertos = lista.count { it.status != StatusRelato.RESOLVIDO },
                resolvidos = lista.count { it.status == StatusRelato.RESOLVIDO }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Resumo())

    private val _mensagem = MutableStateFlow<String?>(null)
    /** Mensagem pendente para o Snackbar; null quando não há nada a mostrar. */
    val mensagem: StateFlow<String?> = _mensagem.asStateFlow()

    fun selecionarFiltro(status: StatusRelato?) {
        _filtro.value = status
    }

    fun observarRelato(id: Long): Flow<DetalheEstado> =
        repository.observarPorId(id).map { relato ->
            if (relato == null) DetalheEstado.NaoEncontrado else DetalheEstado.Encontrado(relato)
        }

    /** Ação adicional 1: vizinho confirma que também vê o problema. */
    fun confirmar(relato: Relato) = viewModelScope.launch {
        repository.confirmar(relato)
        mostrarMensagem("Confirmação registrada. Obrigado!")
    }

    /** Ação adicional 2: avança o status (Aberto → Em análise → Resolvido). */
    fun avancarStatus(relato: Relato) = viewModelScope.launch {
        val novo = relato.status.proximo()
        repository.avancarStatus(relato)
        mostrarMensagem("Status alterado para \"${novo.rotulo}\"")
    }

    /** Ação adicional 3: exclui o relato. */
    fun excluir(relato: Relato) = viewModelScope.launch {
        repository.excluir(relato)
        mostrarMensagem("Relato excluído")
    }

    fun mostrarMensagem(texto: String) {
        _mensagem.value = texto
    }

    fun mensagemExibida() {
        _mensagem.value = null
    }
}
