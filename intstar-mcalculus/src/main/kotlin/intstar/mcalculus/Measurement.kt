package intstar.mcalculus

data class Measurement(
    val left: Measure,
    val right: Measure,
    val comparison: Comparison,
    val confidence: Confidence? = null
)

data class Confidence(val intervals: Array<ConfidenceInterval>)

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

data class ByteEntityConcept(val value: ByteArray) : EntityConcept
