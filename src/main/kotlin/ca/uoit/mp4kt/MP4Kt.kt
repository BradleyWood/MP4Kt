package ca.uoit.mp4kt

import ca.uoit.mp4kt.concurrent.MPExecutor

internal fun getNumProcessors() = Runtime.getRuntime().availableProcessors()

fun parallel(np: Int = -1, body: () -> Unit) {
    val count = if (np < 0) getNumProcessors() else np

    val executor = MPExecutor(getNumProcessors())

    for (i in 0 until count) {
        executor.execute(body)
    }

    executor.waitFor()
}
