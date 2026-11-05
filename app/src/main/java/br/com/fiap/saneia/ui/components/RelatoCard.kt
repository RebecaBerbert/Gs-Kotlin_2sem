package br.com.fiap.saneia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.saneia.data.Categoria
import br.com.fiap.saneia.data.Relato
import br.com.fiap.saneia.data.StatusRelato
import br.com.fiap.saneia.ui.theme.AmareloAlerta
import br.com.fiap.saneia.ui.theme.AmareloClaro
import br.com.fiap.saneia.ui.theme.AzulAgua
import br.com.fiap.saneia.ui.theme.AzulClaro
import br.com.fiap.saneia.ui.theme.VerdeClaro
import br.com.fiap.saneia.ui.theme.VerdeSaneia

/** Ícone que representa cada categoria. */
fun Categoria.icone(): ImageVector = when (this) {
    Categoria.ESGOTO -> Icons.Filled.Warning
    Categoria.VAZAMENTO -> Icons.Filled.WaterDrop
    Categoria.FALTA_DAGUA -> Icons.Filled.Opacity
    Categoria.BUEIRO -> Icons.Filled.Plumbing
}

/** Etiqueta colorida com o status do relato. */
@Composable
fun StatusEtiqueta(status: StatusRelato) {
    val (fundo, texto) = when (status) {
        StatusRelato.ABERTO -> AmareloClaro to AmareloAlerta
        StatusRelato.EM_ANALISE -> AzulClaro to AzulAgua
        StatusRelato.RESOLVIDO -> VerdeClaro to VerdeSaneia
    }
    Text(
        text = status.rotulo,
        color = texto,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(fundo, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/** Card de um relato na lista de acompanhamento. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelatoCard(relato: Relato, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = relato.categoria.icone(),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = relato.categoria.rotulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                StatusEtiqueta(relato.status)
            }
            Spacer(Modifier.size(8.dp))
            Text(
                text = relato.descricao,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.size(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Filled.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(
                    text = "${relato.bairro} · ${relato.endereco}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Filled.People, contentDescription = "Confirmações", modifier = Modifier.size(16.dp))
                Text(text = "${relato.confirmacoes}", style = MaterialTheme.typography.bodySmall)
            }
            if (relato.status != StatusRelato.RESOLVIDO) {
                Spacer(Modifier.size(4.dp))
                Text(
                    text = "Prioridade ${relato.prioridade()} · gravidade ${relato.gravidade.rotulo.lowercase()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}
