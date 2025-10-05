package com.amyartist.illusionaireapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amyartist.illusionaireapp.R
import com.amyartist.illusionaireapp.composables.imageButton

@Composable
fun MainMenuScreen(
    onButtonOneClick: () -> Unit,
    onButtonTwoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.title),
                contentDescription = "Illusionaire",
                modifier = Modifier
                    .padding(bottom = 32.dp)
            )
            imageButton(
                onButtonOneClick,
                R.drawable.button1,
                250.dp,
                100.dp,
                "Generate Rooms",
                20.sp
                )

            imageButton(
                onButtonTwoClick,
                R.drawable.button1,
                250.dp,
                100.dp,
                "Start Game",
                20.sp
                )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
