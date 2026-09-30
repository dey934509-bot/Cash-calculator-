package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.IndianNumberToWords
import com.example.ui.components.ThreeDButton
import com.example.ui.components.ThreeDButtonVariant
import com.example.ui.components.ThreeDCard
import com.example.ui.components.ThreeDDepth
import com.example.ui.theme.Local3DThemeColors
import com.example.viewmodel.CashViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCashScannerSheet(
    viewModel: CashViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val themeColors = Local3DThemeColors.current
    val depth by viewModel.selectedDepth.collectAsStateWithLifecycle()
    val isScanning by viewModel.isAiScanning.collectAsStateWithLifecycle()
    val scanResult by viewModel.aiScanResult.collectAsStateWithLifecycle()
    val scanError by viewModel.aiScanError.collectAsStateWithLifecycle()

    var lastBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            lastBitmap = bitmap
            viewModel.scanCashImage(bitmap)
        }
    }

    // Photo picker launcher (zero-permission Google Play compliant)
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    lastBitmap = bitmap
                    viewModel.scanCashImage(bitmap)
                }
            } catch (_: Exception) {}
        }
    }

    // Scanning animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanPulse")
    val scanLineOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanOffset"
    )

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.clearAiScanResult()
            onDismiss()
        },
        containerColor = themeColors.surface,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(themeColors.accent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = themeColors.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI Cash Scanner",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Multimodal Indian Currency Vision",
                            fontSize = 11.sp,
                            color = Color.LightGray.copy(alpha = 0.7f)
                        )
                    }
                }

                IconButton(onClick = {
                    viewModel.clearAiScanResult()
                    onDismiss()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scanner Action Buttons
            if (!isScanning && scanResult == null) {
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = depth,
                    cornerRadius = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Place notes flat under camera or upload a picture",
                            fontSize = 13.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ThreeDButton(
                                onClick = { cameraLauncher.launch(null) },
                                modifier = Modifier.weight(1f),
                                variant = ThreeDButtonVariant.GOLD
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }

                            ThreeDButton(
                                onClick = {
                                    pickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                variant = ThreeDButtonVariant.ACCENT
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Demo / Simulation button for instant test
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(themeColors.surfaceVariant)
                                .border(1.dp, themeColors.highlightBorder.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable {
                                    // Generate a quick sample bitmap
                                    val bmp = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888)
                                    val canvas = Canvas(bmp)
                                    val paint = Paint().apply { color = android.graphics.Color.DKGRAY }
                                    canvas.drawRect(0f, 0f, 400f, 300f, paint)
                                    lastBitmap = bmp
                                    viewModel.scanCashImage(bmp)
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = themeColors.accent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Try Sample Instant Scan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = themeColors.accent)
                            }
                        }
                    }
                }
            }

            // Scanning state
            if (isScanning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .border(1.5.dp, themeColors.accent, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = themeColors.accent,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Gemini AI Analyzing Banknotes...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Detecting ₹2000, ₹500, ₹200, ₹100, ₹50, ₹20, ₹10 & coins",
                            fontSize = 11.sp,
                            color = themeColors.accent,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Results Card
            scanResult?.let { detected ->
                Spacer(modifier = Modifier.height(8.dp))
                ThreeDCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = depth,
                    cornerRadius = 16.dp,
                    borderColor = themeColors.accent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DETECTION SUCCESSFUL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                            if (detected.isSimulation) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE65100).copy(alpha = 0.3f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Demo Model", fontSize = 10.sp, color = Color(0xFFFFB74D))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = detected.summary,
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Breakdown tags
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            detected.counts.filter { it.value > 0 }.forEach { (denomKey, count) ->
                                val label = if (denomKey.startsWith("coin_")) "₹${denomKey.removePrefix("coin_")} Coin" else "₹$denomKey Note"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(label, fontSize = 12.sp, color = Color.LightGray)
                                    Text("$count pcs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeColors.accent)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.15f)))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Scanned Amount:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                            Text(
                                IndianNumberToWords.formatIndianCurrency(detected.totalAmount),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = themeColors.accent
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Buttons: Apply or Scan Again
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ThreeDButton(
                                onClick = {
                                    viewModel.clearAiScanResult()
                                },
                                modifier = Modifier.weight(1f),
                                variant = ThreeDButtonVariant.SURFACE
                            ) {
                                Text("Rescan", color = Color.LightGray, fontSize = 13.sp)
                            }

                            ThreeDButton(
                                onClick = {
                                    viewModel.applyAiScanToCounter(detected)
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1.5f),
                                variant = ThreeDButtonVariant.GOLD
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to Counter", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            // Error display
            scanError?.let { err ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFC62828).copy(alpha = 0.3f))
                        .padding(12.dp)
                ) {
                    Text(text = err, fontSize = 12.sp, color = Color(0xFFEF9A9A))
                }
            }
        }
    }
}
