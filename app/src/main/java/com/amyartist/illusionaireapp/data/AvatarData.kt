package com.amyartist.illusionaireapp.data

import androidx.annotation.DrawableRes
import com.amyartist.illusionaireapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AvatarType {
    HAPPY, SAD, SURPRISED, NEUTRAL
}

class Avatar(
    val type: AvatarType,
    @DrawableRes val avatarResId: Int
)

object Avatars {
    val SURPRISED = Avatar(
        type = AvatarType.SURPRISED,
        avatarResId = R.drawable.avatar_surprised
    )
    val HAPPY = Avatar(
        type = AvatarType.HAPPY,
        avatarResId = R.drawable.avatar_happy
    )
    val SAD = Avatar(
        type = AvatarType.SAD,
        avatarResId = R.drawable.avatar_sad
    )
    val NEUTRAL = Avatar(
        type = AvatarType.NEUTRAL   ,
        avatarResId = R.drawable.avatar_neutral
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