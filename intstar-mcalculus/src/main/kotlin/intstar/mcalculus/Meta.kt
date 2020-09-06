package intstar.mcalculus

val TRUE = listOf(ConfidenceValue(listOf(PointInterval(0.0)), 1.0))

val FALSE = listOf(ConfidenceValue(listOf(OpenInterval(NEG_INFINITY, 0.0), OpenInterval(0.0, INFINITY)), 1.0))

val UNKNOWN = listOf(ConfidenceValue(listOf(OpenInterval(NEG_INFINITY, INFINITY)), 1.0))

const val AGENT = "$"

const val FOCUS = "*"

const val MANIFEST = "@"
