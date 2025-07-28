package com.example.dicerollinganimation

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

    LaunchedEffect(isRolling) {
        if (isRolling) {

            targetColor = Color(
                red = Random.nextFloat(),
                green = Random.nextFloat(),
                blue = Random.nextFloat(),
                alpha = 1f
            )

            val totalRolls = 1
            for (i in 1..totalRolls) {
                diceValue = (1..6).random()

                scale.animateTo(1.3f, tween(80, easing = FastOutSlowInEasing))
                scale.animateTo(1f, tween(80, easing = LinearOutSlowInEasing))
                rotation.animateTo(rotation.value + 45f, tween(80))
                delay(50)
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
                rotation = rotation.value,
                backgroundColor = animatedColor
            )

            Spacer(modifier = Modifier.height(44.dp))

            Text(
                text = if (isRolling) "Rolling..." else "You rolled a $diceValue!",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DiceFace(value: Int, scale: Float, rotation: Float, backgroundColor: Color) {
    val dotSize = 12.dp
    val spacing = 24.dp

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation % 360
            }
            .size(120.dp)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
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
            modifier = Modifier.size(200.dp)
        )
    }
}
