package com.amyartist.illusionaireapp.screens

import MessagePopup
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.amyartist.illusionaireapp.composables.HealthBar
import com.amyartist.illusionaireapp.composables.OverlayImage
import com.amyartist.illusionaireapp.data.ActionType
import com.amyartist.illusionaireapp.data.AvatarData
import com.amyartist.illusionaireapp.data.GameViewModel
import com.amyartist.illusionaireapp.data.InventoryData
import com.amyartist.illusionaireapp.data.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.graphics.scale
import com.amyartist.illusionaireapp.R
import com.amyartist.illusionaireapp.composables.imageButton

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameDisplay(
    gameViewModel: GameViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by gameViewModel.uiState.collectAsState()
    val equippedWeapon by InventoryData.equippedWeapon.collectAsState()
    val currentAvatar by AvatarData.currentAvatar.collectAsState()

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        gameViewModel.updatePermissionStatus(isGranted)
    }
    LaunchedEffect(Unit) {
        val needsPermissionCheck = !uiState.hasStoragePermission && !uiState.permissionInitiallyChecked
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val currentPermissionStatus = ContextCompat.checkSelfPermission(context, permissionToRequest)
            if (currentPermissionStatus == PackageManager.PERMISSION_GRANTED) {
                if (!uiState.hasStoragePermission) {
                    gameViewModel.updatePermissionStatus(true)
                }
            } else if (needsPermissionCheck) {
                permissionLauncher.launch(permissionToRequest)
            }
        } else {
            if (!uiState.hasStoragePermission) {
                gameViewModel.updatePermissionStatus(true)
            }
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            gameViewModel.dismissSnackbar()
        }
    }

    val currentRoomValue: MutableStateFlow<Room?> = uiState.currentRoom

    LaunchedEffect(currentRoomValue, uiState.hasStoragePermission) {
        if (uiState.hasStoragePermission || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (uiState.imageUri == null && !uiState.isLoadingImage && uiState.imageErrorMessage == null) {
                gameViewModel.loadImageForCurrentRoom(context)
            } else if (uiState.imageUri == null && uiState.isLoadingImage) {
                gameViewModel.loadImageForCurrentRoom(context)
            }
        }
    }

    val damageInitialOffsetX = 80.dp
    val damageTargetOffsetX = 0 .dp
    val damageAnimatedOffsetX = remember { Animatable(damageInitialOffsetX.value) }

    LaunchedEffect(uiState.damageTakenText) {
        if (uiState.damageTakenText != null) {
            damageAnimatedOffsetX.snapTo(damageInitialOffsetX.value)
            damageAnimatedOffsetX.animateTo(
                targetValue = damageTargetOffsetX.value,
                animationSpec = tween(
                    durationMillis = 2500,
                    easing = LinearEasing
                )
            )
        }
    }

    // Animation for blood splatter
    val infiniteTransition = rememberInfiniteTransition(label = "blood_splatter_transition")
    val bloodOffsetX by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 70, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blood_splatter_offset_x"
    )

    // Animation for avatar taking damage
    val avatarShakeTransition = rememberInfiniteTransition(label = "avatar_shake_transition")

    val avatarOffsetX by if (uiState.isTakingDamage) {
        avatarShakeTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 50, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "avatar_offset_x"
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }

    val avatarColor by animateColorAsState(
        targetValue = if (uiState.isTakingDamage) Color.Red.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "avatar_color_overlay"
    )


    if (uiState.isThinking) {
        ThinkingScreen()
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Health Bar and Equipped Weapon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .statusBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HealthBar(
                        currentHealth = uiState.playerCurrentHealth,
                        maxHealth = uiState.playerMaxHealth,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Equipped:",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Image(
                        painter = painterResource(id = equippedWeapon.iconResId),
                        contentDescription = "Equipped Weapon: ${equippedWeapon.name}",
                        modifier = Modifier.size(40.dp)
                    )
                }

                // Room
                Box(modifier = Modifier.weight(1f)) {
                    if (uiState.isLoadingImage) {
                        CircularProgressIndicator()
                        Text("Loading image for ${uiState.currentRoom.value?.name ?: "selected room"}...")
                    } else if (uiState.imageUri != null) {
                        AsyncImage(
                            model = uiState.imageUri,
                            contentDescription = "Image for ${uiState.currentRoom.value?.name ?: "selected room"}",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit,
                            onError = { errorResult ->
                                gameViewModel.onImageLoadError(
                                    errorResult.result.throwable.localizedMessage ?: "Unknown error"
                                )
                                Log.e(
                                    "GameDisplay",
                                    "Coil error loading image: ${errorResult.result.throwable}"
                                )
                            }
                        )
                    } else if (uiState.imageErrorMessage != null) {
                        Text("Error: ${uiState.imageErrorMessage}")
                    } else {
                        Text("No image available for this room or permission denied.")
                    }

                    // Overlay Image for Monsters
                    if (uiState.showOverlayImage && uiState.overlayImageSource != null) {
                        OverlayImage(
                            imageSource = uiState.overlayImageSource,
                            imageDescription = uiState.overlayImageDescription,
                            imageSize = uiState.overlayImageFixedSize,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    // Monster blood splatter
                    if (uiState.showBloodSplatter && uiState.bloodSplatterImageSource != null) {
                        Image(
                            painter = painterResource(id = uiState.bloodSplatterImageSource!!),
                            contentDescription = "Blood splatter",
                            modifier = Modifier
                                .fillMaxSize()
                                .offset(x = bloodOffsetX.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Avatar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val avatarBitmap = ImageBitmap.imageResource(id = currentAvatar.avatarResId)

                        Image(
                            bitmap = avatarBitmap,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .offset(x = avatarOffsetX.dp)
                                .padding(8.dp)
                                .size(100.dp)
                                .drawBehind {
                                    val shadowScaleFactor = 0.25f
                                    val scaledWidth =
                                        (avatarBitmap.width * shadowScaleFactor).toInt()
                                    val scaledHeight =
                                        (avatarBitmap.height * shadowScaleFactor).toInt()

                                    val scaledAvatarForShadow = avatarBitmap.asAndroidBitmap()
                                        .scale(scaledWidth, scaledHeight)
                                    val shadowColor = Color.Black.copy(alpha = 0.7f)

                                    // Adjust these values:
                                    val shadowOffsetX = -5.dp.toPx()
                                    val shadowOffsetY = -2.dp.toPx()
                                    val blurRadius = 8.dp.toPx()

                                    val shadowPaint = android.graphics.Paint().apply {
                                        isAntiAlias = true
                                        colorFilter = android.graphics.PorterDuffColorFilter(
                                            shadowColor.toArgb(),
                                            android.graphics.PorterDuff.Mode.SRC_IN
                                        )
                                        if (blurRadius > 0) {
                                            maskFilter = BlurMaskFilter(
                                                blurRadius.coerceAtLeast(0.1f),
                                                BlurMaskFilter.Blur.NORMAL
                                            )
                                        }
                                    }

                                    // Draw the shadow
                                    drawContext.canvas.nativeCanvas.drawBitmap(
                                        scaledAvatarForShadow, // Original bitmap
                                        shadowOffsetX,
                                        shadowOffsetY,
                                        shadowPaint,

                                        )
                                }
                                .graphicsLayer {
                                    alpha = if (avatarColor != Color.Transparent) 0.7f else 1f
                                },
                            colorFilter = if (avatarColor != Color.Transparent) ColorFilter.tint(
                                avatarColor,
                                BlendMode.SrcAtop
                            ) else null
                        )

                        this@Column.AnimatedVisibility(
                            visible = uiState.damageTakenText != null,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(
                                    x = damageAnimatedOffsetX.value.dp,
                                    y = (-20).dp)
                        ) {
                            uiState.damageTakenText?.let { damageText ->
                                Text(
                                    text = damageText,
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Yellow
                                    )
                                )
                            }
                        }
                    }
                }

                // Player's buttons
                if (uiState.isInCombatMode) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        imageButton(
                            { gameViewModel.fight() },
                            R.drawable.button1,150.dp, 50.dp,
                            "Fight!", 16.sp
                        )
                        imageButton(
                            { gameViewModel.appease() },
                            R.drawable.button1,150.dp, 50.dp,
                            "Appease", 16.sp
                        )

                    }
                } else {
                    val roomActions = uiState.currentRoom.value?.actions ?: emptyList()
                    if (roomActions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            roomActions.forEach { action ->
                                var actionButtonText = ""
                                when (action.type) {
                                    ActionType.LOOK -> actionButtonText = "Look"
                                    ActionType.OPEN -> actionButtonText = "Open ${action.item}"
                                    ActionType.GO -> actionButtonText = "Go ${action.direction}"
                                }
                                imageButton(
                                    { gameViewModel.handleAction(action) },
                                    R.drawable.button1,150.dp, 50.dp,
                                    actionButtonText, 16.sp
                                )

                            }
                        }
                    } else {
                        Text(
                            "No actions available for this room.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.25f))
            }

            // Info Popup
            if (uiState.showInfoPopup) {
                MessagePopup(
                    isVisible = true,
                    title = uiState.infoPopupTitle,
                    message = uiState.infoPopupMessage ?: "",
                    onDismiss = { gameViewModel.closeInfoPopup() }
                )
            }

            // Camera screen
            if (uiState.showCamera) {
                CameraScreen(
                    onImageCaptured = { capturedBitmap ->
                        gameViewModel.viewModelScope.launch {
                            gameViewModel.handleCapturedImage(bitmap = capturedBitmap)
                        }
                        gameViewModel.toggleCamera(false)
                    }
                )
            }
        }
    }
}
