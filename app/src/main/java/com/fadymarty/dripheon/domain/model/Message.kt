package com.fadymarty.dripheon.domain.model

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromLocalUser: Boolean = false,
)