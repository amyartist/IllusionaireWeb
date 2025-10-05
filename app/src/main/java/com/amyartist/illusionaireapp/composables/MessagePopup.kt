import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun MessagePopup(
    isVisible: Boolean,
    title: String,
    message: String?,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val dialogBackgroundColor = Color(0xFFFAD454)
        val dialogBorderColor = Color(0xFF805D00)
        val buttonTextColor = Color(red = 65, green = 42, blue = 11, alpha = 255)

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, dialogBorderColor)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White, dialogBackgroundColor),
                                center = Offset(0f, 0f),
                                radius = 800f
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = buttonTextColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        message?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = buttonTextColor
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        } ?: Spacer(modifier = Modifier.height(24.dp))


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(Modifier.weight(1.0f))
                            TextButton(
                                onClick = onDismiss,
                            ) {
                                Text(
                                    "OK",
                                    color = buttonTextColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
