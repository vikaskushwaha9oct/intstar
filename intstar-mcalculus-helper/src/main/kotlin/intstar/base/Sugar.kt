package intstar.base

import intstar.mcalculus.*

fun b(x: String): ByteString {
    return ByteString(x.toByteArray())
}

fun b(vararg x: Int): ByteString {
    return ByteString(x.map { it.toByte() }.toByteArray())
}

data class MeasureComparison(val left: DerivedMeasure, val comparison: Comparison, val right: Measure)

infix fun String.ms(x: String): DerivedMeasure {
    return DerivedMeasure(IdEntityConcept(this), IdEntityConcept(x))
}

infix fun ByteString.ms(x: String): DerivedMeasure {
    return DerivedMeasure(ByteEntityConcept(this), IdEntityConcept(x))
}

infix fun RelationConcept.ms(x: String): DerivedMeasure {
    return DerivedMeasure(this, IdEntityConcept(x))
}

infix fun String.rel(x: String): RelationConcept {
    return RelationConcept(IdEntityConcept(this), IdEntityConcept(x))
}

infix fun ByteString.rel(x: String): RelationConcept {
    return RelationConcept(ByteEntityConcept(this), IdEntityConcept(x))
}

infix fun String.rel(x: ByteString): RelationConcept {
    return RelationConcept(IdEntityConcept(this), ByteEntityConcept(x))
}

infix fun ByteString.rel(x: ByteString): RelationConcept {
    return RelationConcept(ByteEntityConcept(this), ByteEntityConcept(x))
}

infix fun DerivedMeasure.eq(x: Double): MeasureComparison {
    return MeasureComparison(this, Comparison.EQUALS, ConstantMeasure(x))
}

infix fun DerivedMeasure.eq(x: Measure): MeasureComparison {
    return MeasureComparison(this, Comparison.EQUALS, x)
}

infix fun DerivedMeasure.lt(x: Double): MeasureComparison {
    return MeasureComparison(this, Comparison.LESS_THAN, ConstantMeasure(x))
}

infix fun DerivedMeasure.lt(x: Measure): MeasureComparison {
    return MeasureComparison(this, Comparison.LESS_THAN, x)
}

infix fun DerivedMeasure.gt(x: Double): MeasureComparison {
    return MeasureComparison(this, Comparison.GREATER_THAN, ConstantMeasure(x))
}

infix fun DerivedMeasure.gt(x: Measure): MeasureComparison {
    return MeasureComparison(this, Comparison.GREATER_THAN, x)
}

infix fun DerivedMeasure.lte(x: Double): MeasureComparison {
    return MeasureComparison(this, Comparison.LESS_THAN_EQUALS, ConstantMeasure(x))
}

infix fun DerivedMeasure.lte(x: Measure): MeasureComparison {
    return MeasureComparison(this, Comparison.LESS_THAN_EQUALS, x)
}

infix fun DerivedMeasure.gte(x: Double): MeasureComparison {
    return MeasureComparison(this, Comparison.GREATER_THAN_EQUALS, ConstantMeasure(x))
}

infix fun DerivedMeasure.gte(x: Measure): MeasureComparison {
    return MeasureComparison(this, Comparison.GREATER_THAN_EQUALS, x)
}

infix fun MeasureComparison.with(confidence: List<ConfidenceValue>): Measurement {
    return Measurement(left, comparison, right, confidence)
}

fun conf(vararg x: ConfidenceValue): List<ConfidenceValue> {
    return x.toList()
}

data class Intervals(val value: List<Interval>)

data class IncompleteIntervals(val value: List<Interval>, val incomplete: Double)

infix fun Double.to(x: Double): Intervals {
    return Intervals(mutableListOf(OpenInterval(this, x)))
}

infix fun Double.and(x: Double): IncompleteIntervals {
    return IncompleteIntervals(mutableListOf(PointInterval(this)), x)
}

infix fun Double.at(x: Double): ConfidenceValue {
    return ConfidenceValue(listOf(PointInterval(this)), x)
}

infix fun IncompleteIntervals.to(x: Double): Intervals {
    return Intervals(value + OpenInterval(incomplete, x))
}

infix fun IncompleteIntervals.and(x: Double): IncompleteIntervals {
    return IncompleteIntervals(value + PointInterval(incomplete), x)
}

infix fun IncompleteIntervals.at(x: Double): ConfidenceValue {
    return ConfidenceValue(value + PointInterval(incomplete), x)
}

infix fun Intervals.and(x: Double): IncompleteIntervals {
    return IncompleteIntervals(value, x)
}

infix fun Intervals.at(x: Double): ConfidenceValue {
    return ConfidenceValue(value, x)
}

val Measure.value: Double?
    get() = (this as? ConstantMeasure)?.value

val Measure.measurable: IdEntityConcept?
    get() = (this as? DerivedMeasure)?.measurable

val Measure.concept: Concept?
    get() = (this as? DerivedMeasure)?.concept

val Concept.id: String?
    get() = (this as? IdEntityConcept)?.value

val Concept.left: EntityConcept?
    get() = (this as? RelationConcept)?.left

val Concept.right: EntityConcept?
    get() = (this as? RelationConcept)?.right

val Concept.bstr: ByteString?
    get() = (this as? ByteEntityConcept)?.value
