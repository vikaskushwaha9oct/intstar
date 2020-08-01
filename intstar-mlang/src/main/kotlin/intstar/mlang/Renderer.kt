package intstar.mlang

import intstar.mcalculus.ByteEntityConcept
import intstar.mcalculus.ByteString
import intstar.mcalculus.Concept
import intstar.mcalculus.ConfidenceValue
import intstar.mcalculus.ConstantMeasure
import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.EntityConcept
import intstar.mcalculus.IdEntityConcept
import intstar.mcalculus.InputStream
import intstar.mcalculus.Interval
import intstar.mcalculus.LanguageRenderer
import intstar.mcalculus.Measure
import intstar.mcalculus.Measurement
import intstar.mcalculus.OpenInterval
import intstar.mcalculus.PointInterval
import intstar.mcalculus.RelationConcept
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.min

fun Iterator<Measurement>.renderMLang(): String {
    return MLangRenderer().render(this).use { it.readBytes().toString(Charsets.UTF_8) }
}

class MLangRenderer(
    private val charset: Charset = Charsets.UTF_8,
    private val newline: String = "\n",
    private val doubleToLiteralFn: (Double) -> String = { it.toLiteral(2) }
) : LanguageRenderer {
    override fun render(measurements: Iterator<Measurement>): InputStream {
        val mLiterals = measurements.asSequence().map { it.toLiteral(doubleToLiteralFn) }.iterator()
        return object : InputStream() {
            var bytes: ByteArray = byteArrayOf()
            var index = 0

            override fun read(): Int {
                if (index == bytes.size) {
                    if (!mLiterals.hasNext()) {
                        return -1
                    }
                    bytes = "%s%s".format(if (bytes.isEmpty()) "" else newline, mLiterals.next()).toByteArray(charset)
                    index = 0
                }
                val byte = bytes[index].toInt()
                index += 1
                return byte
            }

            override fun available(): Int {
                return bytes.size - index
            }
        }
    }
}

fun Double.toLiteral(maxPrecision: Int): String {
    val str = toString()
    val dotIndex = str.indexOf('.')
    if (dotIndex >= 0) {
        val integerPart = str.substring(0, dotIndex)
        val eIndex = str.indexOf('E').let { if (it < 0) str.length else it }
        val exponentPart = str.substring(eIndex)
        val fractionalPart = str.substring(dotIndex, eIndex).let {
            val trimmed = it.substring(0, min(it.length, max(maxPrecision, 1) + 1)).trimEnd('0')
            if (trimmed.length > 1) trimmed else ""
        }
        return "$integerPart$fractionalPart$exponentPart"
    }
    return str
}

private fun Measurement.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    val leftLiteral = left.toLiteral(doubleToLiteralFn)
    val rightLiteral = right.toLiteral(doubleToLiteralFn)
    val confidenceLiteral = confidence.joinToString(" ") { it.toLiteral(doubleToLiteralFn) }
    return "%s %s %s %s;".format(leftLiteral, comparison.symbol, rightLiteral, confidenceLiteral)
}

private fun ConfidenceValue.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    val intervalsLiteral = intervals.joinToString(" ~ ") { it.toLiteral(doubleToLiteralFn) }
    return "[%s = %s]".format(intervalsLiteral, doubleToLiteralFn(value))
}

private fun Interval.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    return when (this) {
        is PointInterval -> doubleToLiteralFn(value)
        is OpenInterval -> "%s:%s".format(
            if (low.isFinite()) "${doubleToLiteralFn(low)} " else "",
            if (high.isFinite()) " ${doubleToLiteralFn(high)}" else ""
        )
    }
}

private fun Measure.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    return when (this) {
        is ConstantMeasure -> doubleToLiteralFn(value)
        is DerivedMeasure -> "%s : %s".format(concept.toLiteral(), measurable.value.toLiteral())
    }
}

private fun Concept.toLiteral(): String {
    return when (this) {
        is EntityConcept -> toLiteral()
        is RelationConcept -> "%s ~ %s".format(left.toLiteral(), right.toLiteral())
    }
}

private fun EntityConcept.toLiteral(): String {
    return when (this) {
        is IdEntityConcept -> value.toLiteral()
        is ByteEntityConcept -> value.toLiteral()
    }
}

private fun String.toLiteral(): String {
    return if (none { it.isWhitespace() || it.isISOControl() || it in "{}[]();,:~<>='`\"" }) this
    else toLiteral('`')
}

private fun ByteString.toLiteral(): String {
    val string = tryOrNull { asString() }
    if (string != null) {
        val quote = if (string.sumBy { if (it == '\'') 1 else if (it == '"') -1 else 0 } >= 0) '"' else '\''
        return string.toLiteral(quote)
    }
    return "(%s)".format(this)
}

private inline fun <T> tryOrNull(expr: () -> T): T? {
    return try {
        expr()
    } catch (t: Throwable) {
        null
    }
}

private fun String.toLiteral(quote: Char): String {
    val builder = StringBuilder().append(quote)
    for (char in this) {
        when (char) {
            '\n' -> builder.append("\\n")
            '\t' -> builder.append("\\t")
            '\r' -> builder.append("\\r")
            '\\' -> builder.append("\\\\")
            quote -> builder.append("\\" + quote)
            else -> if (char != ' ' && (char.isWhitespace() || char.isISOControl())) {
                val hex = char.toInt().toString(16)
                val pad = if (hex.length <= 2) 2 else 4
                val escape = if (pad == 2) 'x' else 'u'
                builder.append("\\" + escape + hex.padStart(pad, '0'))
            } else {
                builder.append(char)
            }
        }
    }
    return builder.append(quote).toString()
}
