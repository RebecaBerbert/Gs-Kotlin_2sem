package br.com.fiap.saneia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Banco local do app (Room). Os dados ficam no aparelho e continuam
 * disponíveis depois de fechar e abrir o app novamente.
 */
@Database(entities = [Relato::class], version = 1, exportSchema = false)
abstract class SaneiaDatabase : RoomDatabase() {

    abstract fun relatoDao(): RelatoDao

    companion object {
        @Volatile
        private var INSTANCE: SaneiaDatabase? = null

        fun getInstance(context: Context): SaneiaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SaneiaDatabase::class.java,
                    "saneia.db"
                )
                    .addCallback(SeedCallback())
                    .build()
                    .also { INSTANCE = it }
            }
    }

    /** Insere os dados mockados só quando o banco é criado pela primeira vez. */
    private class SeedCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                INSTANCE?.relatoDao()?.inserirTodos(DadosMock.relatos)
            }
        }
    }
}
