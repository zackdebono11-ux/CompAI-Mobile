
package com.example.compaimobile
import com.example.compaishared.SharedEngine
import androidx.compose.animation.core.Animatable
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.os.BatteryManager
import android.os.Build
import android.content.ActivityNotFoundException
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.os.StatFs
import android.text.format.Formatter
import androidx.compose.ui.platform.LocalContext
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compaimobile.ui.theme.CompAIMobileTheme

private val Navy = Color(0xFF080D1B)
private val Panel = Color(0xFF111A2D)
private val Blue = Color(0xFF398BFF)
private val Cyan = Color(0xFF42D9FF)
private val Purple = Color(0xFF875CFF)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CompAIMobileTheme {
                CompAIApp()
            }
        }
    }
}


        
    


data class ChatMessage(
    val message: String,
    val isUser: Boolean
)

@Composable
fun CompAIApp() {
    var command by remember { mutableStateOf("") }
    val context = LocalContext.current
    val speechLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->

    if (result.resultCode == android.app.Activity.RESULT_OK) {

        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.firstOrNull()

        if (!spokenText.isNullOrBlank()) {
            command = spokenText
        }
    }
}

    val messages = remember {
        mutableStateListOf(
            ChatMessage(
                "Hello! I'm CompAI Mobile.",
                false
            ),
            ChatMessage(
                "Your mobile AI assistant is getting ready. What would you like to do?",
                false
            )
        )
    }

    val background = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF101B35),
            Navy,
            Color(0xFF080B16)
        )
    )
        // STARTUP INTRO ANIMATION

    var showIntro by remember { mutableStateOf(true) }
    val introAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        introAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700)
        )

        delay(1200)

        introAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(650)
        )

        showIntro = false
    }

    Box(modifier = Modifier.fillMaxSize()) {

    Scaffold(
        containerColor = Navy,
        modifier = Modifier.fillMaxSize()
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .padding(padding)
                .padding(horizontal = 18.dp)
        ) {

            // HEADER

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "COMPAI",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Text(
                        text = "MOBILE v0.18.0",
                        color = Cyan,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp
                    )
                }

                Surface(
                    color = Color(0xFF123B36),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF45E6A0))
                        )

                        Spacer(Modifier.width(7.dp))

                        Text(
                            "READY",
                            color = Color(0xFF75F0B5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ANIMATED ORB

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(235.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedCompAIOrb()
            }

            Text(
                text = "YOUR PERSONAL AI ASSISTANT",
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Text(
                text = "CompAI Mobile is ready for your commands.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, bottom = 15.dp),
                color = Color(0xFF8999B8),
                fontSize = 12.sp
            )

            // CHAT AREA

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0x9910192C))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { item ->
                    ChatBubble(item)
                }
            }

            // COMMAND INPUT

            // COMMAND INPUT

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "Enter a command...",
                            color = Color(0xFF8290AA)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            sendCommand(command, messages, context) {
                                command = ""
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = Color(0xFF34435F),
                        cursorColor = Cyan,
                        focusedContainerColor = Panel,
                        unfocusedContainerColor = Panel
                    )
                )

                Spacer(Modifier.width(6.dp))

                // MICROPHONE BUTTON
                Button(
                    onClick = {
                        val speechIntent = Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
                        ).apply {
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                            )
                            putExtra(
                                RecognizerIntent.EXTRA_LANGUAGE,
                                Locale.getDefault().toLanguageTag()
                            )
                            putExtra(
                                RecognizerIntent.EXTRA_PROMPT,
                                "Speak a CompAI command..."
                            )
                        }

                        try {
                            speechLauncher.launch(speechIntent)
                        } catch (_: ActivityNotFoundException) {
                            messages.add(
                                ChatMessage(
                                    "Speech recognition is not available on this device.",
                                    false
                                )
                            )
                        }
                    },
                    modifier = Modifier.height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple
                    )
                ) {
                    Text(
                        "MIC",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.width(6.dp))

                // SEND BUTTON
                Button(
                    onClick = {
                        sendCommand(command, messages, context) {
                            command = ""
                        }
                    },
                    
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue
                    )
                ) {
                    Text(
                        "SEND",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

            }
        }
    } // CLOSE SCAFFOLD

    // COMPAI STARTUP OVERLAY
    if (showIntro) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(background)
                .graphicsLayer {
                    alpha = introAlpha.value

                    val scale = 0.9f + (introAlpha.value * 0.1f)
                    scaleX = scale
                    scaleY = scale
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedCompAIOrb()

                Spacer(modifier = Modifier.height(25.dp))

                Text(
                    text = "COMPAI",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 6.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "MOBILE AI ASSISTANT",
                    color = Cyan,
                    fontSize = 12.sp,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "INITIALISING...",
                    color = Color(0xFF8999B8),
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )
            }
        }
    }
} // CLOSE OUTER BOX
} // CLOSE COMPAIAPP



                


    


fun sendCommand(
    command: String,
    messages: MutableList<ChatMessage>,
    context: Context,
    clearInput: () -> Unit
) {
    val text = command.trim()

    if (text.isEmpty()) return

    messages.add(ChatMessage(text, true))

    val response = processMobileCommand(text, messages, context)

    if (response.isNotEmpty()) {
        messages.add(ChatMessage(response, false))
    }

    clearInput()
}
fun processMobileCommand(
    command: String,
    messages: MutableList<ChatMessage>,
    context: Context
): String {
    val sharedEngine = SharedEngine()
val lower = sharedEngine.normalizeCommand(command)

// Get platform-specific date and time.
val currentTime = LocalTime.now().format(
    DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
)

val currentDate = LocalDate.now().format(
    DateTimeFormatter.ofPattern(
        "EEEE, d MMMM yyyy",
        Locale.getDefault()
    )
)

// Process shared commands.
val sharedResponse = sharedEngine.processNormalizedCommand(
    lower,
    currentTime,
    currentDate
)

if (sharedResponse != null) {
    return sharedResponse
}


return when {

          
       
        // BATTERY

        lower == "battery" || lower == "battery status" -> {

            val batteryIntent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )

            if (batteryIntent == null) {
                "I couldn't retrieve battery information."
            } else {
                val level = batteryIntent.getIntExtra(
                    BatteryManager.EXTRA_LEVEL, -1
                )

                val scale = batteryIntent.getIntExtra(
                    BatteryManager.EXTRA_SCALE, -1
                )

                val percentage = if (level >= 0 && scale > 0) {
                    level * 100 / scale
                } else {
                    -1
                }

                val status = batteryIntent.getIntExtra(
                    BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN
                )

                val charging = when (status) {
                    BatteryManager.BATTERY_STATUS_CHARGING ->
                        "Charging"
                    BatteryManager.BATTERY_STATUS_FULL ->
                        "Fully charged"
                    else -> "Not charging"
                }

                "BATTERY INFORMATION\n\nCharge: ${
                    if (percentage >= 0) "$percentage%" else "Unavailable"
                }\nStatus: $charging"
            }
        }

        // STORAGE

        lower == "storage" ||
        lower == "check storage" -> {

            val stat = StatFs(context.filesDir.absolutePath)

            val total = stat.totalBytes
            val available = stat.availableBytes
            val used = total - available

            """
            DEVICE STORAGE

            Total: ${Formatter.formatFileSize(context, total)}
            Used: ${Formatter.formatFileSize(context, used)}
            Available: ${Formatter.formatFileSize(context, available)}
            """.trimIndent()
        }

        // DEVICE INFORMATION

        lower == "device info" ||
        lower == "phone information" ||
        lower == "device information" -> {

            """
            DEVICE INFORMATION

            Manufacturer: ${Build.MANUFACTURER}
            Model: ${Build.MODEL}
            Android version: ${Build.VERSION.RELEASE}
            SDK version: ${Build.VERSION.SDK_INT}
            Device: ${Build.DEVICE}
            """.trimIndent()
        }

        // OPEN SETTINGS

        lower == "open settings" ||
        lower == "settings" -> {

            try {
                context.startActivity(
                    Intent(Settings.ACTION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
                "Opening Android Settings."
            } catch (e: Exception) {
                "I couldn't open Android Settings: ${e.message}"
            }
        }

        // OPEN CALCULATOR

        lower == "open calculator" ||
        lower == "calculator" -> {

            val packages = listOf(
                "com.sec.android.app.popupcalculator",
                "com.google.android.calculator"
            )

            var launched = false

            for (packageName in packages) {
                val intent = context.packageManager
                    .getLaunchIntentForPackage(packageName)

                if (intent != null) {
                    try {
                        context.startActivity(intent)
                        launched = true
                        break
                    } catch (_: Exception) {
                        // Try the next calculator.
                    }
                }
            }

            if (launched) {
                "Opening Calculator."
            } else {
                "I couldn't find a supported Calculator app."
            }
        }

        // OPEN OTHER INSTALLED APPS

lower.startsWith("open ") ||
lower.startsWith("launch ") ||
lower.startsWith("start ") ||
lower.startsWith("run ") ||
lower.startsWith("use ") -> {

    val appName = lower.replaceFirst(
        Regex("^(open|launch|start|run|use)\\s+"),
        ""
    ).trim()

    if (appName.isEmpty()) {
        "Please tell me which app to open."
    } else {

        try {
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val apps = context.packageManager
                .queryIntentActivities(launcherIntent, 0)

            val matchingApp = apps.firstOrNull {
                it.loadLabel(context.packageManager)
                    .toString()
                    .lowercase(Locale.getDefault())
                    .contains(appName)
            }

            if (matchingApp != null) {

                val packageName =
                    matchingApp.activityInfo.packageName

                val launchIntent = context.packageManager
                    .getLaunchIntentForPackage(packageName)

                if (launchIntent != null) {
                    context.startActivity(launchIntent)

                    "Opening ${
                        matchingApp.loadLabel(context.packageManager)
                    }."
                } else {
                    "I found the app, but couldn't launch it."
                }

            } else {
                "I couldn't find an installed app matching \"$appName\"."
            }

        } catch (e: Exception) {
            "There was a problem opening the app: ${e.message}"
        }
    }
}

        // CLEAR

        lower == "clear" || lower == "clear chat" -> {
            messages.clear()
            messages.add(
                ChatMessage("Hello! I'm CompAI Mobile v0.18.0.", false)
            )
            "Conversation cleared!"
        }

       
        // STATUS

        lower == "status" -> """
            COMPAI MOBILE STATUS

            Interface: Connected
            Command engine: Online
            Android integration: Available
            Battery information: Available
            Storage information: Available
            Device information: Available
        """.trimIndent()

       
        // UNKNOWN COMMAND

        else -> """
            I don't understand that command yet.

            You entered: "$command"

            Type "help" to see my available commands.
        """.trimIndent()
    }
}



@Composable
fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        Surface(
            color = if (message.isUser) {
                Color(0xFF174B91)
            } else {
                Color(0xFF1B2942)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = message.message,
                modifier = Modifier.padding(13.dp),
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
fun AnimatedCompAIOrb() {
    val transition = rememberInfiniteTransition(
        label = "orb animation"
    )

    val pulse by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb pulse"
    )

    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb rotation"
    )

    Canvas(
        modifier = Modifier.size(215.dp)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension * 0.31f

        // Outer glow

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Blue.copy(alpha = 0.35f * pulse),
                    Purple.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = androidx.compose.ui.geometry.Offset(
                    centerX,
                    centerY
                ),
                radius = radius * 2.1f
            ),
            radius = radius * 2.1f,
            center = androidx.compose.ui.geometry.Offset(
                centerX,
                centerY
            )
        )

        // Main orb

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF77E9FF),
                    Blue,
                    Color(0xFF252D86),
                    Color(0xFF10162F)
                ),
                center = androidx.compose.ui.geometry.Offset(
                    centerX - radius * 0.3f,
                    centerY - radius * 0.3f
                ),
                radius = radius * 1.7f
            ),
            radius = radius * pulse,
            center = androidx.compose.ui.geometry.Offset(
                centerX,
                centerY
            )
        )

        // Rotating energy rings

        drawArc(
            color = Cyan.copy(alpha = 0.9f),
            startAngle = rotation,
            sweepAngle = 245f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(
                centerX - radius * 1.45f,
                centerY - radius * 1.45f
            ),
            size = androidx.compose.ui.geometry.Size(
                radius * 2.9f,
                radius * 2.9f
            ),
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawArc(
            color = Purple.copy(alpha = 0.9f),
            startAngle = -rotation * 0.7f,
            sweepAngle = 190f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(
                centerX - radius * 1.7f,
                centerY - radius * 1.7f
            ),
            size = androidx.compose.ui.geometry.Size(
                radius * 3.4f,
                radius * 3.4f
            ),
            style = Stroke(
                width = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}
