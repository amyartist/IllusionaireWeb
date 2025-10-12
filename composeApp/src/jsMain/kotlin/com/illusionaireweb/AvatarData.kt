package com.illusionaireweb

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AvatarType {
    HAPPY, SAD, SURPRISED, NEUTRAL
}

class Avatar(
    val type: AvatarType,
    val avatarPath: String
)

object Avatars {
    val SURPRISED = Avatar(
        type = AvatarType.SURPRISED,
        avatarPath = "images/avatar_surprised.png"
    )
    val HAPPY = Avatar(
        type = AvatarType.HAPPY,
        avatarPath = "images/avatar_happy.png"
    )
    val SAD = Avatar(
        type = AvatarType.SAD,
        avatarPath = "images/avatar_sad.png"
    )
    val NEUTRAL = Avatar(
        type = AvatarType.NEUTRAL   ,
        avatarPath = "images/avatar_neutral.png"
    )
}

object AvatarData {
    val currentAvatarFlow = MutableStateFlow<Avatar>(Avatars.SURPRISED)
    val currentAvatar: StateFlow<Avatar> = currentAvatarFlow.asStateFlow()

    fun changeAvatar(newAvatar: Avatar) {
        currentAvatarFlow.value = newAvatar
    }

    fun getCurrentAvatar(): Avatar {
        return currentAvatarFlow.value
    }
}