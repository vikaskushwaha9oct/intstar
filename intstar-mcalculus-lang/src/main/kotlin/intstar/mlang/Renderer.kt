package intstar.mlang

import intstar.mcalculus.*
import java.nio.charset.Charset

fun Iterator<Measurement>.renderMLang(): String {
    return MLangRenderer().render(this).use { it.readBytes().toString(Charsets.UTF_8) }
}

class MLangRenderer(
        private val charset: Charset = Charsets.UTF_8,
        private val newline: String = "\n",
        private val doubleToLiteralFn: (Double) -> String = Double::toString
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

private fun Measurement.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    val leftLiteral = left.toLiteral(doubleToLiteralFn)
    val rightLiteral = right.toLiteral(doubleToLiteralFn)
    val confidenceLiteral = confidence.joinToString(" ") { it.toLiteral(doubleToLiteralFn) }
    return "%s %s %s %s;".format(leftLiteral, comparison.symbol, rightLiteral, confidenceLiteral)
}

private fun ConfidenceValue.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    val intervalsLiteral = intervals.joinToString(" ~ ") { it.toLiteral(doubleToLiteralFn) }
    return "[%s %% %s]".format(intervalsLiteral, doubleToLiteralFn(value))
}

private fun Interval.toLiteral(doubleToLiteralFn: (Double) -> String): String {
    return when (this) {
        is PointInterval -> doubleToLiteralFn(value)
        is OpenInterval -> "%s : %s".format(doubleToLiteralFn(low), doubleToLiteralFn(high))
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
        is MeasurementEntityConcept -> throw UnsupportedOperationException()
    }
}

private fun String.toLiteral(): String {
    return if (isNotEmpty() && all { it.isMLangValid() && !it.isMLangDelimiter() }) this
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
            else -> if (!char.isMLangValid()) {
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
