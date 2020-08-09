package intstar.mlang

import intstar.mcalculus.ByteEntityConcept
import intstar.mcalculus.ByteString
import intstar.mcalculus.Comparison
import intstar.mcalculus.Concept
import intstar.mcalculus.ConfidenceValue
import intstar.mcalculus.ConstantMeasure
import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.EntityConcept
import intstar.mcalculus.IdEntityConcept
import intstar.mcalculus.Interval
import intstar.mcalculus.LanguageParser
import intstar.mcalculus.Measure
import intstar.mcalculus.Measurement
import intstar.mcalculus.OpenInterval
import intstar.mcalculus.PointInterval
import intstar.mcalculus.RelationConcept
import intstar.mcalculus.TRUE
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Character.toChars
import java.nio.charset.Charset

fun String.parseMLang(): Iterator<Measurement> {
    return MLangParser().parse(ByteArrayInputStream(toByteArray()))
}

class MLangParser(private val charset: Charset = Charsets.UTF_8) : LanguageParser {
    override fun parse(stream: InputStream): Iterator<Measurement> {
        return stream.parseChars(charset).parseTokens().parseMeasurements().iterator()
    }
}

class ParseException(message: String, val context: ParsedContext? = null) :
    Exception(message + if (context != null) "\n" + context else "")

data class ParsedContext(val text: String, val lineNo: Int, val colNo: Int) {
    override fun toString(): String {
        return "[$lineNo:$colNo] $text"
    }
}

private fun InputStream.parseChars(charset: Charset): Sequence<ParsedChar> {
    return sequence {
        var lineNo = 1
        for (line in BufferedReader(InputStreamReader(this@parseChars, charset)).lines()) {
            var colNo = 1
            for (char in line) {
                if (!char.isMLangValid()) {
                    val context = ParsedContext("$char", lineNo, colNo)
                    throw ParseException("Invalid char \\u" + char.toInt().toString(16), context)
                }
                yield(ParsedChar(char, lineNo, colNo))
                colNo += 1
            }
            yield(ParsedChar('\n', lineNo, colNo))
            lineNo += 1
        }
    }
}

private fun Sequence<ParsedChar>.parseTokens(): Sequence<Token> {
    return sequence {
        val builder = mutableListOf<ParsedChar>()
        var tokenType: TokenType? = null

        for (parsedChar in this@parseTokens) {
            if (tokenType != null && tokenType.isStop(parsedChar.char, builder)) {
                val tokenText = tokenType.createText(builder)
                val startChar = builder.first()
                yield(Token(tokenText, tokenType, startChar.lineNo, startChar.colNo))
                builder.clear()
                tokenType = null
            }

            if (tokenType == null && !parsedChar.char.isMLangSpace()) {
                tokenType = TokenType.values().find { it.isStart(parsedChar.char) }
            }

            if (tokenType != null) {
                builder.add(parsedChar)
            }
        }
    }
}

private fun Sequence<Token>.parseMeasurements(): Sequence<Measurement> {
    return sequence {
        var node = BraceNode(BraceNodeType.AND)

        for (token in this@parseMeasurements) {
            if (token.type == TokenType.CONTROL && token.text == ";") {
                yieldAll(node.toMeasurements())
                node = BraceNode(BraceNodeType.AND)
            } else {
                node = node.nextWriteNode(token)
                node.tokens.add(token)
            }
        }
        yieldAll(node.toMeasurements())
    }
}

private fun parseException(message: String, token: Token): ParseException {
    return ParseException(message, ParsedContext(token.text, token.lineNo, token.colNo))
}

private class BraceNode(val type: BraceNodeType, val parent: BraceNode? = null) {
    val tokens = mutableListOf<Token>()
    val children = mutableListOf<BraceNode>()

    init {
        parent?.children?.add(this)
    }

    fun nextWriteNode(token: Token): BraceNode {
        try {
            return if (token.type == TokenType.CONTROL) {
                when (token.text) {
                    "{" -> type.nextWriteNodeForBraceStart(this)
                    "}" -> type.nextWriteNodeForBraceEnd(this)
                    "," -> type.nextWriteNodeForComma(this)
                    else -> type.nextWriteNodeForOthers(this)
                }
            } else type.nextWriteNodeForOthers(this)
        } catch (e: ParseException) {
            throw parseException(e.message!!, token)
        }
    }

    fun toMeasurements(): List<Measurement> {
        validateBrackets()
        return allTokenPaths().map { MBuilder(it).buildMeasurement() }
    }

    private fun validateBrackets() {
        children.forEach { it.validateBrackets() }
        if (tokens.isNotEmpty()) {
            var prev = '\n'
            for (token in tokens) {
                if (token.type == TokenType.CONTROL && token.text[0].isMLangBracket()) {
                    val curr = token.text[0]
                    if (curr.isMLangBracketOpen()) {
                        if (prev.isMLangBracketOpen()) {
                            throw parseException("Expected close of $prev first", token)
                        }
                    } else {
                        if (prev == '\n') {
                            throw parseException("Non matching $curr", token)
                        }
                        if (!curr.isBracketCloseCounterpart(prev)) {
                            throw parseException("Expected close of $prev first", token)
                        }
                    }
                    prev = curr
                }
            }
            if (prev.isMLangBracketOpen()) {
                throw parseException("Expected close of $prev first", tokens.last())
            }
        }
    }

    fun nearestAncestor(predicate: (BraceNode) -> Boolean): BraceNode? {
        return if (parent == null || predicate(parent)) parent else parent.nearestAncestor(predicate)
    }

    fun allTokenPaths(): List<List<Token>> {
        return when (type) {
            BraceNodeType.OR -> children.flatMap { it.allTokenPaths() }
            BraceNodeType.AND -> children.map { it.allTokenPaths() }.cartesianProduct().map { it.flatten() }
            else -> listOf(tokens)
        }
    }
}

private class MBuilder(val tokens: List<Token>) {
    var index: Int = 0

    fun buildMeasurement(): Measurement {
        try {
            return Measurement(buildLeftMeasure(), buildComparison(), buildRightMeasure(), buildConfidence())
        } catch (e: IllegalArgumentException) {
            throw parseException(e.message!!, tokens.last())
        }
    }

    private fun buildLeftMeasure(): Measure {
        return if (isFollowedByComparison()) buildConstantMeasure() else buildDerivedMeasure()
    }

    private fun buildRightMeasure(): Measure {
        return if (isFollowedByConfidence()) buildConstantMeasure() else buildDerivedMeasure()
    }

    private fun buildConstantMeasure(): ConstantMeasure {
        return ConstantMeasure(buildNumber())
    }

    private fun buildDerivedMeasure(): DerivedMeasure {
        var concept: Concept = buildEntityConcept()
        if (isControl('~')) {
            index += 1
            val otherConcept = buildEntityConcept()
            concept = RelationConcept(concept as EntityConcept, otherConcept)
        }
        if (!isControl(':')) {
            throw parseException("Expected a :")
        }
        index += 1
        return DerivedMeasure(concept, buildIdEntityConcept())
    }

    private fun buildEntityConcept(): EntityConcept {
        return if (isIdEntity()) buildIdEntityConcept() else buildByteEntityConcept()
    }

    private fun buildIdEntityConcept(): IdEntityConcept {
        val id = if (isIdEntity()) tokens[index].text else null
        if (id != null) {
            index += 1
            return IdEntityConcept(id)
        }
        throw parseException("Expected a identifier")
    }

    private fun buildByteEntityConcept(): ByteEntityConcept {
        if (isControl('(')) {
            index += 1
            val value = ByteArrayOutputStream()
            while (!isControl(')')) {
                value.write(buildByte())
            }
            index += 1
            return ByteEntityConcept(ByteString(value.toByteArray()))
        } else if (isQuotedString()) {
            val value = tokens[index].text
            index += 1
            return ByteEntityConcept(ByteString(value.toByteArray()))
        }
        throw parseException("Expected a ( or string literal")
    }

    private fun buildByte(): Int {
        endOfTokensCheck()
        val value = with(tokens[index]) { if (type == TokenType.NO_QUOTE_TEXT) text.toInt(16) else null }
        if (value != null && value >= 0 && value <= 255) {
            index += 1
            return value
        }
        throw parseException("Expected a hex based byte")
    }

    private fun buildComparison(): Comparison {
        endOfTokensCheck()
        val comparison = with(tokens[index]) { if (type == TokenType.COMPARISON) text else null }
        if (comparison != null) {
            index += 1
            return Comparison.values().find { it.symbol == comparison }!!
        }
        throw parseException("Expected a comparison")
    }

    private fun buildConfidence(): List<ConfidenceValue> {
        if (index > tokens.lastIndex) {
            return TRUE
        }
        val confidence = mutableListOf<ConfidenceValue>()
        while (isControl('[')) {
            index += 1
            confidence.add(buildConfidenceValue())
            if (!isControl(']')) {
                throw parseException("Expected a ]")
            }
            index += 1
        }
        if (index <= tokens.lastIndex) {
            throw parseException("Extraneous trailing tokens")
        }
        return confidence
    }

    private fun buildConfidenceValue(): ConfidenceValue {
        val intervals = mutableListOf<Interval>()
        intervals.add(buildConfidenceInterval())
        while (isControl('~')) {
            index += 1
            intervals.add(buildConfidenceInterval())
        }
        if (!isControl('%')) {
            throw parseException("Expected a %")
        }
        index += 1
        return ConfidenceValue(intervals, buildNumber())
    }

    private fun buildConfidenceInterval(): Interval {
        val first = buildNumber()
        if (isControl('%') || isControl('~')) {
            return PointInterval(first)
        }
        if (!isControl(':')) {
            throw parseException("Expected a :")
        }
        index += 1
        return OpenInterval(first, buildNumber())
    }

    private fun buildNumber(): Double {
        endOfTokensCheck()
        val value = with(tokens[index]) { if (type == TokenType.NO_QUOTE_TEXT) text.toDoubleOrNull() else null }
        if (value != null) {
            index += 1
            return value
        }
        throw parseException("Expected a number")
    }

    private fun endOfTokensCheck() {
        if (index > tokens.lastIndex) {
            throw parseException("Reached end of tokens before completing measurement", tokens.last())
        }
    }

    private fun parseException(message: String): ParseException {
        endOfTokensCheck()
        return parseException(message, tokens[index])
    }

    private fun isFollowedByComparison(): Boolean {
        return index < tokens.lastIndex && tokens[index + 1].type == TokenType.COMPARISON
    }

    private fun isFollowedByConfidence(): Boolean {
        return index == tokens.lastIndex ||
                index < tokens.lastIndex && with(tokens[index + 1]) { type == TokenType.CONTROL && text[0] == '[' }
    }

    private fun isControl(char: Char): Boolean {
        return index <= tokens.lastIndex && with(tokens[index]) { type == TokenType.CONTROL && text[0] == char }
    }

    private fun isIdEntity(): Boolean {
        return index <= tokens.lastIndex &&
                with(tokens[index]) { type == TokenType.NO_QUOTE_TEXT || type == TokenType.BACK_QUOTE_TEXT }
    }

    private fun isQuotedString(): Boolean {
        return index <= tokens.lastIndex && tokens[index].type == TokenType.NORMAL_QUOTE_TEXT
    }
}

private fun <T> List<List<T>>.cartesianProduct(): List<List<T>> {
    return if (isEmpty()) emptyList()
    else drop(1).fold(first().map { listOf(it) }) { pro, x -> pro.flatMap { list -> x.map { list + it } } }
}

private enum class BraceNodeType {
    OR {
        override fun nextWriteNodeForBraceStart(node: BraceNode): BraceNode {
            return if (isClosed(node)) SIMPLE.nextWriteNodeForBraceStart(node)
            else AND.nextWriteNodeForBraceStart(BraceNode(AND, node))
        }

        override fun nextWriteNodeForBraceEnd(node: BraceNode): BraceNode {
            return if (isClosed(node)) SIMPLE.nextWriteNodeForBraceEnd(node)
            else throw ParseException("Cannot have a } right after a " + node.tokens.last().text)
        }

        override fun nextWriteNodeForComma(node: BraceNode): BraceNode {
            return when {
                isClosed(node) -> SIMPLE.nextWriteNodeForComma(node)
                node.tokens.last().text == "{" -> SIMPLE.nextWriteNodeForComma(BraceNode(SIMPLE, node))
                else -> throw ParseException("Cannot have a , right after a ,")
            }
        }

        override fun nextWriteNodeForOthers(node: BraceNode): BraceNode {
            return if (isClosed(node)) SIMPLE.nextWriteNodeForOthers(node)
            else AND.nextWriteNodeForOthers(BraceNode(AND, node))
        }

        fun isClosed(node: BraceNode): Boolean {
            return node.tokens.last().text == "}"
        }
    },
    AND {
        override fun nextWriteNodeForBraceStart(node: BraceNode): BraceNode {
            return BraceNode(OR, node)
        }

        override fun nextWriteNodeForBraceEnd(node: BraceNode): BraceNode {
            throw ParseException("Cannot have a } at the beginning")
        }

        override fun nextWriteNodeForComma(node: BraceNode): BraceNode {
            throw ParseException("Cannot have a , at the beginning")
        }

        override fun nextWriteNodeForOthers(node: BraceNode): BraceNode {
            return BraceNode(SIMPLE, node)
        }
    },
    SIMPLE {
        override fun nextWriteNodeForBraceStart(node: BraceNode): BraceNode {
            return AND.nextWriteNodeForBraceStart(node.nearestAncestor { it.type == AND }!!)
        }

        override fun nextWriteNodeForBraceEnd(node: BraceNode): BraceNode {
            val orNode = node.nearestAncestor { it.type == OR } ?: throw ParseException("Non matching }")
            return OR.nextWriteNodeForBraceEnd(orNode)
        }

        override fun nextWriteNodeForComma(node: BraceNode): BraceNode {
            val orNode = node.nearestAncestor { it.type == OR } ?: throw ParseException("No enclosing {} found for ,")
            return OR.nextWriteNodeForComma(orNode)
        }

        override fun nextWriteNodeForOthers(node: BraceNode): BraceNode {
            return node
        }

    };

    abstract fun nextWriteNodeForBraceStart(node: BraceNode): BraceNode

    abstract fun nextWriteNodeForBraceEnd(node: BraceNode): BraceNode

    abstract fun nextWriteNodeForComma(node: BraceNode): BraceNode

    abstract fun nextWriteNodeForOthers(node: BraceNode): BraceNode
}

private data class ParsedChar(val char: Char, val lineNo: Int, val colNo: Int)

private data class Token(val text: String, val type: TokenType, val lineNo: Int, val colNo: Int)

private sealed class TextOrError

private data class TextResult(val text: String) : TextOrError()

private data class TextError(val msg: String, val index: Int) : TextOrError()

private enum class TokenType {
    COMPARISON {
        override fun isStart(char: Char): Boolean {
            return char.isMLangComparison()
        }

        override fun isStop(char: Char, builder: List<ParsedChar>): Boolean {
            return builder.size == 2 || builder.first().char == '=' || char != '='
        }
    },
    CONTROL {
        override fun isStart(char: Char): Boolean {
            return char.isMLangControl()
        }

        override fun isStop(char: Char, builder: List<ParsedChar>): Boolean {
            return true
        }
    },
    BACK_QUOTE_TEXT {
        override fun isStart(char: Char): Boolean {
            return char.isMLangBackQuote()
        }

        override fun isStop(char: Char, builder: List<ParsedChar>): Boolean {
            return NORMAL_QUOTE_TEXT.isStop(char, builder)
        }
    },
    NORMAL_QUOTE_TEXT {
        override fun isStart(char: Char): Boolean {
            return char.isMLangQuote()
        }

        override fun isStop(char: Char, builder: List<ParsedChar>): Boolean {
            return char == '\n' ||
                    builder.size > 1 && builder.first().char == builder.last().char && isLastCharUnescaped(builder)
        }

        fun isLastCharUnescaped(builder: List<ParsedChar>): Boolean {
            for (i in builder.lastIndex - 1 downTo 0) {
                if (builder[i].char != '\\') {
                    return (builder.size - i) % 2 == 0
                }
            }
            return false
        }
    },
    NO_QUOTE_TEXT {
        override fun isStart(char: Char): Boolean {
            return true
        }

        override fun isStop(char: Char, builder: List<ParsedChar>): Boolean {
            return char.isMLangDelimiter()
        }
    };

    abstract fun isStart(char: Char): Boolean

    abstract fun isStop(char: Char, builder: List<ParsedChar>): Boolean

    fun createText(builder: List<ParsedChar>): String {
        val tokenText = builder.joinToString("") { it.char.toString() }
        if (this == BACK_QUOTE_TEXT || this == NORMAL_QUOTE_TEXT) {
            val lineNo = builder.first().lineNo
            if (tokenText.length < 2 && tokenText.first() != tokenText.last()) {
                val context = ParsedContext(tokenText, lineNo, builder.last().colNo)
                throw ParseException("Missing ending quote " + tokenText.first(), context)
            }
            return when (val textOrError = tokenText.substring(1, tokenText.lastIndex).unescape(tokenText.first())) {
                is TextResult -> textOrError.text
                is TextError -> {
                    val context = ParsedContext(tokenText, lineNo, builder.first().colNo + textOrError.index)
                    throw ParseException(textOrError.msg, context)
                }
            }
        }
        return tokenText
    }
}

private fun String.unescape(quoteChar: Char): TextOrError {
    val builder = StringBuilder(this.length)
    var i = 0
    while (i < this.length) {
        when {
            this[i] != '\\' -> {
                if (this[i] == '\t') {
                    return TextError("Tab literal not allowed in quoted text", i)
                }
                builder.append(this[i])
            }
            this[i + 1] == 'x' -> {
                if (i + 3 < this.length) {
                    val code = String(charArrayOf(this[i + 2], this[i + 3]))
                    try {
                        builder.append(toChars(code.toInt(16)))
                    } catch (e: NumberFormatException) {
                        return TextError("Ascii escape seq should be composed of 2 hexadecimal digits", i)
                    } catch (e: IllegalArgumentException) {
                        return TextError("Invalid ascii code point $code", i)
                    }
                } else {
                    return TextError("Ascii escape seq should have 2 digits", i)
                }
                i += 3
            }
            this[i + 1] == 'u' -> {
                if (i + 5 < this.length) {
                    val code = String(charArrayOf(this[i + 2], this[i + 3], this[i + 4], this[i + 5]))
                    try {
                        builder.append(toChars(code.toInt(16)))
                    } catch (e: NumberFormatException) {
                        return TextError("Unicode escape seq should be composed of 4 hexadecimal digits", i)
                    } catch (e: IllegalArgumentException) {
                        return TextError("Invalid unicode code point $code", i)
                    }
                } else {
                    return TextError("Unicode escape seq should have 4 digits", i)
                }
                i += 5
            }
            else -> {
                val ch = when (this[i + 1]) {
                    '\\' -> '\\'
                    'n' -> '\n'
                    'r' -> '\r'
                    't' -> '\t'
                    quoteChar -> quoteChar
                    else -> return TextError("Invalid escape seq \\" + this[i + 1], i)
                }
                builder.append(ch)
                i += 1
            }
        }
        i += 1
    }
    return TextResult(builder.toString())
}
