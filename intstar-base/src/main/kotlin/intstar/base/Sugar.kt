package intstar.base

import intstar.mcalculus.*

fun b(x: String): ByteString {
    return ByteString(x.toByteArray())
}

fun b(vararg x: Int): ByteString {
    return ByteString(x.map { it.toByte() }.toByteArray())
}

data class MeasuresWithComparison(val left: DerivedMeasure, val comparison: Comparison, val right: Measure)

infix fun String.ms(id: String): DerivedMeasure {
    return DerivedMeasure(IdEntityConcept(this), IdEntityConcept(id))
}

infix fun ByteString.ms(id: String): DerivedMeasure {
    return DerivedMeasure(ByteEntityConcept(this), IdEntityConcept(id))
}

infix fun Concept.ms(id: String): DerivedMeasure {
    return DerivedMeasure(this, IdEntityConcept(id))
}

infix fun Concept.ms(x: IdEntityConcept): DerivedMeasure {
    return DerivedMeasure(this, x)
}

infix fun String.ms(x: IdEntityConcept): DerivedMeasure {
    return DerivedMeasure(IdEntityConcept(this), x)
}

infix fun ByteString.ms(x: IdEntityConcept): DerivedMeasure {
    return DerivedMeasure(ByteEntityConcept(this), x)
}

infix fun String.rel(id: String): RelationConcept {
    return RelationConcept(IdEntityConcept(this), IdEntityConcept(id))
}

infix fun ByteString.rel(id: String): RelationConcept {
    return RelationConcept(ByteEntityConcept(this), IdEntityConcept(id))
}

infix fun String.rel(bstr: ByteString): RelationConcept {
    return RelationConcept(IdEntityConcept(this), ByteEntityConcept(bstr))
}

infix fun ByteString.rel(bstr: ByteString): RelationConcept {
    return RelationConcept(ByteEntityConcept(this), ByteEntityConcept(bstr))
}

infix fun EntityConcept.rel(id: String): RelationConcept {
    return RelationConcept(this, IdEntityConcept(id))
}

infix fun EntityConcept.rel(bstr: ByteString): RelationConcept {
    return RelationConcept(this, ByteEntityConcept(bstr))
}

infix fun EntityConcept.rel(x: EntityConcept): RelationConcept {
    return RelationConcept(this, x)
}

infix fun DerivedMeasure.eq(value: Double): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.EQUALS, ConstantMeasure(value))
}

infix fun DerivedMeasure.eq(measure: Measure): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.EQUALS, measure)
}

infix fun DerivedMeasure.lt(value: Double): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.LESS_THAN, ConstantMeasure(value))
}

infix fun DerivedMeasure.lt(measure: Measure): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.LESS_THAN, measure)
}

infix fun DerivedMeasure.gt(value: Double): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.GREATER_THAN, ConstantMeasure(value))
}

infix fun DerivedMeasure.gt(measure: Measure): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.GREATER_THAN, measure)
}

infix fun DerivedMeasure.lte(value: Double): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.LESS_THAN_EQUALS, ConstantMeasure(value))
}

infix fun DerivedMeasure.lte(measure: Measure): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.LESS_THAN_EQUALS, measure)
}

infix fun DerivedMeasure.gte(value: Double): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.GREATER_THAN_EQUALS, ConstantMeasure(value))
}

infix fun DerivedMeasure.gte(measure: Measure): MeasuresWithComparison {
    return MeasuresWithComparison(this, Comparison.GREATER_THAN_EQUALS, measure)
}

infix fun MeasuresWithComparison.with(confidence: List<ConfidenceValue>): Measurement {
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
