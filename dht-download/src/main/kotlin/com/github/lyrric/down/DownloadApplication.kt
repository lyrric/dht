package com.github.lyrric.down


import com.github.lyrric.common.entity.DownloadMsgInfo
import com.github.lyrric.down.stream.MessageStreams
import com.github.lyrric.down.task.BlockingExecutor
import com.github.lyrric.down.task.DownloadTask
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.Executors
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Slf4j
@EnableScheduling
@RestController
@EnableBinding(MessageStreams::class)
@SpringBootApplication
class DownloadApplication {

    @Value("\${download.num.thread}")
    private var nThreads:Int = 10

    private var blockingExecutor: BlockingExecutor? = null

    @StreamListener("download-message-out")
    fun handleMessage(msgInfo: DownloadMsgInfo?) { //submit to blocking executor
        try {
            blockingExecutor?.execute(DownloadTask(msgInfo))
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @Scheduled(fixedDelay = 5 * 60 * 1000)
    fun autoFinalize() { //定时强制回收 Finalizer 队列里的 Socket 对象（有个抽象父类重写了 finalize 方法，
        //频繁创建 Socket 会导致 Socket 得不到及时回收频繁发生 FGC）
        System.runFinalization()
    }

    @PostConstruct
    fun init() { //max task bound 5000
        blockingExecutor = BlockingExecutor(Executors.newFixedThreadPool(nThreads), 5000)
    }

    @PreDestroy
    fun destroy() {
        blockingExecutor?.shutdownNow()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(DownloadApplication::class.java, *args)
        }
    }
}