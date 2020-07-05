package intstar.mcalculus

import kotlin.Double.Companion.NEGATIVE_INFINITY
import kotlin.Double.Companion.POSITIVE_INFINITY

data class Measurement(val left: Measure, val comparison: Comparison, val right: Measure, val confidence: Confidence)

data class Confidence(val intervals: List<ConfidenceInterval>)

data class ConfidenceInterval(val low: Double, val high: Double, val confidence: Double)

enum class Comparison {
    EQUALS,
    LESS_THAN,
    GREATER_THAN,
    LESS_THAN_EQUALS,
    GREATER_THAN_EQUALS
}

interface Measure

data class ConstantMeasure(val value: Double) : Measure

data class DerivedMeasure(val concept: Concept, val measurable: IdEntityConcept) : Measure

interface Concept

data class RelationConcept(val left: EntityConcept, val right: EntityConcept) : Concept

interface EntityConcept : Concept

data class IdEntityConcept(val id: String) : EntityConcept

data class ByteEntityConcept(val value: List<Byte>) : EntityConcept

val TRUE = Confidence(listOf(ConfidenceInterval(0.0, 0.0, 1.0)))

val INVALID = Confidence(listOf(ConfidenceInterval(NEGATIVE_INFINITY, POSITIVE_INFINITY, 1.0)))

val ENTITY = IdEntityConcept("$")

val FOCUS = IdEntityConcept("*")

val MANIFEST = IdEntityConcept("@")
