package br.com.fiap.saneia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.saneia.data.Relato
import br.com.fiap.saneia.data.StatusRelato
import br.com.fiap.saneia.ui.components.StatusEtiqueta
import br.com.fiap.saneia.ui.components.icone
import br.com.fiap.saneia.viewmodel.DetalheEstado
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Tela de detalhe: mostra todos os dados do relato e as ações
 * adicionais (confirmar, avançar status e excluir).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheScreen(
    estado: DetalheEstado,
    snackbarHostState: SnackbarHostState,
    onConfirmar: (Relato) -> Unit,
    onAvancarStatus: (Relato) -> Unit,
    onExcluir: (Relato) -> Unit,
    onVoltar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do relato") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (estado) {
                DetalheEstado.Carregando ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                DetalheEstado.NaoEncontrado ->
                    Text(
                        "Este relato não existe mais.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )

                is DetalheEstado.Encontrado -> ConteudoDetalhe(
                    relato = estado.relato,
                    onConfirmar = onConfirmar,
                    onAvancarStatus = onAvancarStatus,
                    onExcluir = onExcluir
                )
            }
        }
    }
}

@Composable
private fun ConteudoDetalhe(
    relato: Relato,
    onConfirmar: (Relato) -> Unit,
    onAvancarStatus: (Relato) -> Unit,
    onExcluir: (Relato) -> Unit
) {
    var confirmarExclusao by remember { mutableStateOf(false) }
    val formatoData = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                relato.categoria.icone(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                relato.categoria.rotulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            StatusEtiqueta(relato.status)
        }

        Text(relato.descricao, style = MaterialTheme.typography.bodyLarge)

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinhaInfo("Endereço", relato.endereco)
                LinhaInfo("Bairro", relato.bairro)
                LinhaInfo("Gravidade", relato.gravidade.rotulo)
                LinhaInfo("Confirmações", "${relato.confirmacoes} vizinho(s)")
                LinhaInfo("Reportado em", formatoData.format(Date(relato.dataCriacao)))
                LinhaInfo("Dias em aberto", "${relato.diasEmAberto()}")
                HorizontalDivider()
                LinhaInfo(
                    "Prioridade",
                    if (relato.status == StatusRelato.RESOLVIDO) "Resolvido" else "${relato.prioridade()}"
                )
            }
        }

        // Ações adicionais sobre o registro
        Button(
            onClick = { onConfirmar(relato) },
            enabled = relato.status != StatusRelato.RESOLVIDO,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Filled.ThumbUp, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Também vejo isso")
        }

        OutlinedButton(
            onClick = { onAvancarStatus(relato) },
            enabled = relato.status != StatusRelato.RESOLVIDO,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Icon(Icons.Filled.DoneAll, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                if (relato.status == StatusRelato.RESOLVIDO) "Relato resolvido"
                else "Mover para \"${relato.status.proximo().rotulo}\""
            )
        }

        TextButton(
            onClick = { confirmarExclusao = true },
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Delete, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Excluir relato")
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir relato?") },
            text = { Text("Essa ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarExclusao = false
                    onExcluir(relato)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun LinhaInfo(rotulo: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            rotulo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.weight(1f)
        )
        Text(valor, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
