package com.mcwilliams.memerator.ui.dashboard

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.mcwilliams.memerator.R
import com.mcwilliams.memerator.ui.theme.MemeratorTheme
import com.muddzdev.quickshot.QuickShot
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun StravaDashboard(viewModel: MemeratorViewModel, paddingValues: PaddingValues) {
    val imageList by viewModel.images.observeAsState(emptyList())

    MemeratorTheme() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            var memeSearchField by remember { mutableStateOf(TextFieldValue("")) }
            var selectedMemeImage by remember { mutableStateOf("") }

            var topText by remember { mutableStateOf("") }
            var bottomText by remember { mutableStateOf("") }
            var textInputType by remember { mutableStateOf(TextType.NONE) }

            var filteredList by remember { mutableStateOf(mutableListOf<String>()) }

            if (selectedMemeImage.isNotEmpty()) {
                Box() {
                    Image(
                        painter = rememberImagePainter(data = selectedMemeImage.buildMemeImageUrl()),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        contentScale = ContentScale.FillWidth
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (topText.isEmpty()) {
                            Button(
                                onClick = {
                                    textInputType = TextType.TOP
                                },
                            ) {
                                Text(text = "Add Top Text", color = Color.White)
                            }
                        } else {
                            Text(
                                text = topText,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.clickable {
                                    textInputType = TextType.BOTTOM
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (bottomText.isEmpty()) {
                            Button(
                                onClick = {
                                    textInputType = TextType.BOTTOM
                                },
                                modifier = Modifier
                            ) {
                                Text(text = "Add Bottom Text", color = Color.White)
                            }
                        } else {
                            Text(
                                text = bottomText,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.clickable {
                                    textInputType = TextType.BOTTOM
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = memeSearchField,
                onValueChange = {
                    memeSearchField = it
                },
                label = { Text(text = "Search Meme's") },
                placeholder = { Text(text = "ex: Doge") },
                modifier = Modifier.padding(vertical = 32.dp),
                maxLines = 1
            )

            filteredList = imageList.filter { it.contains(memeSearchField.text) }.toMutableList()

            val listToLoad =
                if (memeSearchField.text.isEmpty() || filteredList.isEmpty()) imageList else filteredList

            LazyVerticalGrid(cells = GridCells.Fixed(3)) {
                items(listToLoad) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(data = it.buildMemeImageUrl()),
                            contentDescription = "",
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    if (it == selectedMemeImage) {
                                        selectedMemeImage = ""
                                    } else {
                                        selectedMemeImage = it
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )

                        Text(text = it.replace("-", " "), textAlign = TextAlign.Center)
                    }
                }
            }
            if (textInputType == TextType.TOP || textInputType == TextType.BOTTOM) {
                Dialog(onDismissRequest = { /*TODO*/ }) {
                    var textFieldValue by remember { mutableStateOf(TextFieldValue(if (textInputType == TextType.TOP) topText else bottomText)) }
                    Column(
                        modifier = Modifier
                            .background(Color.DarkGray)
                            .padding(16.dp)
                    ) {
                        val titleText = when (textInputType) {
                            TextType.TOP -> "Top Text"
                            TextType.BOTTOM -> "Bottom Text"
                            TextType.NONE -> ""
                        }
                        Text(
                            text = titleText,
                            color = Color.White
                        )
                        OutlinedTextField(value = textFieldValue, onValueChange = {
                            textFieldValue = it
                        }, modifier = Modifier.padding(vertical = 16.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = {
                                if (textInputType == TextType.TOP) {
                                    topText = textFieldValue.text
                                } else {
                                    bottomText = textFieldValue.text
                                }
                                textInputType = TextType.NONE
                            }) {
                                Text(text = "SAVE")
                            }
                            TextButton(onClick = { /*TODO*/ }) {
                                Text(text = "CANCEL")
                            }
                        }
                    }
                }
            }
        }


    }
}

enum class TextType { TOP, BOTTOM, NONE }


@Composable
fun StreakDashboardWidget(content: @Composable () -> Unit, widgetName: String) {
    AndroidView(factory = { context ->
        val androidView =
            LayoutInflater.from(context)
                .inflate(R.layout.compose_view, null)


        val composeView =
            androidView.findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            content()
        }

        val titleComposeView =
            androidView.findViewById<ComposeView>(R.id.title_compose_view)
        titleComposeView.setContent {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = widgetName,
                    style = MaterialTheme.typography.h6,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .padding(top = 4.dp, start = 16.dp)
                )

                val fileName = widgetName.replace(" ", "-")
                IconButton(
                    onClick = {
                        share(composeView, fileName, context = context)
                    },
                    modifier = Modifier.padding(end = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_save),
                        contentDescription = "Share"
                    )
                }
            }
        }

        return@AndroidView androidView
    })
}

private fun share(view: ComposeView, name: String, context: Context) {
    val fileName = "$name-${LocalDate.now()}-${LocalTime.now().hour}-${LocalTime.now().minute}"
    QuickShot.of(view).setResultListener(HandleSavedImage(context, fileName))
        .setFilename(fileName)
        .setPath("Streak")
        .toPNG()
//        .toJPG()
        .save();
}

class HandleSavedImage(val context: Context, val fileName: String) :
    QuickShot.QuickShotListener {
    override fun onQuickShotSuccess(path: String?) {
        Toast.makeText(context, "Dashboard Saved", Toast.LENGTH_SHORT).show()
    }

    override fun onQuickShotFailed(path: String?) {
        Toast.makeText(context, "Error Saving", Toast.LENGTH_SHORT).show()
    }
}

fun String.buildMemeImageUrl(): String {
    val imageName = this.replace(" ", "-")
    Log.d("TAG", "buildMemeImageUrl: $imageName")
    return "https://apimeme.com/meme?meme=$imageName&top=&bottom="
}
