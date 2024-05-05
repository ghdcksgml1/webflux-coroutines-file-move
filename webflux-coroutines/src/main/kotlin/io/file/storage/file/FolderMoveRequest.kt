package io.file.storage.file

data class FolderMoveRequest(
    val fromDirectory: String,
    val toDirectory: String
)
