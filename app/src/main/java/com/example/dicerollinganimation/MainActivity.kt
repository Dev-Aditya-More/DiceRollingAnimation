package com.example.dicerollinganimation

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.dicerollinganimation.ui.theme.DiceRollingAnimationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceRollingAnimationTheme {

                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2500)
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    DiceRoller()
                }
            }
        }
    }
}
@Composable
fun DiceRoller(modifier: Modifier = Modifier) {

    var diceValue by remember{mutableIntStateOf(1)}
    var isRolling by remember{mutableStateOf(false)}

    val scale = remember{ Animatable(1f) }

    val rotation = remember { Animatable(0f) }

    var targetColor by remember { mutableStateOf(Color.White) }
    val animatedColor by animateColorAsState(targetColor, animationSpec = tween(300))

    val context = LocalContext.current
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.dice_roll)
    }
    val rotationX = remember { Animatable(0f) }
    val rotationY = remember { Animatable(0f) }

    LaunchedEffect(isRolling) {
        if (isRolling) {
            mediaPlayer.start()

            targetColor = Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1f
            )

            repeat(1) {
                diceValue = (1..6).random()

                val randX = Random.nextInt(-80, 80).toFloat()
                val randY = Random.nextInt(-80, 80).toFloat()

                launch {
                    scale.animateTo(1.3f, tween(80))
                    scale.animateTo(1f, tween(80))
                }

                launch {
                    rotation.animateTo(rotation.value + Random.nextInt(120, 240), tween(150))
                    rotationX.animateTo(randX, tween(150))
                    rotationY.animateTo(randY, tween(150))

                }

                delay(50)
            }

            // Reset 3D tilt after roll
            launch {
                rotationX.animateTo(0f, tween(300, easing = LinearOutSlowInEasing))
                rotationY.animateTo(0f, tween(300, easing = LinearOutSlowInEasing))
            }

            isRolling = false
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(enabled = !isRolling)
            {
                isRolling = true
            }
            .wrapContentSize(Alignment.Center)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Dice face drawn as dots
            DiceFace(
                value = diceValue,
                scale = scale.value,
                rotationZ = rotation.value,
                rotationX = rotationX.value,
                rotationY = rotationY.value,
                backgroundColor = animatedColor
            )


            Spacer(modifier = Modifier.height(94.dp))

            Text(
                text = if (isRolling) "Rolling..." else "You rolled a $diceValue!",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DiceFace(
    value: Int,
    scale: Float,
    rotationZ: Float,
    rotationX: Float,
    rotationY: Float,
    backgroundColor: Color
) {
    val dotSize = 12.dp
    val spacing = 24.dp

    Box(
        modifier = Modifier
            .graphicsLayer {
                val normalizedZ = rotationZ % 360

                scaleX = scale
                scaleY = scale
                this.rotationZ = normalizedZ
                this.rotationX = rotationX
                this.rotationY = rotationY
                cameraDistance = 32 * density
                shadowElevation = 16f
                shape = RoundedCornerShape(16.dp)
                clip = true

            }
            .size(120.dp)
            .background(backgroundColor)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(0.3f, 0.3f),
                        radius = Float.POSITIVE_INFINITY
                    )
                )
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = dotSize.toPx() / 2
            val w = size.width
            val h = size.height

            fun drawDot(x: Float, y: Float) {
                drawCircle(Color.Black, radius = radius, center = Offset(x, y))
            }

            val cx = w / 2
            val cy = h / 2
            val offset = spacing.toPx()

            when (value) {
                1 -> drawDot(cx, cy)
                2 -> {
                    drawDot(cx - offset, cy - offset)
                    drawDot(cx + offset, cy + offset)
                }
                3 -> {
                    drawDot(cx, cy)
                    drawDot(cx - offset, cy - offset)
                    drawDot(cx + offset, cy + offset)
                }
                4 -> {
                    drawDot(cx - offset, cy - offset)
                    drawDot(cx + offset, cy - offset)
                    drawDot(cx - offset, cy + offset)
                    drawDot(cx + offset, cy + offset)
                }
                5 -> {
                    drawDot(cx, cy)
                    drawDot(cx - offset, cy - offset)
                    drawDot(cx + offset, cy - offset)
                    drawDot(cx - offset, cy + offset)
                    drawDot(cx + offset, cy + offset)
                }
                6 -> {
                    drawDot(cx - offset, cy - offset)
                    drawDot(cx + offset, cy - offset)
                    drawDot(cx - offset, cy)
                    drawDot(cx + offset, cy)
                    drawDot(cx - offset, cy + offset)
                    drawDot(cx + offset, cy + offset)
                }
            }
        }
    }
}


@Composable
fun SplashScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottiesDice.json"))
    val progress by animateLottieCompositionAsState(composition)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF262626)), // matching Lottie background
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = Modifier.size(300.dp)
        )
    }
}
