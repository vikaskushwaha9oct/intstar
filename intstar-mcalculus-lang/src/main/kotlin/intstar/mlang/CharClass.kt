package intstar.mlang

fun Char.isMLangValid(): Boolean {
    return isMLangSpace() || isMLangNonSpace()
}

fun Char.isMLangDelimiter(): Boolean {
    return isMLangSpace() || isMLangControl() || isMLangComparison() || isMLangQuote() || isMLangBackQuote()
}

fun Char.isMLangNonSpace(): Boolean {
    return !isWhitespace() && !isISOControl()
}

fun Char.isMLangSpace(): Boolean {
    return this in " \t\n"
}

fun Char.isMLangComparison(): Boolean {
    return this in "<>="
}

fun Char.isMLangControl(): Boolean {
    return isMLangPunctuation() || isMLangBracket()
}

fun Char.isMLangPunctuation(): Boolean {
    return this in ";,:~%"
}

fun Char.isMLangQuote(): Boolean {
    return this in "'\""
}

fun Char.isMLangBackQuote(): Boolean {
    return this == '`'
}

fun Char.isMLangBracket(): Boolean {
    return isMLangBracketOpen() || isMLangBracketClose()
}

fun Char.isMLangBracketOpen(): Boolean {
    return this in OPEN_BRACKETS
}

fun Char.isMLangBracketClose(): Boolean {
    return this in CLOSE_BRACKETS
}

fun Char.isBracketCloseCounterpart(other: Char): Boolean {
    return OPEN_BRACKETS.indexOf(other) == CLOSE_BRACKETS.indexOf(this)
}

private const val OPEN_BRACKETS = "{[("

private const val CLOSE_BRACKETS = "}])"
