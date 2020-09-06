package intstar.mcalculus

data class Measurement(
        val left: DerivedMeasure,
        val comparison: Comparison,
        val right: Measure,
        val confidence: List<ConfidenceValue>
) {
    init {
        require(confidence.sumsToOne { it.value }) { "Total confidence should be 1" }
        require(confidence.isDisjoint { it.intervals }) { "Intervals should be disjoint" }
        require(confidence.isSortedByStart { it.intervals }) { "Confidence values should be sorted by interval starts" }
    }
}

data class ConfidenceValue(val intervals: List<Interval>, val value: Double) {
    init {
        require(value > 0 && value <= 1) { "Confidence value should be > 0 and <= 1" }
        require(intervals.isNotEmpty()) { "Intervals within a confidence value should not be empty" }
        require(intervals.isDisconnected()) { "Intervals within a confidence value should be disconnected" }
        require(intervals.isSortedByStart()) { "Intervals within a confidence value should be sorted by their starts" }
    }
}

enum class Comparison(val symbol: String) {
    EQUALS("="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_EQUALS("<="),
    GREATER_THAN_EQUALS(">=")
}

sealed class Measure

data class ConstantMeasure(val value: Double) : Measure() {
    init {
        require(value.isDefined()) { "Constant measure should have a defined value" }
    }
}

data class DerivedMeasure(val concept: Concept, val measurable: IdEntityConcept) : Measure()

sealed class Concept

data class RelationConcept(val left: EntityConcept, val right: EntityConcept) : Concept()

sealed class EntityConcept : Concept()

data class IdEntityConcept(val value: String) : EntityConcept()

data class ByteEntityConcept(val value: ByteString) : EntityConcept()
