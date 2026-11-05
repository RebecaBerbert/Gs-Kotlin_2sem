package br.com.fiap.saneia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import br.com.fiap.saneia.data.Categoria
import br.com.fiap.saneia.data.Gravidade
import br.com.fiap.saneia.ui.components.icone
import br.com.fiap.saneia.viewmodel.CadastroUiState

/**
 * Tela de cadastro de um novo relato.
 * Endereço, bairro e descrição são obrigatórios: com algum inválido,
 * o relato não é salvo e o campo mostra o erro.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CadastroScreen(
    estado: CadastroUiState,
    snackbarHostState: SnackbarHostState,
    onCategoria: (Categoria) -> Unit,
    onGravidade: (Gravidade) -> Unit,
    onEndereco: (String) -> Unit,
    onBairro: (String) -> Unit,
    onDescricao: (String) -> Unit,
    onEnviar: () -> Unit,
    onVoltar: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo relato") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Qual é o problema?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Categoria.entries.forEach { categoria ->
                    FilterChip(
                        selected = estado.categoria == categoria,
                        onClick = { onCategoria(categoria) },
                        label = { Text(categoria.rotulo) },
                        leadingIcon = { Icon(categoria.icone(), contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            Text("Gravidade", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gravidade.entries.forEach { gravidade ->
                    FilterChip(
                        selected = estado.gravidade == gravidade,
                        onClick = { onGravidade(gravidade) },
                        label = { Text(gravidade.rotulo) }
                    )
                }
            }

            OutlinedTextField(
                value = estado.endereco,
                onValueChange = onEndereco,
                label = { Text("Endereço *") },
                placeholder = { Text("Ex.: Rua das Flores, 100") },
                isError = estado.tentouEnviar && estado.erroEndereco != null,
                supportingText = {
                    if (estado.tentouEnviar) estado.erroEndereco?.let { Text(it) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.bairro,
                onValueChange = onBairro,
                label = { Text("Bairro *") },
                isError = estado.tentouEnviar && estado.erroBairro != null,
                supportingText = {
                    if (estado.tentouEnviar) estado.erroBairro?.let { Text(it) }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.descricao,
                onValueChange = onDescricao,
                label = { Text("Descrição *") },
                placeholder = { Text("O que está acontecendo? Há quanto tempo?") },
                isError = estado.tentouEnviar && estado.erroDescricao != null,
                supportingText = {
                    if (estado.tentouEnviar) estado.erroDescricao?.let { Text(it) }
                    else Text("${estado.descricao.length} caracteres")
                },
                minLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "* campos obrigatórios",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )

            Button(
                onClick = onEnviar,
                enabled = !estado.salvando,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (estado.salvando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(Modifier.size(8.dp))
                    Text("Enviar relato")
                }
            }
        }
    }
}
