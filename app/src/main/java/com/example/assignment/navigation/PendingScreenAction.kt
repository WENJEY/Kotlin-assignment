package com.example.assignment.navigation

object PendingScreenAction {
    const val SCAN = "scan"
    const val IMPORT = "import"
    const val HISTORY = "history"
    const val NEW_CHAT = "new"
    const val CHAT_HISTORY = "chat_history"

    @Volatile
    var scannerLaunch: String? = null

    @Volatile
    var chatLaunch: String? = null

    @Volatile
    var chatConversationId: String? = null

    fun consumeScannerLaunch(): String? {
        val value = scannerLaunch
        scannerLaunch = null
        return value
    }

    fun consumeChatLaunch(): Pair<String?, String?> {
        val action = chatLaunch
        val conversationId = chatConversationId
        chatLaunch = null
        chatConversationId = null
        return action to conversationId
    }
}
