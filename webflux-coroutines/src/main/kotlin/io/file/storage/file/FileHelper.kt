package io.file.storage.file

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.nio.file.Files

@Repository
class FileHelper {

    private val log = LoggerFactory.getLogger(FileHelper::class.java)

    suspend fun moveFile(command: FileMoveCommand) = coroutineScope {
        log.info("[{}]에서 [{}]로 이동합니다.", command.from.path.normalize(), command.to.path.normalize())
//        delay(10)
        FilePath(Files.move(command.from.path, command.to.path))
    }
}