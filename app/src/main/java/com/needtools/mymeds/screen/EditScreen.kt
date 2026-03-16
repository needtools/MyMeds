package com.needtools.mymeds.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.needtools.mymeds.util.MedsViewModel
import com.needtools.mymeds.util.PillFormEnum
import com.needtools.mymeds.R
import com.needtools.mymeds.util.IntakeEnum
import com.needtools.mymeds.util.PlaySound
import com.needtools.mymeds.util.TimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    pillId: Int?,
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: MedsViewModel
) {

    val context = LocalContext.current
    val pillFromDb by viewModel.getPillById(pillId ?: -1).collectAsState(initial = null)

    var pillName by remember { mutableStateOf("") }
    var pillDose by remember { mutableStateOf("") }
    var takenForm by remember { mutableStateOf("") }
    var takenInstruction by remember { mutableStateOf("") }
    var isPermanent by remember { mutableStateOf(false) }
    var intakeDuration by remember { mutableStateOf("") }
    var numberOfIntakes by remember { mutableStateOf("1") }
    val scheduleTimes = remember { mutableStateListOf<String>() }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val intakeOptions = (1..6).toList().map { it.toString() }
    var isFormDropdownExpanded by remember { mutableStateOf(false) }
    var isInstructionDropdownExpanded by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCustomFormDialog by remember { mutableStateOf(false) }
    var showCustomIntakeDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val formItems = PillFormEnum.entries.map { it.name to stringResource(it.resId) }
    val instructionItems = IntakeEnum.entries.map { it.name to stringResource(it.labelResId) }

    LaunchedEffect(pillFromDb) {
        pillFromDb?.let { pill ->
            pillName = pill.name
            pillDose = pill.dose
            takenForm = pill.form
            takenInstruction = pill.intakeInstructions ?: ""
            isPermanent = pill.isPermanent
            intakeDuration = pill.courseDurationDays?.toString() ?: ""
            numberOfIntakes = pill.numberOfIntakes.toString()
            notes = pill.notes ?: ""

            scheduleTimes.clear()
            scheduleTimes.addAll(pill.scheduleTime)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_pill)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = pillName,
                onValueChange = { pillName = it },
                label = { Text(stringResource(R.string.pill_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = isFormDropdownExpanded,
                onExpandedChange = { isFormDropdownExpanded = !isFormDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = takenForm,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.form_field)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isFormDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = isFormDropdownExpanded,
                    onDismissRequest = { isFormDropdownExpanded = false }
                ) {
                    formItems.forEach { (nameHere, resId) ->
                        val label = resId
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                if (nameHere == formItems.last().first) {
                                    showCustomFormDialog = true
                                } else {
                                    takenForm = label
                                }
                                isFormDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            OutlinedTextField(
                value = pillDose,
                onValueChange = { pillDose = it },
                label = { Text(stringResource(R.string.dose)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isPermanent, onCheckedChange = { isPermanent = it })
                Text(stringResource(R.string.permanent_course))
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = intakeDuration,
                    onValueChange = { intakeDuration = it },
                    label = { Text(stringResource(R.string.intake_duration)) },
                    modifier = Modifier.weight(1f),
                    enabled = !isPermanent,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = numberOfIntakes,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.intakes)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    intakeOptions.forEach { howMany ->
                        DropdownMenuItem(
                            text = { Text(howMany) },
                            onClick = {
                                numberOfIntakes = howMany
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.time_remind),
                    modifier = Modifier.padding(end = 8.dp)
                )
                OutlinedButton(
                    onClick = {
                        showTimePicker = true
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_time))
                }
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scheduleTimes.forEach { time ->
                    InputChip(
                        selected = true,
                        onClick = {  },
                        label = { Text(time) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.delete),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { scheduleTimes.remove(time) }
                            )
                        }
                    )
                }
            }

            ExposedDropdownMenuBox(
                expanded = isInstructionDropdownExpanded,
                onExpandedChange = { isInstructionDropdownExpanded = !isInstructionDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    readOnly = true,
                    value = takenInstruction,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.instruction)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isInstructionDropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = isInstructionDropdownExpanded,
                    onDismissRequest = { isInstructionDropdownExpanded = false }
                ) {
                    instructionItems.forEach { (nameHereTwo, labelResId) ->
                        val label = labelResId
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                if (nameHereTwo == instructionItems.last().first) {//
                                    showCustomIntakeDialog = true
                                } else {
                                    takenInstruction = label
                                }
                                isInstructionDropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    PlaySound.playClickSound(context)
                    pillFromDb?.let { currentPill ->
                        val updatedPill = currentPill.copy(
                            name = pillName,
                            dose = pillDose,
                            form = takenForm,
                            intakeInstructions = takenInstruction,
                            isPermanent = isPermanent,
                            courseDurationDays = if (isPermanent) null else intakeDuration.toIntOrNull(),
                            scheduleTime = scheduleTimes.toList(),
                            notes = notes,
                            numberOfIntakes = numberOfIntakes.toIntOrNull() ?: 1
                        )
                        viewModel.updatePill(updatedPill)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = pillName.isNotBlank() && pillDose.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cancel))
            }

                Button(
                    onClick = {
                        PlaySound.playClickSound(context)
                        showDeleteConfirmation = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { h, m ->
                val formatted = String.format("%02d:%02d", h, m)
                if (!scheduleTimes.contains(formatted)) {
                    scheduleTimes.add(formatted)
                    scheduleTimes.sort()
                }
            }
        )
    }

    if (showCustomFormDialog) {
        var tempInputForm by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showCustomFormDialog = false
            },
            title = { Text(stringResource(R.string.enter_custom_form)) },
            text = {
                OutlinedTextField(
                    value = tempInputForm,
                    onValueChange = { tempInputForm = it },
                    label = { Text(stringResource(R.string.custom_form)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        if (tempInputForm.isNotBlank()) {
                            takenForm = tempInputForm
                            showCustomFormDialog = false
                        }

                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCustomFormDialog = false
                        takenForm=formItems[0].second
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (showCustomIntakeDialog) {
        var tempInputInstruction by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = {
                showCustomIntakeDialog = false
            },
            title = { Text(stringResource(R.string.enter_custom_instructions)) },
            text = {
                OutlinedTextField(
                    value = tempInputInstruction,
                    onValueChange = { tempInputInstruction = it },
                    label = { Text(stringResource(R.string.custom_instructions)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        if (tempInputInstruction.isNotBlank()) {
                            takenInstruction = tempInputInstruction
                            showCustomIntakeDialog = false
                        }

                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCustomIntakeDialog = false
                        takenInstruction=instructionItems[0].second
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(text = stringResource(R.string.delete_pill))
            },
            text = {
                Text(text = stringResource(R.string.are_you_sure))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        pillFromDb?.let {
                            viewModel.deletePill(it)
                            navController.navigate("meds") {
                                popUpTo("meds") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.delete), color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

}
