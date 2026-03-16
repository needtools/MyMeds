package com.needtools.mymeds.screen

import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.needtools.mymeds.R
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.needtools.mymeds.util.MedsViewModel
import com.needtools.mymeds.util.PillStatusCalculator
import com.needtools.mymeds.util.PlaySound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedsScreen(
    viewModel: MedsViewModel,
    onAddPillClick: () -> Unit,
    navController: NavHostController,

    ) {

    val pills by viewModel.allPills.collectAsState(initial = emptyList())
    val pillsWithStatus by viewModel.pillsWithStatus.collectAsState(initial = emptyList())
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(onClick = onAddPillClick) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { navController.navigate("info") }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.info),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (pills.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.no_meds))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                itemsIndexed(
                    items = pillsWithStatus,
                    key = { _, item -> item.pill.id }
                ) { index, item ->
                    val isOverdue = item.isOverdue(context)
                    val daysSinceEnd = PillStatusCalculator.getDaysSinceCourseEnded(item.pill)

                    val cardColor = when {
                        daysSinceEnd != null && daysSinceEnd >= 0 -> {
                            Color(0xFFFFDADA)
                        }
                        isOverdue -> {
                            Color(0xFFDCEDC8)
                        }
                        else -> {
                            Color.LightGray
                        }
                    }

                    Box(modifier = Modifier.animateItem()) {
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            when (value) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    try {
                                        navController.navigate("edit/${item.pill.id}") {
                                            launchSingleTop =
                                                true
                                        }
                                    } catch (e: Exception) {
                                        TODO("Not yet implemented")
                                    }
                                    false
                                }

                                SwipeToDismissBoxValue.EndToStart -> {
                                    navController.navigate("history/${item.pill.id}") {
                                        launchSingleTop =
                                            true
                                    }
                                    false
                                }
                                else -> false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val alignment = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                else -> Alignment.Center
                            }
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
                                SwipeToDismissBoxValue.EndToStart -> Color(0xFF2196F3)
                                else -> Color.Transparent
                            }
                            val icon = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                                SwipeToDismissBoxValue.EndToStart -> Icons.Default.History
                                else -> Icons.Default.Info
                            }

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = alignment
                            ) {
                                Icon(icon, contentDescription = null, tint = Color.White)
                            }
                        }
                    ) {
                        PillItem(
                            pill = item.pill,
                            backgroundColor = cardColor,
                            isFirst = index == 0,
                            isLast = index == pillsWithStatus.size - 1,
                            onMoveUp = { viewModel.movePillUp(item.pill) },
                            onMoveDown = { viewModel.movePillDown(item.pill) },
                            missedTimes = PillStatusCalculator.getMissedTimes(item),
                            daysSinceEnd = PillStatusCalculator.getDaysSinceCourseEnded(item.pill),
                            onIntakeClick = {
                                PlaySound.playClickSound(context)
                                viewModel.recordIntake(item.pill.id)
                            }
                        )
                    }
                }

                }
            }
        }
    }
}


