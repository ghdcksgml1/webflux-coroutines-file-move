package io.file.storage.file

import io.file.storage.utils.StopWatch
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@RestController
class FileController(
    val fileService: FileService,
) {
    private val log = LoggerFactory.getLogger(FileController::class.java)

    @PostMapping("/files/move")
    suspend fun moveFile(@RequestBody request: FileMoveRequest) = coroutineScope {
        val stopWatch = StopWatch("[files/move]")
        fileService.moveFile(request)
        log.info(stopWatch.toString())
    }

    @PostMapping("/folders/move")
    suspend fun moveFolder(@RequestBody request: FolderMoveRequest) = coroutineScope {
        val stopWatch = StopWatch("[folders/move]")
        fileService.moveFolder(request)
        log.info(stopWatch.toString())
    }

    @GetMapping("/mock/data")
    suspend fun createFile() = coroutineScope {
        val stopWatch = StopWatch("[mock/data]")
        val file = Path.of("image.eml")

        val deferredList = emptyList<Deferred<Path>>()
        repeat(1_000) {
            val task = async {
                val randomUUID = UUID.randomUUID().toString()
                val path = Path.of("storage", "01", randomUUID)
                Files.copy(file, path)
            }
            deferredList.plus(task)
        }

        for (task in deferredList) {
            task.await()
        }
        log.info(stopWatch.toString())
    }
}