package eladkay.onepunchbot

import java.nio.channels.ClosedChannelException

/**
 * @author WireSegal
 * Created at 2:32 PM on 5/7/17.
 */

private val timeouts = mutableMapOf<Long, Thread>()
private val intervals = mutableMapOf<Long, Thread>()

fun setTimeout(millis: Long, code: () -> Unit): Long {
    val thread = Thread {
        try {
            Thread.sleep(millis)
            code()
            timeouts.remove(Thread.currentThread().id)
        } catch (e: Exception) {
            if (e !is ClosedChannelException) {
                val id = Thread.currentThread().id
                println("Critical failure in timeout thread $id!")
                timeouts.remove(id)
            }
        }
    }

    timeouts.put(thread.id, thread)
    thread.start()
    return thread.id
}

fun setInterval(millis: Long, code: () -> Unit): Long {
    val thread = Thread {
        try {
            while (true) {
                Thread.sleep(millis)
                code()
            }
        } catch (e: Exception) {
            if (e !is ClosedChannelException) {
                val id = Thread.currentThread().id
                println("Critical failure in interval thread $id!")
                intervals.remove(id)
            }
        }
    }
    intervals.put(thread.id, thread)
    thread.start()
    return thread.id
}


fun clearTimeout(idCode: Long): Boolean {
    val thread = timeouts[idCode] ?: return false
    if (!thread.isAlive) return false
    thread.interrupt()
    timeouts.remove(idCode)
    return true
}

fun clearInterval(idCode: Long): Boolean {
    val thread = intervals[idCode] ?: return false
    if (!thread.isAlive) return false
    thread.interrupt()
    intervals.remove(idCode)
    return true
}
