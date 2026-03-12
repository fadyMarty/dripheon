package com.fadymarty.dripheon.presentation.chat

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fadymarty.dripheon.R
import com.fadymarty.dripheon.presentation.chat.components.ChatMessage
import com.fadymarty.dripheon.presentation.components.DripheonFilledIconButton
import com.fadymarty.dripheon.presentation.components.MessageTextField
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ChatScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreen(
    state: ChatState,
    onEvent: (ChatEvent) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val isScrolledToNewest = rememberSaveable(lazyListState.firstVisibleItemIndex) {
        lazyListState.firstVisibleItemIndex == 0
    }

    LaunchedEffect(state.messages.size) {
        if (isScrolledToNewest) {
            lazyListState.animateScrollToItem(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopAppBar(
                isRefreshing = state.isRefreshing
            )
        },
        bottomBar = {
            MessageBar(
                message = state.message,
                onMessageChange = {
                    onEvent(ChatEvent.OnMessageChange(it))
                },
                onSendMessage = {
                    onEvent(ChatEvent.OnSendMessage)
                }
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            targetState = state.isLoading,
            transitionSpec = {
                (fadeIn() + scaleIn())
                    .togetherWith(fadeOut() + scaleOut())
            }
        ) { isLoading ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = lazyListState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom)
                ) {
                    items(
                        items = state.messages.reversed(),
                        key = { it.id }
                    ) { message ->
                        Row(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                            horizontalArrangement = if (message.isFromLocalUser) {
                                Arrangement.End
                            } else Arrangement.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(0.85f),
                                horizontalArrangement = if (message.isFromLocalUser) {
                                    Arrangement.End
                                } else Arrangement.Start
                            ) {
                                ChatMessage(message = message)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatTopAppBar(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                AnimatedContent(
                    modifier = Modifier.size(32.dp),
                    targetState = isRefreshing,
                    transitionSpec = {
                        (fadeIn() + scaleIn())
                            .togetherWith(fadeOut() + scaleOut())
                    }
                ) { isRefreshing ->
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.ic_glass_cup),
                            contentDescription = null
                        )
                    }
                }
                Text("Dripheon")
            }
        }
    )
}

@Composable
private fun MessageBar(
    modifier: Modifier = Modifier,
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        MessageTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = onMessageChange,
            maxLines = 4
        )
        DripheonFilledIconButton(
            icon = ImageVector.vectorResource(R.drawable.ic_arrow_upward),
            onClick = onSendMessage,
            enabled = message.isNotBlank()
        )
    }
}