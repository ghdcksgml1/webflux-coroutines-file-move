package io.file.storage.file

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

@Service
class FileService(
    val fileHelper: FileHelper,
) {
    private val log = LoggerFactory.getLogger(FileService::class.java)

    suspend fun moveFile(request: FileMoveRequest) = coroutineScope {
        val fromPath = Path(request.fromPath)
        val toDirectoryPath = Path(request.toDirectory)

        val fromFileName = fromPath.fileName
        val toDirectory: Path = extractParentDirectoryIfNotDirectoryPath(toDirectoryPath)
        val toPath = toDirectory.resolve(fromFileName)

        val fromFilePath = FilePath(fromPath)
        val toFilePath = FilePath(toPath)

        val validateFromPathDeferred = async { verifyExistsDirectoryPath(fromPath) }
        val validateToPathDeferred = async { verifyExistsDirectoryPath(toPath) }
        awaitAll(validateFromPathDeferred, validateToPathDeferred)

        fileHelper.moveFile(FileMoveCommand(fromFilePath, toFilePath))
    }

    suspend fun moveFolder(request: FolderMoveRequest) = coroutineScope {
        val fromDirectory = Path(request.fromDirectory)
        val toDirectory = Path(request.toDirectory)

        val validateFromDirectoryDeferred = async { verifyExistsDirectoryPath(fromDirectory) }
        val validateToDirectoryDeferred = async { verifyExistsDirectoryPath(toDirectory) }
        awaitAll(validateFromDirectoryDeferred, validateToDirectoryDeferred)

        channelFlow {
            val filesPathInDirectory = fromDirectory.listDirectoryEntries()

            for (fromFilePath in filesPathInDirectory) {
                val toFilePath = toDirectory.resolve(fromFilePath.fileName)
                val moveCommand = FileMoveCommand(FilePath(fromFilePath), FilePath(toFilePath))
                val deferredMoveFile = async(Dispatchers.IO) { fileHelper.moveFile(moveCommand) }
                send(deferredMoveFile)
            }
        }.collect {
            it.await()
        }
    }

    private suspend fun extractParentDirectoryIfNotDirectoryPath(target: Path) = coroutineScope {
        if (!target.isDirectory())
            target?.parent ?: throw IllegalArgumentException("올바른 파일경로를 입력해주세요.")
        else target
    }

    private suspend fun verifyExistsDirectoryPath(target: Path) = coroutineScope {
        val parentDirectoryPath = extractParentDirectoryIfNotDirectoryPath(target)
        if (!parentDirectoryPath.exists()) {
            throw RuntimeException("존재하지 않는 디렉토리 입니다.")
        }
    }
}