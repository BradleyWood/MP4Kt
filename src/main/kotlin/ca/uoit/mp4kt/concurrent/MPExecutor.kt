package ca.uoit.mp4kt.concurrent

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MPExecutor(numThreads: Int) : Executor {

    private val executor: ExecutorService = Executors.newFixedThreadPool(numThreads)

    fun waitFor() {
        executor.shutdown()
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS)
    }

    override fun execute(task: Runnable) {
        executor.execute(task)
    }
}
