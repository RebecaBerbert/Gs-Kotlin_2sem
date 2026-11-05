package br.com.fiap.saneia.data

import kotlinx.coroutines.flow.Flow

/** Ponto único de acesso aos dados; os ViewModels não falam direto com o DAO. */
class RelatoRepository(private val dao: RelatoDao) {

    fun observarTodos(): Flow<List<Relato>> = dao.observarTodos()

    fun observarPorId(id: Long): Flow<Relato?> = dao.observarPorId(id)

    suspend fun inserir(relato: Relato): Long = dao.inserir(relato)

    suspend fun confirmar(relato: Relato) =
        dao.atualizar(relato.copy(confirmacoes = relato.confirmacoes + 1))

    suspend fun avancarStatus(relato: Relato) =
        dao.atualizar(relato.copy(status = relato.status.proximo()))

    suspend fun excluir(relato: Relato) = dao.excluir(relato)
}
