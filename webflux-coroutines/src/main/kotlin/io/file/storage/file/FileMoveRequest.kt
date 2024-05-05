package io.file.storage.file

data class FileMoveRequest(
    val fromPath: String,
    val toDirectory: String
)