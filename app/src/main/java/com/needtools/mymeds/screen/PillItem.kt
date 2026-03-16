package com.needtools.mymeds.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.needtools.mymeds.db.Pill
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.needtools.mymeds.R
import com.needtools.mymeds.util.PlaySound

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PillItem(
    pill: Pill,
    backgroundColor: Color,
    isFirst: Boolean,
    isLast: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    missedTimes: List<String>,
    daysSinceEnd: Int?,
    onIntakeClick: () -> Unit
) {

    val isMissed = missedTimes.isNotEmpty()
    val context = LocalContext.current

    LaunchedEffect(isMissed) {
        if (isMissed) {
            PlaySound.playNotificationSound(context)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (pill.scheduleTime.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterStart),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }

                Text(
                    text = pill.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1.2f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.form_pill)}: ${pill.form}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${stringResource(R.string.pill_dose)} ${pill.dose}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    if (!pill.intakeInstructions.isNullOrBlank()) {
                        Text(
                            text = pill.intakeInstructions,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (pill.scheduleTime.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            pill.scheduleTime.forEach { time ->
                                Text(
                                    text = "${stringResource(R.string.time)} $time",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = onIntakeClick,
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.button_take),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isFirst) {
                        IconButton(onClick = onMoveUp, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = stringResource(R.string.move_up))
                        }
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }

                    if (!isLast) {
                        IconButton(onClick = onMoveDown, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(R.string.move_down))
                        }
                    }
                }
            }
            if ((daysSinceEnd != null && daysSinceEnd >= 0) || missedTimes.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 2.dp,
                    color = Color.White)

                val (text, textColor) = when {
                    daysSinceEnd == 0 -> {
                        stringResource(R.string.course_finished_today) to Color.Red
                    }
                    daysSinceEnd != null && daysSinceEnd >= 0 -> {
                        "${stringResource(R.string.course_finished)} $daysSinceEnd ${stringResource(R.string.days_ago)}" to Color.Red
                    }
                    missedTimes.isNotEmpty() -> {
                        "${stringResource(R.string.passed)} ${missedTimes.joinToString(", ")}" to Color(0xFF2E7D32)
                    }
                    else -> "" to Color.Transparent
                }

                if (text.isNotEmpty()) {
                    Text(
                        text = text,
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


