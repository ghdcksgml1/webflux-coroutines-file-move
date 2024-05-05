package io.file.storage.file

data class FileMoveCommand(
    val from: FilePath,
    val to: FilePath,
)
