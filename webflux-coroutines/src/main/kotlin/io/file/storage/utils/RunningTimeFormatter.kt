package io.file.storage.utils

import java.util.concurrent.TimeUnit
import kotlin.math.pow

object RunningTimeFormatter {
    private val TIME_UNIT_BINARY_VALUE: HashMap<String?, Int?> = object : HashMap<String?, Int?>() {
        init {
            put("MILLISECONDS", 1)
            put("SECONDS", 2)
            put("MINUTES", 4)
            put("HOURS", 8)
            put("DAYS", 16)
        }
    }
    private val TIME_FORMAT = arrayOf("(ms)", "(s)", "(m)", "(h)", "(d)")
    private val TIME_VALUE = arrayOf(1000L, 60L, 60L, 24L, 100000L)

    /**
     * 시간과 TimeUnit을 파라미터로 받아 Time Unit에 맞는 시간으로 변환해주는 함수
     * (MilliSeconds로 단위가 고정되어 있는게 아니라, TimeUnit 기준으로 단위를 설정함.)
     *
     * ex.1) System.out.println(RunningTimeFormatter.format(4223230L, TimeUnit.MILLISECONDS));
     * -> 1(h) 10(m) 23(s) 230(ms)
     * ex.2) System.out.println(format(4223L, TimeUnit.SECONDS));
     * -> 1(h) 10(m) 23(s)
     *
     * @param runningTime : Long 타입의 실행시간 (형식을 변환시킬 대상)
     * @param timeUnit    : 변경할 시간 단위 (MilliSeconds ~ Days)
     * @return
     */
    fun format(runningTime: Long, timeUnit: TimeUnit): String {
        val bitmask = getBitmask(timeUnit)
        return generateStringFromBitMask(bitmask, runningTime)
    }

    /**
     * TimeUnit Default 버전
     *
     * @param runningTime (, TimeUnit.MILLISECONDS)
     * @return
     */
    fun format(runningTime: Long): String {
        val bitmask = TIME_UNIT_BINARY_VALUE["MILLISECONDS"]!!
        return generateStringFromBitMask(bitmask, runningTime)
    }

    /**
     * format에서 잘라내고 싶은 부분을 설정할 수 있는 함수
     *
     * ex.1) RunningTimeFormatter.format(4223230L, TimeUnit.MILLISECONDS, 0)
     * -> 1(h) 10(m) 23(s) 230(ms)
     * RunningTimeFormatter.format(4223230L, TimeUnit.MILLISECONDS, 1)
     * -> 1(h) 10(m) 23(s)
     * RunningTimeFormatter.format(4223230L, TimeUnit.MILLISECONDS, 2)
     * -> 1(h) 10(m)
     * ex.2) RunningTimeFormatter.format(4223L, TimeUnit.SECONDS, 0);
     * -> 1(h) 10(m) 23(s)
     * RunningTimeFormatter.format(4223L, TimeUnit.SECONDS, 1);
     * -> 1(h) 10(m)
     * RunningTimeFormatter.format(4223L, TimeUnit.SECONDS, 2);
     * -> 1(h)
     *
     * @param runningTime : Long 타입의 실행시간 (형식을 변환시킬 대상)
     * @param timeUnit    : 변경할 시간 단위 (MilliSeconds ~ Days)
     * @param startIndex  : 시간 단위로 부터 잘라내고 싶은 단위의 개수
     * @return
     */
    fun format(runningTime: Long, timeUnit: TimeUnit, startIndex: Int): String {
        var cpyRunningTime = runningTime
        require((startIndex >= 0 && startIndex < TIME_VALUE.size)) { "startIndex는 0~4 사이의 값 입니다." }
        var bitmask = getBitmask(timeUnit)
        var i = 0
        while (bitmask != 0) {
            if (bitmask % 2 == 1) {
                require(i + startIndex < TIME_VALUE.size) { "잘못된 startIndex 값 입니다." }
                val index: Int = i + startIndex
                bitmask = 2.0.pow(index.toDouble()) as Int
                while (i < index) {
                    cpyRunningTime /= TIME_VALUE[i]
                    i++
                }
                break
            }
            bitmask /= 2
            i++
        }
        return generateStringFromBitMask(bitmask, cpyRunningTime)
    }

    // TimeUnit을 Bitmask로 바꿔주는 메소드
    private fun getBitmask(timeUnit: TimeUnit): Int {
        val bitmask: Int // TimeUnit에 맞는 2진수를 임시로 저장할 변수
        bitmask = when (timeUnit) {
            TimeUnit.MILLISECONDS -> TIME_UNIT_BINARY_VALUE["MILLISECONDS"]!!
            TimeUnit.SECONDS -> TIME_UNIT_BINARY_VALUE["SECONDS"]!!
            TimeUnit.MINUTES -> TIME_UNIT_BINARY_VALUE["MINUTES"]!!
            TimeUnit.HOURS -> TIME_UNIT_BINARY_VALUE["HOURS"]!!
            TimeUnit.DAYS -> TIME_UNIT_BINARY_VALUE["DAYS"]!!
            else -> 0
        }
        return bitmask
    }

    private fun generateStringFromBitMask(bitmask: Int, runningTime: Long): String {
        var bitmask = bitmask
        val sb = StringBuffer("")
        var cpyRunningTime = runningTime

        // 비트 마스킹 되어있는 1 찾기
        var i = 0
        while (bitmask != 0) {

            // 1을 찾으면 해당 단위부터 계산 시작
            if (bitmask % 2 == 1) {
                while (cpyRunningTime != 0L) {
                    sb.insert(0, " " + cpyRunningTime % TIME_VALUE[i] + TIME_FORMAT[i])
                    cpyRunningTime /= TIME_VALUE[i]
                    i++
                }
            }
            bitmask /= 2
            i++
        }
        return sb.toString()
    }
}