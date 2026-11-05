package br.com.fiap.saneia.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.fiap.saneia.data.Relato
import br.com.fiap.saneia.data.StatusRelato
import br.com.fiap.saneia.ui.components.RelatoCard

/**
 * Tela de listagem/acompanhamento: relatos ordenados por prioridade,
 * com filtro por status e estado vazio.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    relatos: List<Relato>,
    filtro: StatusRelato?,
    snackbarHostState: SnackbarHostState,
    onFiltroSelecionado: (StatusRelato?) -> Unit,
    onRelatoClick: (Long) -> Unit,
    onNovoRelato: () -> Unit,
    onVoltar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatos por prioridade") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNovoRelato,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Novo relato") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filtro por status
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtro == null,
                    onClick = { onFiltroSelecionado(null) },
                    label = { Text("Todos") }
                )
                StatusRelato.entries.forEach { status ->
                    FilterChip(
                        selected = filtro == status,
                        onClick = { onFiltroSelecionado(status) },
                        label = { Text(status.rotulo) }
                    )
                }
            }

            if (relatos.isEmpty()) {
                EstadoVazio(filtroAtivo = filtro != null, onNovoRelato = onNovoRelato)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(relatos, key = { it.id }) { relato ->
                        RelatoCard(relato = relato, onClick = { onRelatoClick(relato.id) })
                    }
                }
            }
        }
    }
}

/** Mensagem exibida quando não há relatos para mostrar. */
@Composable
private fun EstadoVazio(filtroAtivo: Boolean, onNovoRelato: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.Inbox,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (filtroAtivo) "Nenhum relato com esse status" else "Nenhum relato ainda",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Viu algum problema de saneamento na sua rua? Seja o primeiro a reportar.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = onNovoRelato) { Text("Reportar um problema") }
    }
}
