package com.fadymarty.dripheon.data.mapper

import com.fadymarty.dripheon.domain.model.Message

fun String.toMessage(isFromLocalUser: Boolean): Message {
    return Message(
        content = this,
        isFromLocalUser = isFromLocalUser
    )
}