package com.amyartist.illusionaireweb

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.amyartist.illusionaireweb.data.AvatarData
import com.amyartist.illusionaireweb.data.Avatars
import com.amyartist.illusionaireweb.data.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

object PlayerStats {
    private const val DEFAULT_MAX_HEALTH = 100
    private var _maxHealth = mutableStateOf(DEFAULT_MAX_HEALTH)
    val maxHealth: Int get() = _maxHealth.value

    private var _currentHealth = mutableStateOf(DEFAULT_MAX_HEALTH)
    val currentHealth: Int get() = _currentHealth.value

    private val _currentHealthFlow = MutableStateFlow(DEFAULT_MAX_HEALTH)

    fun takeDamage(amount: Int, viewModel: GameViewModel) {
        val newHealth = (_currentHealth.value - amount).coerceAtLeast(0)
        _currentHealth.value = newHealth
        _currentHealthFlow.value = newHealth
        viewModel.viewModelScope.launch {
            viewModel.updateDamageTaken((amount * -1).toString())
            viewModel.setIsTakingDamage(true)
            viewModel.playSound(viewModel.hurtSoundId)
            delay(1200)
            viewModel.setIsTakingDamage(false)
            AvatarData.changeAvatar(Avatars.NEUTRAL)
            delay(1500)
            viewModel.updateDamageTaken(null)
        }
    }

    fun heal(amount: Int) {
        val newHealth = (_currentHealth.value + amount).coerceAtMost(_maxHealth.value)
        _currentHealth.value = newHealth
        _currentHealthFlow.value = newHealth
    }

    fun setMaxHealth(newMax: Int, setCurrentToMax: Boolean = true) {
        _maxHealth.value = newMax.coerceAtLeast(1)
        if (setCurrentToMax) {
            _currentHealth.value = _maxHealth.value
            _currentHealthFlow.value = _maxHealth.value
        } else {
            _currentHealth.value = _currentHealth.value.coerceAtMost(_maxHealth.value)
            _currentHealthFlow.value = _currentHealth.value
        }
    }

    fun resetHealth() {
        _currentHealth.value = _maxHealth.value
        _currentHealthFlow.value = _maxHealth.value
    }
}
