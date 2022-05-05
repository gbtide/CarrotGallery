import android.graphics.Color
import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Test
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

class DefaultTest {

    // 핵심은, prefix가 참조를 던지는 것이 아니라 값을 던지고 있기 때문에..
    // close 로직 부분에서도 prefix 는 open쪽 로직과 완전히 똑같은 String 값이 들어온다.
    @Test
    fun leetCodeTest() {
        // 재귀함수가 호출됨에 변경사항이 반영되어야할 부분은 바깥 쪽에,
        val n = 3
        val result = mutableListOf<String>()

        // 파라미터는 재귀함수에서 단한번 정의되고 수정안되는 변수들
        fun addParenthesis(
            prefix: String,
            openCount: Int,
            closeCount: Int
        ) {
            if (openCount == n && closeCount == n) {
                result.add(prefix)
                return
            }
            if (openCount < n) {
                addParenthesis("$prefix(", openCount + 1, closeCount)
            }
            if (openCount > closeCount && closeCount < n) {
                addParenthesis("$prefix)", openCount, closeCount + 1)
            }
        }
        
        addParenthesis("(", 1, 0)
        println("=== result : $result")
    }


    /**
     * Input: nums = [1,2,3]
     * Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
     */
    @Test
    fun leetCodeTest02() {
        val nums: IntArray = intArrayOf(0, -1, 1)
        val result = mutableListOf<List<Int>>()

        fun permutation(remainNums: List<Int>, singleResult: List<Int>) {
            if (singleResult.size == nums.size) {
                result.add(singleResult)
                return
            }
            remainNums.forEachIndexed { index, num ->
                val newRemainNums = remainNums.toMutableList()
                newRemainNums.removeAt(index)
                permutation(newRemainNums, singleResult + num)
            }
        }

        permutation(nums.toList(), mutableListOf())
        println("result: " + result.toList())
    }


    @Test
    fun leetCodeTest03() {
        val nums: IntArray = intArrayOf(1, 2, 3)
        val result = mutableListOf<List<Int>>()

        fun combination(singleResult: List<Int>, index: Int) {
            if (index == nums.size) {
                result.add(singleResult)
                return
            }
            // 포함
            combination(singleResult + nums[index], index + 1)
            // 안포함
            combination(singleResult, index + 1)
        }

        combination(listOf(), 0)
        println("result: " + result.toList())
    }

    //
    //
    //

    @Test
    fun testtest() {
        val list = listOf(1, 2, 3, 4, 5)
        run {
            list.forEach {
                if (it > 3) {
                    println("break! $it")
                    return@run  // break
                } else {
                    println("continue! $it")
                    return@forEach  // continue
                }
            }
        }
    }

    // [ hash 자료구조에 mutable 을 데이터를 넣을 때 재앙 ]
    @Test
    fun test() {
        val person = FullName("Mr", "sam")
        val set = mutableSetOf<FullName>()
        set.add(person)
        person.surname = "ailoi"
        println("=== $person") // FullName(name=Mr, surname=ailoi)
        println("=== " + (set.first()))
        println("=== " + (person in set)) // 이게 false 다!!!
        println("=== " + (set.first() == person)) //true
    }

    data class FullName(
        var name: String,
        var surname: String
    )

    // [ compare 잘 써보기 ]
    @Test
    fun test2() {
        val users = listOf(
            User("a", "zb"),
            User("b", "ef"),
            User("b", "af"),
            User("c", "ad")
        )
        val sorted = users.sortedBy { it.surname }
        println("=== $sorted")
        // === [User(name=c, surname=ad), User(name=b, surname=af), User(name=b, surname=ef), User(name=a, surname=zb)]

        val sorted2 = users.sortedWith(
            compareBy(
                { it.name },
                { it.surname }
            )
        )
        println("=== $sorted2")
        // === [User(name=a, surname=zb), User(name=b, surname=af), User(name=b, surname=ef), User(name=c, surname=ad)]
    }

    // [ API의 필수적이지 않은 부분은 확장함수로 추출하라 ]
    inline fun CharSequence?.isNullOrBlank(): Boolean {
        return false
    } // null에 추가 가능
//    public fun Interable<Int>.sum(): Int {} // 특정 generic 타입에 추가 가능
    /**
     * - 확장 함수의 특징
    - 직접 멤버를 추가할 수 없는 경우 쓴다
    - 데이터와 행위를 분리하도록 설계할 때 쓴다
    - 클래스에 추가하는 것이 아니다!! → 타입에 추가를 하는 것이다!!
     */

    // [ 스킬 ]
    @Test
    fun test3() {
        println("== start")
        val ws = WorkShop()
        ws.plus()
        ws.plus()
        val one = WorkShop::makeSomething // KFunction1 이다. reflection reference
        val two = WorkShop::makeSomething
        println("== end ${one.invoke(ws)} _${two.invoke(ws)}")

        // 이렇게
        val user = User("", "")
        val function = User::compareTo
        function.invoke(user, User("", ""))
    }

    class WorkShop {
        var value = 30
            private set

        fun plus() {
            ++value
        }

        fun makeSomething(): Int {
            return value
        }
    }


    data class User(
        var name: String,
        var surname: String
    ) : Comparable<User> {
        // 요렇게도 가능
        companion object {
            val USER_COMPARATOR = compareBy<User>({ it.surname }, { it.name }) // compareBy return value : Comparator<T>
        }

        override fun compareTo(other: User): Int = compareValues(name, other.name)

        // 여러개 비교가 하고 싶다면
        fun comareToV2(other: User): Int = compareValuesBy(this, other, { it.name }, { it.surname })
    }

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
    fun somethingTestGo() {
        // Concurrently executes both sections
        suspend fun doWorld() = coroutineScope { // this: CoroutineScope
            launch {
                delay(2000L)
                println("World 2")
            }
            launch {
                delay(1000L)
                println("World 1")
            }
            println("Hello")
        }
        // 경훈. 내부는 fire and forget 인데, scope 자체는 launch들이 모두 종료될때까지 suspending 되어있다.
        // A coroutineScope in doWorld completes only after both are complete,
        // so doWorld returns and allows Done string to be printed only after that:
        // Sequentially executes doWorld followed by "Done"
        runBlocking {
            doWorld()
            println("Done")
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
        Thread.sleep(10000)
    }

    @Test
    fun testSomeSome() {
        repeat(3) {
            val worker = Worker() // worker 쓰레드 생성
            worker.start() // worker 쓰레드 시작
            Thread.sleep(100) // 메인 쓰레드 잠시 수면
            println("stop을 true로 변경")
            worker.stop = true // worker쓰레드의 stop 플래그 변경
            worker.join() // worker 쓰레드가 끝날 때까지 메인쓰레드에서 대기
        }
        println("작업 종료")
    }

    class Worker : Thread() {
        @Volatile
        var stop = false
        override fun run() {
            println("==== " + currentThread())
            super.run()
            while (!stop) {
            }
        }
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

// 사설 ip : 십.일칠이.일구이
// 10.0 ~ 10.255
// 172.16.0 ~ 172.31.255
// 192.168.0 ~ 192.168.255

class MapIncludeCountingFunction<K, V> constructor(
    val innerMap: MutableMap<K, V> = mutableMapOf()
) : MutableMap<K, V> by innerMap {
    var count = 0
        private set

    override fun put(key: K, value: V): V? {
        count++
        return innerMap.put(key, value)
    }

    // mutableMapOf로 상속을 때리면, putAll이 put을 복수 호출하기 때문에 오동작함
    // 위 상황을 떠나, Framework 코드를 상속받는 다는 것 자체가, 블랙박스 임.
    override fun putAll(from: Map<out K, V>) {
        count += from.size
        innerMap.putAll(from)
    }
}

fun destructor() {
    // [ Data 한정자를 쓰라! ]
    //
    // 1. mapOf(vararg pairs: Pair<K, V>) 라서 되는 거임.
    val trip = mapOf(
        "China" to "Tianjin",
        "Russia" to "Petersburg"
    )
    for (entry in trip) {
    }
    for ((country, city) in trip) {
    }

    // 2. public data class Pair<out A, out B>(first, second) 라서 아래가 되는거다. data 라서!
    val degrees = 10
    val (description, color) = when {
        degrees < 5 -> "cold" to Color.BLUE
        degrees < 23 -> "mild" to Color.YELLOW
        else -> "hot" to Color.RED
    }

    // 3. public inline fun <T> Iterable<T>.partition(predicate: (T) -> Boolean): Pair<List<T>, List<T>>
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7)
    val (odd, even) = numbers.partition { it % 2 == 1 }

    // 4.
    val fullName = "Macin Moka"
    // ?: return 잘 생각해보자...
    // 과거의 나라면 fullName.parseName()?.let {}... 이런식으로 쓰지 않았을까
//    val (firstName, lastName) = fullName.parseName() ?: return

    /**
     * data class Person(val name: String?)
     * val person = Person(null)
     * println("=== GG 1")
     * val s = person.name ?: return
     * println("=== GG 2") // 여기는 노출 안됨
     */

    // 5. for문에 라벨링하기
    // 이름 지을 때는 앞에 태깅하듯이
    // break @loop, return @lit (영어 어순을 생각해보자)
    // @는 풀
    loop@ for (i in 1..100) {
        inner@ for (j in 1..100) {
            if (j == 23) {
                println("== break 1")
                break@loop
            }
        }
    }
    listOf(1, 2, 3, 4, 5).forEach lit@{
        if (it == 3) {
            println("=== break 2")
            return@lit
        }
    }

    val list = listOf(1, 2, 3, 4, 5)
    run {
        list.forEach {
            if (it > 3) {
                return@run  // break
            } else {
                return@forEach  // continue
            }
        }
    }
}

typealias OnClickAliasV2 = (View) -> Unit
typealias OnSwipeAliasV2 = (View) -> Boolean

var clickEvent: OnClickAliasV2? = null
var swipeEvent: OnSwipeAliasV2? = null
fun setUiEvent(onSwipe: OnSwipeAliasV2? = null, onClick: OnClickAliasV2? = null) {}