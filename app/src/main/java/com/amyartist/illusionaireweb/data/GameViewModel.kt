package com.amyartist.illusionaireweb.data

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.SoundPool
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.amyartist.illusionaireweb.PlayerStats
import com.amyartist.illusionaireweb.R
import com.amyartist.illusionaireweb.utils.findImageUriByName
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.content
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class GameUiState(
    val currentRoom: MutableStateFlow<Room?>,
    val imageUri: Uri? = null,
    val isLoadingImage: Boolean = true,
    val imageErrorMessage: String? = null,
    val snackbarMessage: String? = null,
    val showInfoPopup: Boolean = false,
    val infoPopupTitle: String = "",
    val infoPopupMessage: String? = null,
    val showCamera: Boolean = false,
    val capturedImageBitmap: Bitmap? = null,
    val showOverlayImage: Boolean = false,
    val overlayImageSource: Any? = null,
    val overlayImageDescription: String? = null,
    val overlayImageFixedSize: Dp? = 150.dp,
    val isInCombatMode: Boolean = false,
    val currentAction: Action? = null,
    val playerCurrentHealth: Int = PlayerStats.currentHealth,
    val playerMaxHealth: Int = PlayerStats.maxHealth,
    val hasStoragePermission: Boolean = false,
    val permissionInitiallyChecked: Boolean = false,
    val isThinking: Boolean = false,
    val bloodSplatterImageSource: Int? = null,
    val showBloodSplatter: Boolean = false,
    val isTakingDamage: Boolean = false,
    val isMusicPlaying: Boolean = false,
    val damageTakenText: String? = null
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val _viewModelGameRooms: MutableMap<String, Room> =
        gameRooms.mapValues { entry -> entry.value.copy() }.toMutableMap()
    private val initialRoomId: String? = _viewModelGameRooms.keys.firstOrNull()
    private var soundPool: SoundPool? = null
    var fightSoundId: Int = 0
    var creakSoundId: Int = 0
    var hmSoundId: Int = 0
    var footstepsSoundId: Int = 0
    var magicSoundId: Int = 0
    var hurtSoundId: Int = 0
    var scarySoundId: Int = 0
    var selectSoundId: Int = 0
    private var backgroundMusicPlayer: MediaPlayer? = null

    private val _uiState = MutableStateFlow(GameUiState(
        currentRoom = MutableStateFlow(initialRoomId?.let { _viewModelGameRooms[it] }))
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    fun updatePermissionStatus(hasPermission: Boolean) {
        _uiState.update { it.copy(hasStoragePermission = hasPermission, permissionInitiallyChecked = true) }
        if (hasPermission) {
            if (_uiState.value.imageUri == null) {
                loadImageForCurrentRoom(getApplication<Application>().applicationContext)
            }
        } else {
            _uiState.update { it.copy(isLoadingImage = false, imageErrorMessage = "Permission denied. Cannot load image from gallery.") }
        }
    }

    fun loadImageForCurrentRoom(context: Context) {
        if (!_uiState.value.hasStoragePermission && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            _uiState.update { it.copy(isLoadingImage = false, imageErrorMessage = "Storage permission needed to load images.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingImage = true, imageErrorMessage = null, imageUri = null) }
            val room = _uiState.value.currentRoom.value ?: return@launch
            try {
                val foundUri = findImageUriByName(context, room.id)
                if (foundUri != null) {
                    _uiState.update { it.copy(imageUri = foundUri, isLoadingImage = false) }
                } else {
                    _uiState.update { it.copy(imageErrorMessage = "Image not found for room: ${room.name}", isLoadingImage = false) }
                    Log.e("GameViewModel", "Image URI not found for room ID: ${room.name}")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(imageErrorMessage = "Error loading image: ${e.localizedMessage}", isLoadingImage = false) }
                Log.e("GameViewModel", "Exception while loading image: ${e.message}")
            }
        }
    }

    fun onImageLoadError(errorMessage: String) {
        _uiState.update { it.copy(imageErrorMessage = "Failed to load image: $errorMessage", isLoadingImage = false) }
    }

    // Game Actions
    fun handleAction(action: Action) {
        val currentRoomId = _uiState.value.currentRoom.value?.id ?: return
        _uiState.update { it.copy(currentAction = action) }
        when (action.type) {
            ActionType.LOOK -> {
                playSound(hmSoundId)
                AvatarData.changeAvatar(action.avatar ?: Avatars.NEUTRAL)
                _uiState.update {
                    it.copy(
                        infoPopupTitle = "Looking...",
                        infoPopupMessage = action.message,
                        showInfoPopup = true,
                        showOverlayImage = false
                    )
                }
            }

            ActionType.OPEN -> {
                playSound(creakSoundId)
                val currentRoomFromViewModel = _viewModelGameRooms[currentRoomId]
                val actionFromViewModel = currentRoomFromViewModel?.actions?.find { it.id == action.id } ?: action

                val updatedAction = action.copy(opened = true)

                var contentsMessage = "This ${updatedAction.item} is empty."
                var enterCombat = false
                var newOverlayImageSource: Any? = null
                var newOverlayImageDescription: String? = null
                var newOverlayImageFixedSize: Dp? = _uiState.value.overlayImageFixedSize

                if (!actionFromViewModel.opened) {
                    if (updatedAction.monster != null) {
                        AvatarData.changeAvatar(action.avatar ?: Avatars.SURPRISED)
                        newOverlayImageSource = updatedAction.monster.image
                        newOverlayImageDescription = updatedAction.monster.description
                        newOverlayImageFixedSize = 200.dp
                        enterCombat = true
                        playSound(scarySoundId)
                    } else if (updatedAction.contents != null) {
                        playSound(magicSoundId)
                        AvatarData.changeAvatar(action.avatar ?: Avatars.HAPPY)
                        contentsMessage = "You found:"
                        updatedAction.contents.forEach { item ->
                            contentsMessage += "\n- ${item.name}"
                            if (item is Weapon) {
                                InventoryData.equipWeapon(item)
                                showSnackbar("Equipped ${item.name}")
                            }
                        }
                    }
                } else {
                    AvatarData.changeAvatar(Avatars.NEUTRAL)
                    contentsMessage = "You have already searched the ${updatedAction.item}."
                }

                _viewModelGameRooms[currentRoomId]?.let { roomToUpdate ->
                    val newRoomActions = roomToUpdate.actions.map { act ->
                        if (act.id == action.id) updatedAction else act
                    }
                    _viewModelGameRooms[currentRoomId] = roomToUpdate.copy(actions = newRoomActions)
                }

                _uiState.update {
                    it.copy(
                        currentAction = updatedAction,
                        infoPopupTitle = "Contents of ${updatedAction.item}",
                        infoPopupMessage = contentsMessage,
                        showInfoPopup = !enterCombat,
                        showOverlayImage = enterCombat,
                        overlayImageSource = newOverlayImageSource,
                        overlayImageDescription = newOverlayImageDescription,
                        overlayImageFixedSize = newOverlayImageFixedSize,
                        isInCombatMode = enterCombat
                    )
                }
            }

            ActionType.GO -> {
                playSound(footstepsSoundId)
                AvatarData.changeAvatar(action.avatar ?: Avatars.NEUTRAL)
                updateCurrentRoomById(action.destinationRoomId)
            }
        }
    }

    private fun updateCurrentRoomById(roomId: String?) {
        roomId?.let { id ->
            gameRooms[id]?.let { newRoom ->
                _uiState.update { currentState ->
                    currentState.copy(
                        currentRoom = MutableStateFlow(newRoom),
                        imageUri = null,
                        isLoadingImage = true,
                        imageErrorMessage = null,
                        hasStoragePermission = currentState.hasStoragePermission,
                        permissionInitiallyChecked = currentState.permissionInitiallyChecked
                    )
                }
            }
        }
    }

    fun updateDamageTaken(damageTaken: String?) {
        _uiState.update {
            it.copy(
                damageTakenText = damageTaken
            )
        }
    }

    fun setIsTakingDamage(isDamaged: Boolean) {
        _uiState.update {
            it.copy(
                isTakingDamage = isDamaged
            )
        }
    }

    // Combat Actions
    fun fight() {
        val currentAction = _uiState.value.currentAction
        val playerWeapon = InventoryData.getCurrentWeapon()

        if (currentAction?.monster?.strength != null) {
            playSound(fightSoundId)
            val damageToPlayer = (currentAction.monster.strength - playerWeapon.strength).coerceAtLeast(0)
            PlayerStats.takeDamage(damageToPlayer, this)

            _uiState.update {
                it.copy(
                    playerCurrentHealth = PlayerStats.currentHealth,
                    bloodSplatterImageSource = R.drawable.blood_splatter,
                    showBloodSplatter = true,
                    snackbarMessage = "You fought the ${currentAction.monster.description}!"
                )
            }
            viewModelScope.launch {
                delay(1500)
                _uiState.update {
                    it.copy(
                        showOverlayImage = false,
                        showBloodSplatter = false,
                        bloodSplatterImageSource = null,
                        isInCombatMode = false,
                        overlayImageSource = null,
                        overlayImageDescription = null
                    )
                }
            }

        } else {
            _uiState.update {
                it.copy(
                    showOverlayImage = false,
                    isInCombatMode = false,
                    snackbarMessage = "Could not initiate fight."
                )
            }
        }
    }
    // Appease monster
    fun appease() {
        val currentAction = _uiState.value.currentAction
        toggleCamera(true)
        _uiState.update {
            it.copy(
                infoPopupTitle = "Appease me!",
                infoPopupMessage = "${currentAction?.appeaseMessage}",
                showInfoPopup = true
            )
        }
    }

    fun closeInfoPopup() {
        _uiState.update { it.copy(showInfoPopup = false) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun toggleCamera(show: Boolean) {
        if (show) {
            releaseSoundResources()
        } else {
            reinitializeSoundResources(getApplication())
             if (uiState.value.isMusicPlaying) {
                 startBackgroundMusic()
             }
        }
        _uiState.update { it.copy(showCamera = show) }
    }

    @OptIn(PublicPreviewAPI::class)
    suspend fun handleCapturedImage(bitmap: Bitmap) { // Make this suspend again
        _uiState.update { currentState ->
            currentState.copy(
                capturedImageBitmap = bitmap,
                showCamera = false,
                isThinking = true
            )
        }

        val currentAction = _uiState.value.currentAction
        val promptText = currentAction?.appeasePrompt

        if (promptText.isNullOrBlank()) {
            _uiState.update {
                it.copy(
                    snackbarMessage = "Cannot process image: AI prompt is missing.",
                    isThinking = false
                )
            }
            return
        }

        try {
            val model = Firebase.ai.generativeModel("gemini-2.5-pro")
            val content = content {
                image(bitmap)
                _uiState.value.currentAction?.appeasePrompt?.let { text(it) }
            }
            val response = model.generateContent(content)

            val appeased = response.text?.contains("yes", ignoreCase = true)
            var snackbarMsg = "The creature seems appeased!"
            if (appeased != true) {
                val damageToPlayer: Int = (currentAction.monster!!.strength).coerceAtLeast(0)
                PlayerStats.takeDamage(damageToPlayer, this)
                snackbarMsg = "The creature is not impressed, you failed to appease!"
            } else {
                AvatarData.changeAvatar(Avatars.HAPPY)
            }

            _uiState.update {
                it.copy(
                    playerCurrentHealth = PlayerStats.currentHealth,
                    snackbarMessage = snackbarMsg,
                    isThinking = false,
                    isInCombatMode = false,
                    showOverlayImage = false,
                    overlayImageSource = null,
                    overlayImageDescription = null
                )
            }

        } catch (e: Exception) {
            Log.e("GameViewModel", "Error calling Firebase AI: ${e.message}", e)
            _uiState.update {
                it.copy(
                    snackbarMessage = "Error processing image with AI: ${e.localizedMessage}",
                    isThinking = false
                )
            }
        }
    }

    fun playSound(sound: Int) {
        soundPool?.play(sound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    fun startBackgroundMusic() {
        if (backgroundMusicPlayer?.isPlaying == false) {
            backgroundMusicPlayer?.start()
            _uiState.update { it.copy(isMusicPlaying = true) }
        }
    }

    private fun releaseSoundResources() {
        backgroundMusicPlayer?.stop()
        backgroundMusicPlayer?.release()
        backgroundMusicPlayer = null

        soundPool?.release()
        soundPool = null
    }

    private fun reinitializeSoundResources(application: Application) {
        val platformAudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(platformAudioAttributes)
            .build()

        fightSoundId = soundPool?.load(application, R.raw.monster_hit, 1) ?: 0
        creakSoundId = soundPool?.load(application, R.raw.creak, 1) ?: 0
        hmSoundId = soundPool?.load(application, R.raw.hm, 1) ?: 0
        footstepsSoundId = soundPool?.load(application, R.raw.footsteps, 1) ?: 0
        magicSoundId = soundPool?.load(application, R.raw.magic, 1) ?: 0
        hurtSoundId = soundPool?.load(application, R.raw.hurt, 1) ?: 0
        scarySoundId = soundPool?.load(application, R.raw.scary, 1) ?: 0
        selectSoundId = soundPool?.load(application, R.raw.select, 1) ?: 0

        backgroundMusicPlayer = MediaPlayer.create(application, R.raw.background_music)
        backgroundMusicPlayer?.isLooping = true
        backgroundMusicPlayer?.setVolume(0.4f, 0.4f)
    }

    init {
        val platformAudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(platformAudioAttributes)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(6)
            .setAudioAttributes(platformAudioAttributes)
            .build()

        fightSoundId = soundPool?.load(application, R.raw.monster_hit, 1) ?: 0
        creakSoundId = soundPool?.load(application, R.raw.creak, 1) ?: 0
        hmSoundId = soundPool?.load(application, R.raw.hm, 1) ?: 0
        footstepsSoundId = soundPool?.load(application, R.raw.footsteps, 1) ?: 0
        magicSoundId = soundPool?.load(application, R.raw.magic, 1) ?: 0
        hurtSoundId = soundPool?.load(application, R.raw.hurt, 1) ?: 0
        scarySoundId = soundPool?.load(application, R.raw.scary, 1) ?: 0
        selectSoundId = soundPool?.load(application, R.raw.select, 1) ?: 0

        backgroundMusicPlayer = MediaPlayer.create(application, R.raw.background_music)
        backgroundMusicPlayer?.isLooping = true
        backgroundMusicPlayer?.setVolume(0.4f, 0.4f)

        InventoryData.equipWeapon(Weapons.FISTS)
        AvatarData.changeAvatar(Avatars.NEUTRAL)
        _uiState.update {
            it.copy(
                playerCurrentHealth = PlayerStats.currentHealth,
                playerMaxHealth = PlayerStats.maxHealth
            )
        }
    }
}
