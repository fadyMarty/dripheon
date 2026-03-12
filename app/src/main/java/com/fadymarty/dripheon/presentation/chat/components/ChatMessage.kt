package com.fadymarty.dripheon.presentation.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fadymarty.dripheon.domain.model.Message

@Composable
fun ChatMessage(
    modifier: Modifier = Modifier,
    message: Message,
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                color = if (message.isFromLocalUser) {
                    MaterialTheme.colorScheme.surfaceContainerLow
                } else MaterialTheme.colorScheme.surfaceContainerLowest
            )
            .padding(16.dp)
    ) {
        Text(
            text = message.content,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}