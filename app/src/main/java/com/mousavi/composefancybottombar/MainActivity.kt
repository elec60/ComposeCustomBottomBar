package com.mousavi.composefancybottombar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mousavi.composefancybottombar.ui.theme.ComposeFancyBottomBarTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeFancyBottomBarTheme {
                // A surface container using the 'background' color from the theme
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    FancyBottomBar()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun FancyBottomBar(
    height: Dp = 80.dp,
    arcHeight: Dp = 20.dp,
    arcWidth: Dp = 30.dp,
    items: List<ImageVector> = listOf(
        ImageVector.vectorResource(id = R.drawable.ic_home),
        ImageVector.vectorResource(id = R.drawable.ic_location),
        ImageVector.vectorResource(id = R.drawable.ic_history),
        ImageVector.vectorResource(id = R.drawable.ic_settings),
    )
) {
    var firstTime by remember {
        mutableStateOf(true)
    }
    val count = items.size

    var selectedItemIndex by remember {
        mutableStateOf(0)
    }

    var width by remember {
        mutableStateOf(0)
    }

    var middlePos by remember {
        mutableStateOf(calcMiddlePos(selectedItemIndex, count, width.toFloat()))
    }

    val anim1 = animateFloatAsState(
        targetValue = middlePos,
        animationSpec = tween(durationMillis = if (firstTime) 0 else 500),
        finishedListener = { firstTime = false }
    )

    Box(
        modifier = Modifier
            .height(height = height)
            .fillMaxWidth()
            .onGloballyPositioned {
                width = it.size.width
            }
    ) {
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            val path = Path()
            middlePos = calcMiddlePos(selectedItemIndex, count, size.width)

            path.moveTo(0f, arcHeight.toPx())

            path.lineTo(size.width, arcHeight.toPx())

            val rect = Rect(
                topLeft = Offset(x = anim1.value - arcWidth.toPx(), y = 20f),
                bottomRight = Offset(
                    x = anim1.value + arcWidth.toPx(),
                    y = 2 * arcHeight.toPx() + 20f
                )
            )

            path.lineTo(x = size.width, size.height)
            path.lineTo(x = 0f, y = size.height)

            path.addArc(
                rect,
                180f,
                180f,
            )

            drawPath(path = path, color = Color(0xFF2142A2))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height - arcHeight)
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, imageVector ->
                Column(
                    modifier = Modifier
                        .clickable {
                            selectedItemIndex = index
                        }
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = if (selectedItemIndex == index) Color.White else Color.LightGray
                    )
                    AnimatedVisibility(
                        visible = selectedItemIndex == index,
                        enter = slideInVertically(
                            initialOffsetY = {
                                it / 2
                            },
                            animationSpec = tween(durationMillis = 500)
                        ),
                        exit = fadeOut(
                            targetAlpha = 1f,
                            animationSpec = tween(durationMillis = 500)
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 5.dp),
                            text = "Screen ${index + 1}",
                            color = Color.White,
                        )
                    }

                }

            }
        }
    }
}

private fun calcMiddlePos(
    selectedItemIndex: Int,
    count: Int,
    width: Float
) = when (selectedItemIndex) {
    0 -> {
        width / count / 2
    }
    count - 1 -> {
        width - width / count / 2
    }
    else -> {
        width / count / 2 + (width / count) * (selectedItemIndex)
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        FancyBottomBar()
    }
}