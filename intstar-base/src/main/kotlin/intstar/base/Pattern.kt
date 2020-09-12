package intstar.base

import intstar.mcalculus.*

data class Var(val value: Int)

fun v(x: Int): Var {
    return Var(x)
}

data class MMatch(val vars: Map<Int, Any>) {
    fun m(x: Int): Measure {
        return vars[x] as Measure
    }

    fun c(x: Int): Concept {
        return vars[x] as Concept
    }

    @Suppress("UNCHECKED_CAST")
    fun cf(x: Int): List<ConfidenceValue> {
        return vars[x] as List<ConfidenceValue>
    }
}

interface MPattern {
    fun match(x: Any): MMatch? {
        val state = mutableMapOf<Int, Any>()
        return if (matchS(x, state)) MMatch(state) else null
    }

    fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean

    fun setS(x: Any, symbol: Var, state: MutableMap<Int, Any>): Boolean {
        val prev = state.put(symbol.value, x)
        return prev == null || prev == x
    }
}

data class MeasurementP(
        val left: DerivedMeasureP,
        val comparison: Comparison,
        val right: MeasureP,
        val confidence: ConfidenceP
) : MPattern {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is Measurement && left.matchS(x.left, state) && comparison == x.comparison &&
                right.matchS(x.right, state) && confidence.matchS(x.confidence, state)
    }
}

sealed class ConfidenceP : MPattern

data class ConfidenceFP(val value: List<ConfidenceValue>) : ConfidenceP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return value == x
    }
}

data class ConfidenceSP(val symbol: Var) : ConfidenceP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return setS(x, symbol, state)
    }
}

sealed class MeasureP : MPattern

data class ConstantMeasureP(val value: Double) : MeasureP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is ConstantMeasure && value == x.value
    }
}

sealed class DerivedMeasureP : MeasureP()

data class DerivedMeasureCP(val concept: ConceptP, val measurable: IdEntityConceptP) : DerivedMeasureP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is DerivedMeasure && concept.matchS(x.concept, state) && measurable.matchS(x.measurable, state)
    }
}

data class DerivedMeasureSP(val symbol: Var) : DerivedMeasureP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is DerivedMeasure && setS(x, symbol, state)
    }
}

data class MeasureSP(val symbol: Var) : MeasureP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is Measure && setS(x, symbol, state)
    }
}

sealed class ConceptP : MPattern

data class RelationConceptP(val left: EntityConceptP, val right: EntityConceptP) : ConceptP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is RelationConcept && left.matchS(x.left, state) && right.matchS(x.right, state)
    }
}

sealed class EntityConceptP : ConceptP()

data class ConceptSP(val symbol: Var) : ConceptP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is Concept && setS(x, symbol, state)
    }
}

sealed class IdEntityConceptP : EntityConceptP()

data class IdEntityConceptFP(val value: String) : IdEntityConceptP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is IdEntityConcept && value == x.value
    }
}

data class IdEntityConceptSP(val symbol: Var) : IdEntityConceptP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is IdEntityConcept && setS(x, symbol, state)
    }
}

data class ByteEntityConceptP(val value: ByteString) : EntityConceptP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is ByteEntityConcept && value == x.value
    }
}

data class EntityConceptSP(val symbol: Var) : EntityConceptP() {
    override fun matchS(x: Any, state: MutableMap<Int, Any>): Boolean {
        return x is EntityConcept && setS(x, symbol, state)
    }
}

data class MeasureComparisonP(val left: DerivedMeasureP, val comparison: Comparison, val right: MeasureP)

fun RelationConcept.asPattern(): RelationConceptP {
    return RelationConceptP(left.asPattern(), right.asPattern())
}

fun EntityConcept.asPattern(): EntityConceptP {
    return when (this) {
        is IdEntityConcept -> IdEntityConceptFP(value)
        is ByteEntityConcept -> ByteEntityConceptP(value)
    }
}

fun Concept.asPattern(): ConceptP {
    return when (this) {
        is RelationConcept -> asPattern()
        is EntityConcept -> asPattern()
    }
}

fun DerivedMeasure.asPattern(): DerivedMeasureP {
    return DerivedMeasureCP(concept.asPattern(), IdEntityConceptFP(measurable.value))
}

fun Measure.asPattern(): MeasureP {
    return when (this) {
        is ConstantMeasure -> ConstantMeasureP(value)
        is DerivedMeasure -> asPattern()
    }
}

fun Measurement.asPattern(): MeasurementP {
    return MeasurementP(left.asPattern(), comparison, right.asPattern(), ConfidenceFP(confidence))
}

infix fun String.ms(x: Var): DerivedMeasureP {
    return DerivedMeasureCP(IdEntityConceptFP(this), IdEntityConceptSP(x))
}

infix fun ByteString.ms(x: Var): DerivedMeasureP {
    return DerivedMeasureCP(ByteEntityConceptP(this), IdEntityConceptSP(x))
}

infix fun Var.ms(x: String): DerivedMeasureP {
    return DerivedMeasureCP(ConceptSP(this), IdEntityConceptFP(x))
}

infix fun Var.ms(x: Var): DerivedMeasureP {
    return DerivedMeasureCP(ConceptSP(this), IdEntityConceptSP(x))
}

infix fun RelationConceptP.ms(x: Var): DerivedMeasureP {
    return DerivedMeasureCP(this, IdEntityConceptSP(x))
}

infix fun RelationConceptP.ms(x: String): DerivedMeasureP {
    return DerivedMeasureCP(this, IdEntityConceptFP(x))
}

infix fun RelationConcept.ms(x: Var): DerivedMeasureP {
    return DerivedMeasureCP(asPattern(), IdEntityConceptSP(x))
}

infix fun String.rel(x: Var): RelationConceptP {
    return RelationConceptP(IdEntityConceptFP(this), EntityConceptSP(x))
}

infix fun ByteString.rel(x: Var): RelationConceptP {
    return RelationConceptP(ByteEntityConceptP(this), EntityConceptSP(x))
}

infix fun Var.rel(x: ByteString): RelationConceptP {
    return RelationConceptP(EntityConceptSP(this), ByteEntityConceptP(x))
}

infix fun Var.rel(x: String): RelationConceptP {
    return RelationConceptP(EntityConceptSP(this), IdEntityConceptFP(x))
}

infix fun Var.rel(x: Var): RelationConceptP {
    return RelationConceptP(EntityConceptSP(this), EntityConceptSP(x))
}

infix fun DerivedMeasureP.eq(x: Double): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.EQUALS, ConstantMeasureP(x))
}

infix fun DerivedMeasureP.eq(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.EQUALS, x.asPattern())
}

infix fun DerivedMeasureP.eq(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.EQUALS, x)
}

infix fun DerivedMeasureP.eq(x: Var): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.EQUALS, MeasureSP(x))
}

infix fun DerivedMeasure.eq(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.EQUALS, x)
}

infix fun DerivedMeasure.eq(x: Var): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.EQUALS, MeasureSP(x))
}

infix fun Var.eq(x: Double): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, ConstantMeasureP(x))
}

infix fun Var.eq(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x.asPattern())
}

infix fun Var.eq(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x)
}

infix fun Var.eq(x: Var): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, MeasureSP(x))
}

infix fun DerivedMeasureP.lt(x: Double): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN, ConstantMeasureP(x))
}

infix fun DerivedMeasureP.lt(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN, x.asPattern())
}

infix fun DerivedMeasureP.lt(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN, x)
}

infix fun DerivedMeasureP.lt(x: Var): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN, MeasureSP(x))
}

infix fun DerivedMeasure.lt(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.LESS_THAN, x)
}

infix fun DerivedMeasure.lt(x: Var): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.LESS_THAN, MeasureSP(x))
}

infix fun Var.lt(x: Double): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, ConstantMeasureP(x))
}

infix fun Var.lt(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x.asPattern())
}

infix fun Var.lt(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x)
}

infix fun Var.lt(x: Var): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, MeasureSP(x))
}

infix fun DerivedMeasureP.gt(x: Double): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN, ConstantMeasureP(x))
}

infix fun DerivedMeasureP.gt(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN, x.asPattern())
}

infix fun DerivedMeasureP.gt(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN, x)
}

infix fun DerivedMeasureP.gt(x: Var): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN, MeasureSP(x))
}

infix fun DerivedMeasure.gt(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.GREATER_THAN, x)
}

infix fun DerivedMeasure.gt(x: Var): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.GREATER_THAN, MeasureSP(x))
}

infix fun Var.gt(x: Double): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, ConstantMeasureP(x))
}

infix fun Var.gt(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x.asPattern())
}

infix fun Var.gt(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x)
}

infix fun Var.gt(x: Var): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, MeasureSP(x))
}

infix fun DerivedMeasureP.lte(x: Double): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN_EQUALS, ConstantMeasureP(x))
}

infix fun DerivedMeasureP.lte(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN_EQUALS, x.asPattern())
}

infix fun DerivedMeasureP.lte(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN_EQUALS, x)
}

infix fun DerivedMeasureP.lte(x: Var): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.LESS_THAN_EQUALS, MeasureSP(x))
}

infix fun DerivedMeasure.lte(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.LESS_THAN_EQUALS, x)
}

infix fun DerivedMeasure.lte(x: Var): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.LESS_THAN_EQUALS, MeasureSP(x))
}

infix fun Var.lte(x: Double): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, ConstantMeasureP(x))
}

infix fun Var.lte(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x.asPattern())
}

infix fun Var.lte(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x)
}

infix fun Var.lte(x: Var): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, MeasureSP(x))
}

infix fun DerivedMeasureP.gte(x: Double): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN_EQUALS, ConstantMeasureP(x))
}

infix fun DerivedMeasureP.gte(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN_EQUALS, x.asPattern())
}

infix fun DerivedMeasureP.gte(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN_EQUALS, x)
}

infix fun DerivedMeasureP.gte(x: Var): MeasureComparisonP {
    return MeasureComparisonP(this, Comparison.GREATER_THAN_EQUALS, MeasureSP(x))
}

infix fun DerivedMeasure.gte(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.GREATER_THAN_EQUALS, x)
}

infix fun DerivedMeasure.gte(x: Var): MeasureComparisonP {
    return MeasureComparisonP(asPattern(), Comparison.GREATER_THAN_EQUALS, MeasureSP(x))
}

infix fun Var.gte(x: Double): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, ConstantMeasureP(x))
}

infix fun Var.gte(x: Measure): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x.asPattern())
}

infix fun Var.gte(x: MeasureP): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, x)
}

infix fun Var.gte(x: Var): MeasureComparisonP {
    return MeasureComparisonP(DerivedMeasureSP(this), Comparison.EQUALS, MeasureSP(x))
}

infix fun MeasureComparisonP.with(confidence: List<ConfidenceValue>): MeasurementP {
    return MeasurementP(left, comparison, right, ConfidenceFP(confidence))
}

infix fun MeasureComparisonP.with(confidence: Var): MeasurementP {
    return MeasurementP(left, comparison, right, ConfidenceSP(confidence))
}
