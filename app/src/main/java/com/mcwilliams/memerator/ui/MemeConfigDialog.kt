package com.mcwilliams.memerator.ui

import ColorPicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mcwilliams.memerator.ui.utils.rememberColorState

@ExperimentalAnimationApi
@Composable
fun MemeConfigDialog(
    topText: String,
    bottomText: String,
    textSize : Float,
    textColor: Color,
    focusRequester: FocusRequester,
    dismissDialog: () -> Unit,
    saveChange: (String, String, Float, Color) -> Unit
) {
    var currentTextSize by remember { mutableStateOf(textSize) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var currentTextColor by remember { mutableStateOf(textColor)}

    val textColorState = rememberColorState(color = Color.Black, updateColor = {
        currentTextColor = it
        showColorPickerDialog = false
    })

    Dialog(onDismissRequest = dismissDialog) {
        var topTextInput by remember { mutableStateOf(TextFieldValue(topText)) }

        var bottomTextInput by remember { mutableStateOf(TextFieldValue(bottomText)) }

        Column(
            modifier = Modifier
                .background(Color.DarkGray)
                .padding(16.dp)
        ) {
            Text(
                text = "Meme Options",
                color = Color.White,
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = topTextInput,
                onValueChange = {
                    topTextInput = it
                }, modifier = Modifier
                    .padding(vertical = 16.dp)
                    .focusRequester(focusRequester),
                label = {
                    Text(
                        text = "Top Text",
                        color = Color.White
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.clickable {
                            topTextInput = TextFieldValue("")
                        })
                },
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = bottomTextInput,
                onValueChange = {
                    bottomTextInput = it
                }, modifier = Modifier
                    .padding(vertical = 16.dp)
                    .focusRequester(focusRequester),
                label = {
                    Text(
                        text = "Bottom Text",
                        color = Color.White
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.clickable {
                            bottomTextInput = TextFieldValue("")
                        })
                },
                maxLines = 1
            )

            Slider(
                value = currentTextSize,
                onValueChange = { currentTextSize = it },
                valueRange = 14f..42f,
                steps = 10
            )

            Text(text = "Text Size: ${currentTextSize.toInt()}")

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Text Color", modifier = Modifier.padding(end = 8.dp))

                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            showColorPickerDialog = true
                        },
                    color = currentTextColor
                ) {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = dismissDialog) {
                    Text(text = "CANCEL")
                }
                TextButton(onClick = {
                    saveChange(topTextInput.text, bottomTextInput.text, currentTextSize, currentTextColor)
                }) {
                    Text(text = "SAVE")
                }
            }
        }

        AnimatedVisibility(showColorPickerDialog) {
            ColorPicker(textColorState)
        }
    }
}