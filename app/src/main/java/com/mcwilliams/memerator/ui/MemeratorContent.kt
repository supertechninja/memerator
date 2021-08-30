package com.mcwilliams.memerator.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequesterModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.mcwilliams.memerator.R
import com.mcwilliams.memerator.ui.theme.MemeratorTheme
import com.muddzdev.quickshot.QuickShot
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

enum class TopAppBarType { Search, Default }

@ExperimentalUnitApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun StravaDashboard(viewModel: MemeratorViewModel, paddingValues: PaddingValues) {
    val imageList by viewModel.images.observeAsState(emptyList())
    var memeImageToSave: ComposeView? = null
    var context: Context? = null
    var updateMemeImage: (ComposeView, Context) -> Unit = { composeView, contextView ->
        memeImageToSave = composeView
        context = contextView
    }

    var topAppBar by rememberSaveable { mutableStateOf(TopAppBarType.Default) }

    var memeSearchField by remember { mutableStateOf(TextFieldValue("")) }

    MemeratorTheme() {
        Scaffold(topBar = {
            when (topAppBar) {
                TopAppBarType.Search -> {
                    TopAppBar() {
                        IconButton(onClick = {
                            topAppBar = TopAppBarType.Default
                        }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                        }

                        TextField(
                            value = memeSearchField,
                            onValueChange = {
                                memeSearchField = it
                            },
                            placeholder = { Text(text = "Search Meme's") },
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = { memeSearchField = TextFieldValue("") }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "")
                                }
                            }, colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                backgroundColor = Color.Transparent
                            )
                        )
                    }
                }
                TopAppBarType.Default -> {
                    TopAppBar(title = {
                        Text("Memerator")
                    }, actions = {
                        IconButton(onClick = {
                            topAppBar = TopAppBarType.Search
                        }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }

                        IconButton(onClick = {
                            memeImageToSave?.let {
                                Log.d("TAG", "StravaDashboard: $it")
                                share(it, "Meme", context = context!!)
                            }

                        }) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null)
                        }
                    })
                }
            }

        }) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            ) {
                var selectedMemeImage by remember { mutableStateOf("") }

                var topText by remember { mutableStateOf("") }
                var bottomText by remember { mutableStateOf("") }
                var textInputType by remember { mutableStateOf(TextType.NONE) }

                var textSize by remember { mutableStateOf(22f) }

                var filteredList by remember { mutableStateOf(mutableListOf<String>()) }

                if (selectedMemeImage.isNotEmpty()) {
                    MemeImageView(updateMemeImage = updateMemeImage) {
                        Box() {
                            if (selectedMemeImage.isNotEmpty()) {
                                Image(
                                    painter = rememberImagePainter(data = selectedMemeImage.buildMemeImageUrl()),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    contentScale = ContentScale.FillWidth
                                )
                            }

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
                                        },
                                        fontSize = TextUnit(textSize, TextUnitType.Sp)
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
                                        },
                                        fontSize = TextUnit(textSize, TextUnitType.Sp)
                                    )
                                }
                            }
                        }
                    }
                }

                filteredList =
                    imageList.filter { it.contains(memeSearchField.text) }.toMutableList()

                val listToLoad =
                    if (memeSearchField.text.isEmpty() || filteredList.isEmpty()) imageList else filteredList

                LazyVerticalGrid(cells = GridCells.Fixed(3)) {
                    itemsIndexed(listToLoad) { index, item ->

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Image(
                                painter = rememberImagePainter(data = item.buildMemeImageUrl()),
                                contentDescription = "",
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .border(
                                        width = if (item == selectedMemeImage) 4.dp else 0.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (item == selectedMemeImage) {
                                            selectedMemeImage = ""
                                        } else {
                                            selectedMemeImage = item
                                        }
                                    },
                                contentScale = ContentScale.Crop
                            )

                            Text(text = item.replace("-", " "), textAlign = TextAlign.Center)
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

                            Slider(
                                value = textSize,
                                onValueChange = { textSize = it },
                                valueRange = 14f..28f,
                                steps = 5
                            )

                            Text(text = "Text Size: $textSize")

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = { /*TODO*/ }) {
                                    Text(text = "CANCEL")
                                }
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
fun MemeImageView(
    updateMemeImage: (ComposeView, Context) -> Unit,
    content: @Composable () -> Unit
) {
    AndroidView(factory = { context ->
        val androidView =
            LayoutInflater.from(context)
                .inflate(R.layout.compose_view, null)


        val composeView = androidView.findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            content()
        }

        updateMemeImage(composeView, context)
        return@AndroidView androidView
    })
}

private fun share(view: ComposeView, name: String, context: Context) {
    val fileName = "$name-${LocalDate.now()}-${LocalTime.now().hour}-${LocalTime.now().minute}"
    QuickShot.of(view).setResultListener(HandleSavedImage(context, fileName))
        .setFilename(fileName)
        .setPath("Memerator")
        .toJPG()
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
