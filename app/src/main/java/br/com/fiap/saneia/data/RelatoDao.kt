package br.com.fiap.saneia.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/** Operações de banco sobre a tabela "relatos". */
@Dao
interface RelatoDao {

    /** Flow: a interface é atualizada sozinha sempre que a tabela muda. */
    @Query("SELECT * FROM relatos ORDER BY dataCriacao DESC")
    fun observarTodos(): Flow<List<Relato>>

    @Query("SELECT * FROM relatos WHERE id = :id")
    fun observarPorId(id: Long): Flow<Relato?>

    @Insert
    suspend fun inserir(relato: Relato): Long

    @Insert
    suspend fun inserirTodos(relatos: List<Relato>)

    @Update
    suspend fun atualizar(relato: Relato)

    @Delete
    suspend fun excluir(relato: Relato)
}
