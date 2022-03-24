import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Test
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DefaultTest {
    private val _someGroups = MutableStateFlow(setOf(1, 3, 5, 7, 9))
    val someGroups = _someGroups.asStateFlow()
    private val otherGroups = MutableStateFlow(setOf(100, 101, 102, 103))
    private val realGroups = _someGroups.flatMapLatest { someSet ->
        otherGroups.flatMapLatest { set ->
            flow {
                set.forEach { emit(it) }
            }
        }
    }

    @Test
    fun testTest() {
        val a = ConcurrentHashMap<Int, Int>()
        val b = CopyOnWriteArrayList<Int>()
        val c = CopyOnWriteArraySet<Int>()
        val d = Collections.synchronizedList(ArrayList<Int>())
        val e = Collections.synchronizedMap(HashMap<Int, Int>())
        val f = Hashtable<Int, Int>()

        val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val otherScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        otherScope.launch {
            val asyncJob = coroutineScope.async {
                println("......... start async job")
                delay(5000)
                println("......... done!")
                "DONE JOB"
            }
            try {
                asyncJob.await()
            } catch (e: Throwable) {
                println("......... error! $e")
            }
        }

        // receiver
        //
        GlobalScope.launch {
            launch {
                delay(2000)
                println("......... cancel otherScope")
                otherScope.cancel()
            }
            launch {
                otherGroups.collect { it.forEach { element -> println(">>> other g $element") } }
            }
            launch {
                realGroups.collect { println(">>> real g $it") }
            }
        }

        // setter
        //
        GlobalScope.launch {
            delay(1000)
            println("= someGroups updated!")
            _someGroups.value = setOf(11, 12, 31, 43)
        }

        GlobalScope.launch {
            delay(2000)
            println("= otherGroups updated!")
            otherGroups.value = setOf(11, 12, 31, 43)
        }
        Thread.sleep(7000)
    }

    @Test
    fun someTest() {
//        Observable.combineLatest(
//            Observable.interval(1000, TimeUnit.MILLISECONDS),
//            Observable.interval(2000, TimeUnit.MILLISECONDS).map { it * 100 }
//        ) { interval1, interval2 ->
//            println("=== $interval1, $interval2 --> ${interval1 + interval2}")
//            return@combineLatest interval1 + interval2
//        }.subscribe { value -> println("=== $value") }

//        Observable.merge(
//            Observable.interval(1000, TimeUnit.MILLISECONDS).map { it.toString() },
//            Observable.interval(2000, TimeUnit.MILLISECONDS).map { it * 100 }
//        ).subscribe {
//            println("=== ${it is String} $it")
//        }

//        Observable.concat(
//            Observable.interval(1000, TimeUnit.MILLISECONDS).map { it.toString() }.take(3),
//            Observable.interval(2000, TimeUnit.MILLISECONDS).map { it * 100 }.take(3)
//        ).subscribe {
//            println("=== ${it is String} $it")
//        }

        // ambition 야망
        // 제일 빠른 자만 살아남는다.
//        Observable.amb(
//            listOf(
//                Observable.interval(1000, TimeUnit.MILLISECONDS).map { it.toString() }.take(3),
//                Observable.interval(2000, TimeUnit.MILLISECONDS).map { it * 100 }.take(3)
//            )
//        ).subscribe {
//            println("=== ${it is String} $it")
//        }

//        Observable.merge(
//            Observable.interval(1000, TimeUnit.MILLISECONDS).map { it.toString() },
//            Observable.interval(2000, TimeUnit.MILLISECONDS).map { it * 100 }
//        )
//            .takeUntil(Observable.timer(4000, TimeUnit.MILLISECONDS))
//            .subscribe {
//                println("=== ${it is String} $it")
//            }

//        Observable.merge(
//            Observable.interval(1000, TimeUnit.MILLISECONDS).map { it.toString() },
//            Observable.interval(2000, TimeUnit.MILLISECONDS).map { it * 100 }
//        )
//            .takeUntil { it == "1" }
//            .subscribe {
//                println("=== ${it is String} $it")
//            }

        // ㅇㅏ... 네트워크 모듈 이렇게 했을 듯...ㅠ
//        Observable.merge(
//            Observable.interval(1000, TimeUnit.MILLISECONDS).map { Type.FILE to it },
//            Observable.interval(3000, TimeUnit.MILLISECONDS).map { Type.NETWORK to it }
//        )
//            .takeUntil { it.first == Type.NETWORK }
//            .subscribe {
//                println("=== $it")
//            }

        // 이것도 신박하다.
//        Observable.merge(
//            Observable.interval(1000, TimeUnit.MILLISECONDS).map { Type.FILE to it },
//            Observable.interval(3000, TimeUnit.MILLISECONDS).map { Type.NETWORK to it }
//        )
//            .skipWhile { it.first == Type.FILE }
//            .subscribe {
//                println("=== $it")
//            }

//        Observable.merge(
//            Observable.interval(1000, TimeUnit.MILLISECONDS)
//                .take(3),
//            Observable.interval(2000, TimeUnit.MILLISECONDS)
//                .take(3)
//                .map { value -> value + 100 }
//        )
//            .all { value -> value >= 0 }
//            .subscribe { final -> println("=== $final") }

        Thread.sleep(10000)
    }
}

enum class Type {
    NETWORK,
    FILE
}

interface NewsRemoteDataSource {
    fun fetchLatestNews(): List<String>
}

// getLatestNews를 구현하시오
// api 자체는 취소하지 못하게 작성하시오
// sychronized 처리를 하시오
class NewsRepository(
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val externalScope: CoroutineScope
) {
    // Mutex to make writes to cached values thread-safe.
    private val latestNewsMutex = Mutex()

    // Cache of the latest news got from the network.
    private var latestNews: List<String> = emptyList()

    suspend fun getLatestNews(refresh: Boolean = false): List<String> {
        return if (refresh) {
            val deferred = externalScope.async {
                newsRemoteDataSource.fetchLatestNews().also { networkResult ->
                    // Thread-safe write to latestNews.
                    latestNewsMutex.withLock { latestNews = networkResult }
                }
            }
            deferred.await()
        } else {
            return latestNewsMutex.withLock { this.latestNews }
        }
    }
}

fun factorial(index: Int): Int {
    println("===== called!!")
    return when (index) {
        1 -> 1
        else -> factorial(index - 1) * index
    }
}

// Any 의 의미는!?
fun <T : Any> notNullElements(): List<T> {
    return emptyList()
}

interface Animal
interface GoodTempered

fun <T : Animal> petV1(animal: T) where T : GoodTempered {
    object : GoodTempered {}
}

fun <T> petV2(animal: T) where T : Animal, T : GoodTempered {
    object : GoodTempered {}
}

// 사설 ip
// 10.0 ~ 10.255
// 172.16.0 ~ 172.31.255
// 192.168.0 ~ 192.168.255

