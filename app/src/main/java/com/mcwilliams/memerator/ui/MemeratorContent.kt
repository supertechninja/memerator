package com.mcwilliams.memerator.ui

import ColorPicker
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.Window
import android.widget.Space
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
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
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

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
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
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

                    LaunchedEffect(key1 = Unit) {
                        focusRequester.requestFocus()
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
            ) {
                var selectedMemeImage by remember { mutableStateOf("") }

                var topText by remember { mutableStateOf("Tap to Edit") }
                var bottomText by remember { mutableStateOf("Tap to Edit") }
                var textInputType by remember { mutableStateOf(TextType.NONE) }

                var textSize by remember { mutableStateOf(22f) }

                var showColorPickerDialog by remember { mutableStateOf(false) }

                var textColor by remember { mutableStateOf(Color.Black) }
                val textColorState = rememberColorState(color = Color.Black, updateColor = {
                    textColor = it
                    showColorPickerDialog = false
                })

                var filteredList by remember { mutableStateOf(mutableListOf<String>()) }

                if (selectedMemeImage.isNotEmpty()) {
                    MemeImageView(updateMemeImage = updateMemeImage) {
                        BoxWithConstraints() {
                            val height = this.maxHeight / 2

                            if (selectedMemeImage.isNotEmpty()) {
                                Image(
                                    painter = rememberImagePainter(data = selectedMemeImage.buildMemeImageUrl()),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(height)
                                        .padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopCenter)
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = topText,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.h4,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.clickable {
                                        textInputType = TextType.TOP
                                    },
                                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                                    color = textColor
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 16.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = bottomText,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.h4,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.clickable {
                                        textInputType = TextType.BOTTOM
                                    },
                                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                                    color = textColor
                                )
                            }
                        }
                    }
                }

                filteredList = imageList.filter {
                    Log.d("TAG", "StravaDashboard: ${memeSearchField.text}")
                    it.contains(memeSearchField.text, ignoreCase = true)
                }.toMutableList()

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
                    Dialog(onDismissRequest = {
                        textInputType = TextType.NONE
                    }) {
                        var topTextInput by remember { mutableStateOf(TextFieldValue(topText)) }

                        var bottomTextInput by remember { mutableStateOf(TextFieldValue(bottomText)) }

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
                                value = textSize,
                                onValueChange = { textSize = it },
                                valueRange = 14f..42f,
                                steps = 10
                            )

                            Text(text = "Text Size: ${textSize.toInt()}")

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
                                    color = textColor
                                ) {}
                            }

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextButton(onClick = { textInputType = TextType.NONE }) {
                                    Text(text = "CANCEL")
                                }
                                TextButton(onClick = {
                                    topText = topTextInput.text
                                    bottomText = bottomTextInput.text
                                    textInputType = TextType.NONE
                                }) {
                                    Text(text = "SAVE")
                                }
                            }
                        }

                        AnimatedVisibility(showColorPickerDialog) {
                            ColorPicker(textColorState)
                        }
                    }

                    LaunchedEffect(key1 = Unit) {
                        focusRequester.requestFocus()
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
    view.toBitmap(
        onBitmapReady = { bitmap ->
            Log.d("TAG", "share: bitmap ready")
            val fileName =
                "$name-${LocalDate.now()}-${LocalTime.now().hour}-${LocalTime.now().minute}"
            QuickShot.of(bitmap, view.context)
                .setResultListener(HandleSavedImage(context, fileName))
                .setFilename(fileName)
                .setPath("Memerator")
                .toJPG()
                .save();
        },
        onBitmapError = {
            Log.d("TAG", it.localizedMessage)
        }
    )
}

class HandleSavedImage(val context: Context, val fileName: String) :
    QuickShot.QuickShotListener {
    override fun onQuickShotSuccess(path: String?) {
        Toast.makeText(context, "Meme Saved", Toast.LENGTH_SHORT).show()
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

// start of extension.
fun View.toBitmap(onBitmapReady: (Bitmap) -> Unit, onBitmapError: (Exception) -> Unit) {

    try {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val temporalBitmap =
                Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

            // Above Android O, use PixelCopy due
            // https://stackoverflow.com/questions/58314397/
            val window: Window = (this.context as Activity).window

            val location = IntArray(2)

            this.getLocationInWindow(location)

            val viewRectangle =
                Rect(location[0], location[1], location[0] + this.width, location[1] + this.height)

            val onPixelCopyListener: PixelCopy.OnPixelCopyFinishedListener =
                PixelCopy.OnPixelCopyFinishedListener { copyResult ->

                    if (copyResult == PixelCopy.SUCCESS) {

                        onBitmapReady(temporalBitmap)
                    } else {

                        error("Error while copying pixels, copy result: $copyResult")
                    }
                }

            PixelCopy.request(
                window, viewRectangle, temporalBitmap, onPixelCopyListener, Handler(
                    Looper.getMainLooper()
                )
            )
        } else {

            val temporalBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.RGB_565)

            val canvas = android.graphics.Canvas(temporalBitmap)

            this.draw(canvas)

            canvas.setBitmap(null)

            onBitmapReady(temporalBitmap)
        }

    } catch (exception: Exception) {

        onBitmapError(exception)
    }
}

class ColorState(
    val color: Color,
    val updateColor: (Color) -> Unit
)

@Composable
fun rememberColorState(
    color: Color = Color.Black, updateColor: (Color) -> Unit = {}
) = ColorState(
    color = color,
    updateColor = updateColor
)
