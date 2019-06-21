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

fun mpfor(init: Int, condition: (i: Int) -> Boolean, after: (i: Int) -> Int, body: (i: Int) -> Unit) {
    val executor = MPExecutor(getNumProcessors())
    var i = init

    while (condition.invoke(i)) {
        val finalI = i
        executor.execute {
            body.invoke(finalI)
        }

        i = after.invoke(i)
    }

    executor.waitFor()
}

fun mpfor(progression: IntProgression, body: (i: Int) -> Unit) {
    val np = getNumProcessors()
    val executor = MPExecutor(np)

    val numIterations = Math.abs(progression.last - progression.first + 1) / progression.step
    val execsPerThread = if (numIterations < np) 1 else numIterations / np
    val remainder = if (numIterations < np) 0 else numIterations % np
    val increasing = progression.last > progression.first

    var counter = progression.first

    for (i in 0 until np) {
        if (increasing && counter > progression.last || !increasing && counter < progression.last)
            break

        val numExecs = if (i == 0) execsPerThread + remainder else execsPerThread
        val finalI = counter

        executor.execute {
            for (j in 0 until numExecs) {
                body.invoke(finalI + j * progression.step)
            }
        }

        counter += numExecs * progression.step
    }

    executor.waitFor()
}
