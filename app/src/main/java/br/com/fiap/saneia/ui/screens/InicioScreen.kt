package br.com.fiap.saneia.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.fiap.saneia.viewmodel.Resumo

/**
 * Tela inicial: apresenta o app (nome, objetivo, público, ODS)
 * e dá acesso às funcionalidades.
 */
@Composable
fun InicioScreen(
    resumo: Resumo,
    snackbarHostState: SnackbarHostState,
    onNovoRelato: () -> Unit,
    onVerRelatos: () -> Unit
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabeçalho com a identidade visual
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Saneia",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sua rua com saneamento de verdade",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoCard(
                    titulo = "Objetivo",
                    texto = "Facilitar o relato de problemas de saneamento (esgoto a céu aberto, " +
                        "vazamentos, falta d'água e bueiros entupidos) e priorizar os casos mais graves."
                )
                InfoCard(
                    titulo = "Para quem",
                    texto = "Moradores de bairros com saneamento precário, que hoje não sabem a quem " +
                        "reclamar nem recebem retorno sobre o problema."
                )

                // Resumo vindo do banco local
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NumeroCard("Relatos", resumo.total, Modifier.weight(1f))
                    NumeroCard("Em aberto", resumo.abertos, Modifier.weight(1f))
                    NumeroCard("Resolvidos", resumo.resolvidos, Modifier.weight(1f))
                }

                Button(onClick = onNovoRelato, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Icon(Icons.Filled.AddAlert, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Reportar um problema")
                }
                OutlinedButton(onClick = onVerRelatos, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Acompanhar relatos")
                }

                Text(
                    text = "Alinhado aos ODS 6 (Água potável e saneamento) e 11 (Cidades sustentáveis)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoCard(titulo: String, texto: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun NumeroCard(rotulo: String, valor: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$valor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(rotulo, style = MaterialTheme.typography.labelMedium)
        }
    }
}
