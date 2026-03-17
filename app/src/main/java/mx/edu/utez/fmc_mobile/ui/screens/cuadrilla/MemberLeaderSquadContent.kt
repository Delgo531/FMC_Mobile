package mx.edu.utez.fmc_mobile.ui.screens.cuadrilla

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.edu.utez.fmc_mobile.data.session.SquadInfo
import mx.edu.utez.fmc_mobile.data.session.SquadMemberInfo
import mx.edu.utez.fmc_mobile.data.session.SquadRole
import mx.edu.utez.fmc_mobile.ui.theme.AppTypography
import mx.edu.utez.fmc_mobile.ui.theme.Primary
import mx.edu.utez.fmc_mobile.ui.theme.Surface
import mx.edu.utez.fmc_mobile.ui.theme.TextPrimary
import mx.edu.utez.fmc_mobile.ui.theme.TextSecondary

/**
 * Contenido de la pestaña Cuadrilla para usuarios que ya son miembro o líder de una cuadrilla.
 * Muestra nombre de la cuadrilla, municipio, contadores Pendientes/Resueltos, lista de miembros y botón Abandonar.
 */
@Composable
fun MemberLeaderSquadContent(
    squad: SquadInfo,
    onAbandonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = squad.name,
            style = AppTypography.Subtitle,
            color = TextPrimary
        )
        Text(
            text = "${squad.municipality}, Morelos",
            style = AppTypography.Body,
            color = TextSecondary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CountChip(label = "Pendientes", count = squad.pendingCount)
            CountChip(label = "Resueltos", count = squad.resolvedCount)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Integrantes",
            style = AppTypography.Body.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )

        squad.members.forEach { member ->
            SquadMemberCard(member = member)
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onAbandonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Abandonar")
        }
    }
}

@Composable
private fun CountChip(
    label: String,
    count: Int
) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = AppTypography.Title,
                color = Primary
            )
            Text(
                text = label,
                style = AppTypography.Caption,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SquadMemberCard(
    member: SquadMemberInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.username,
                    style = AppTypography.Body.copy(fontWeight = FontWeight.Medium),
                    color = TextPrimary
                )
            }
            if (member.role == SquadRole.LEADER) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Primary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LÍDER",
                        style = AppTypography.Caption,
                        color = Primary
                    )
                }
            }
        }
    }
}
