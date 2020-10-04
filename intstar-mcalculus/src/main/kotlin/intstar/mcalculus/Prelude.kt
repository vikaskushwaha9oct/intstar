package intstar.mcalculus

import java.nio.ByteBuffer.wrap
import java.util.*
import kotlin.math.abs

typealias InputStream = java.io.InputStream

const val INFINITY = Double.POSITIVE_INFINITY

const val NEG_INFINITY = Double.NEGATIVE_INFINITY

fun <T> Iterable<T>.sumsToOne(valueFn: (T) -> Double): Boolean {
    return abs(sumByDouble(valueFn) - 1) < 0.00001
}

fun Double.isDefined(): Boolean {
    return !isNaN() && (-0.0).compareTo(this) != 0
}

fun <T> iteratorOf(vararg elements: T): Iterator<T> {
    return elements.iterator()
}

sealed class Interval {
    abstract fun compareStart(other: Interval): Int

    abstract fun intersectsWith(other: Interval): Boolean

    abstract fun anchors(): List<Double>
}

data class OpenInterval(val low: Double, val high: Double) : Interval() {
    init {
        require(low.isDefined() && high.isDefined()) { "Low and high should have a defined value" }
        require(high > low) { "High should be > low for open interval" }
    }

    override fun compareStart(other: Interval): Int {
        return when (other) {
            is OpenInterval -> low.compareTo(other.low)
            is PointInterval -> low.compareTo(other.value).let { if (it == 0) 1 else it }
        }
    }

    override fun intersectsWith(other: Interval): Boolean {
        return when (other) {
            is OpenInterval -> low >= other.low && low < other.high || other.low >= low && other.low < high
            is PointInterval -> other.value > low && other.value < high
        }
    }

    override fun anchors(): List<Double> {
        return listOf(low, high)
    }
}

data class PointInterval(val value: Double) : Interval() {
    init {
        require(value.isDefined() && value.isFinite()) { "Point interval value should be defined and finite" }
    }

    override fun compareStart(other: Interval): Int {
        return when (other) {
            is OpenInterval -> value.compareTo(other.low).let { if (it == 0) -1 else it }
            is PointInterval -> value.compareTo(other.value)
        }
    }

    override fun intersectsWith(other: Interval): Boolean {
        return when (other) {
            is OpenInterval -> value > other.low && value < other.high
            is PointInterval -> value == other.value
        }
    }

    override fun anchors(): List<Double> {
        return listOf(value)
    }
}

fun Iterable<Interval>.isSortedByStart(): Boolean {
    return zipWithNext { a, b -> a.compareStart(b) <= 0 }.all { it }
}

fun Iterable<Interval>.isDisjoint(allowConnected: Boolean = true): Boolean {
    val map = TreeMap<Double, MutableList<Interval>>()
    for (interval in this) {
        for (anchor in interval.anchors()) {
            val closestIntervals = mutableSetOf<Interval>()
            map.floorEntry(anchor)?.value?.let { closestIntervals.addAll(it) }
            map.ceilingEntry(anchor)?.value?.let { closestIntervals.addAll(it) }
            if (closestIntervals.any { interval.intersectsWith(it) }) {
                return false
            }
        }
        interval.anchors().forEach { map.getOrPut(it, { mutableListOf() }).add(interval) }
    }
    return allowConnected || map.values.none { it.size == 3 }
}

fun Iterable<Interval>.isDisconnected(): Boolean {
    return isDisjoint(false)
}

fun <T> Iterable<T>.isSortedByStart(intervalsFn: (T) -> Iterable<Interval>): Boolean {
    return map { intervalsFn(it).first() }.isSortedByStart()
}

fun <T> Iterable<T>.isDisjoint(intervalsFn: (T) -> Iterable<Interval>): Boolean {
    return flatMap { intervalsFn(it) }.isDisjoint()
}

class ByteString(bytes: ByteArray = EMPTY_BYTE_ARRAY) : Iterable<Byte> {
    private val bytes = if (bytes.isNotEmpty()) bytes.copyOf() else EMPTY_BYTE_ARRAY

    fun byteAt(index: Int): Byte {
        return bytes[index]
    }

    fun size(): Int {
        return bytes.size
    }

    fun toByteArray(): ByteArray {
        return bytes.copyOf()
    }

    fun asString(): String {
        return Charsets.UTF_8.newDecoder().decode(wrap(bytes)).toString()
    }

    override fun equals(other: Any?): Boolean {
        return this === other || (javaClass == other?.javaClass && bytes.contentEquals((other as ByteString).bytes))
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun iterator(): ByteIterator {
        return bytes.iterator()
    }

    override fun toString(): String {
        return bytes.joinToString(" ") { "%02x".format(it) }
    }
}

private val EMPTY_BYTE_ARRAY = ByteArray(0)
