package eladkay.onepunchbot

/**
 * @author WireSegal
 * Created at 2:32 PM on 5/7/17.
 */


private val waitingTimeouts = mutableMapOf<Int, Thread>()
private val waitingIntervals = mutableMapOf<Int, Thread>()
private var lastTime = 0
private var lastInterval = 0

fun setTimeout(millis: Long, code: () -> Unit): Int {
    val thread = Thread {
        Thread.sleep(millis)
        code()
    }
    val id = lastTime++
    waitingTimeouts.put(id, thread)
    thread.start()
    return id
}

fun setInterval(millis: Long, code: () -> Unit): Int {
    val thread = Thread {
        while (true) {
            Thread.sleep(millis)
            code()
        }
    }
    val id = lastInterval++
    waitingIntervals.put(id, thread)
    thread.start()
    return id
}


fun clearTimeout(idCode: Int): Boolean {
    val thread = waitingTimeouts[idCode] ?: return false
    if (!thread.isAlive) return false
    thread.interrupt()
    waitingTimeouts.remove(idCode)
    return true
}

fun clearInterval(idCode: Int): Boolean {
    val thread = waitingIntervals[idCode] ?: return false
    if (!thread.isAlive) return false
    thread.interrupt()
    waitingIntervals.remove(idCode)
    return true
}
