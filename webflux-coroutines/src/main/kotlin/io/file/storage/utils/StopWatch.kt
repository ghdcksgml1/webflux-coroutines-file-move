package io.file.storage.utils

class StopWatch(val id: String) {
    private val startTime = System.currentTimeMillis()

    override fun toString(): String {
        val runningTime = System.currentTimeMillis() - startTime
        return "${id}의 걸린 시간은 ${RunningTimeFormatter.format(runningTime)} 입니다."
    }
}