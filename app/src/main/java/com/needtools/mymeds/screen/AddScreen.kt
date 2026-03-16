package com.needtools.mymeds.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.needtools.mymeds.R
import com.needtools.mymeds.db.Pill
import com.needtools.mymeds.util.IntakeEnum
import com.needtools.mymeds.util.PillFormEnum
import com.needtools.mymeds.util.TimePickerDialog
import com.needtools.mymeds.util.PlaySound
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddScreen(
    onPillSaved: (Pill) -> Unit,
    onBack: () -> Unit
) {

    val context = LocalContext.current
    var pillName by remember { mutableStateOf("") }
    var isFormDropdownExpanded by remember { mutableStateOf(false) }
    var isInstructionDropdownExpanded by remember { mutableStateOf(false) }
    var showCustomFormDialog by remember { mutableStateOf(false) }
    var pillDose by remember { mutableStateOf("") }
    var isPermanent by remember { mutableStateOf(false) }
    var intakeDuration by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var numberOfIntakes by remember { mutableStateOf("1") }
    val intakeOptions = (1..6).toList().map { it.toString() }
    val scheduleTimes = remember { mutableStateListOf<String>() }
    var showTimePicker by remember { mutableStateOf(false) }

    var showCustomIntakeDialog by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val formItems:List<Pair<String, String>> = PillFormEnum.entries.map {
        it.name to stringResource(it.resId)
    }
    var takenForm by remember { mutableStateOf(formItems[0].second) }

    val instructionItems:List<Pair<String, String>> = IntakeEnum.entries.map {
        it.name to stringResource(it.labelResId)
    }
    var takenInstruction by remember { mutableStateOf(instructionItems[0].second) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = pillName,
                onValueChange = { pillName = it },
                label = { Text(stringResource(R.string.pill_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){

                Checkbox(
                    checked = isPermanent,
                    onCheckedChange = { isPermanent = it }
                )
                Text(stringResource(R.string.permanent_course))
                OutlinedTextField(
                    value = intakeDuration,
                    onValueChange = { intakeDuration = it },
                    label = { Text(stringResource(R.string.intake_duration)) },
                    modifier = Modifier.weight(1f),
                    enabled = !isPermanent,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
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
                        onClick = { },
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
                             if (nameHereTwo == instructionItems.last().first) {
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
                    scope.launch {
                        val newPill = Pill(
                            name = pillName,
                            dose = pillDose,
                            numberOfIntakes = numberOfIntakes.toIntOrNull() ?: 1,
                            scheduleTime = scheduleTimes.toList(),
                            intakeInstructions = takenInstruction,
                            form = takenForm,
                            isPermanent = isPermanent,
                            notes = notes,
                            courseDurationDays = if (isPermanent) null else intakeDuration.toIntOrNull() ?: 7,
                            numberInList = (System.currentTimeMillis() / 1000).toInt()
                        )

                        onPillSaved(newPill)
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
        }

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

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                val formattedTime = String.format("%02d:%02d", hour, minute)
                if (!scheduleTimes.contains(formattedTime)) {
                    scheduleTimes.add(formattedTime)
                    scheduleTimes.sort()
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

}

