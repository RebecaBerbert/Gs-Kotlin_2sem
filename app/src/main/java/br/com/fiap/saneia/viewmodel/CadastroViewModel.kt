package br.com.fiap.saneia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.saneia.data.Categoria
import br.com.fiap.saneia.data.Gravidade
import br.com.fiap.saneia.data.Relato
import br.com.fiap.saneia.data.RelatoRepository
import br.com.fiap.saneia.data.SaneiaDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado do formulário "Novo relato". */
data class CadastroUiState(
    val categoria: Categoria = Categoria.ESGOTO,
    val gravidade: Gravidade = Gravidade.MEDIA,
    val endereco: String = "",
    val bairro: String = "",
    val descricao: String = "",
    val tentouEnviar: Boolean = false,
    val salvando: Boolean = false
) {
    val erroEndereco: String?
        get() = if (endereco.trim().length < 5) "Informe o endereço (mínimo 5 caracteres)" else null

    val erroBairro: String?
        get() = if (bairro.isBlank()) "Informe o bairro" else null

    val erroDescricao: String?
        get() = if (descricao.trim().length < 10) "Descreva o problema (mínimo 10 caracteres)" else null

    val valido: Boolean
        get() = erroEndereco == null && erroBairro == null && erroDescricao == null
}

/** Mantém o formulário, valida os campos obrigatórios e salva no Room. */
class CadastroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        RelatoRepository(SaneiaDatabase.getInstance(application).relatoDao())

    private val _uiState = MutableStateFlow(CadastroUiState())
    val uiState: StateFlow<CadastroUiState> = _uiState.asStateFlow()

    fun alterarCategoria(valor: Categoria) = _uiState.update { it.copy(categoria = valor) }
    fun alterarGravidade(valor: Gravidade) = _uiState.update { it.copy(gravidade = valor) }
    fun alterarEndereco(valor: String) = _uiState.update { it.copy(endereco = valor) }
    fun alterarBairro(valor: String) = _uiState.update { it.copy(bairro = valor) }
    fun alterarDescricao(valor: String) = _uiState.update { it.copy(descricao = valor) }

    /**
     * Valida e salva. Se algum campo obrigatório estiver inválido,
     * nada é gravado e os erros passam a aparecer no formulário.
     */
    fun salvar(onSucesso: () -> Unit) {
        val estado = _uiState.value
        if (!estado.valido) {
            _uiState.update { it.copy(tentouEnviar = true) }
            return
        }
        if (estado.salvando) return
        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            repository.inserir(
                Relato(
                    categoria = estado.categoria,
                    descricao = estado.descricao.trim(),
                    endereco = estado.endereco.trim(),
                    bairro = estado.bairro.trim(),
                    gravidade = estado.gravidade
                )
            )
            _uiState.value = CadastroUiState()
            onSucesso()
        }
    }
}
