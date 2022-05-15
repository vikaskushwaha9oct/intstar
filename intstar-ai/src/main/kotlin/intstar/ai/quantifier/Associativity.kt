package intstar.ai.quantifier

import intstar.helper.BaseSwitchSide
import intstar.mcalculus.*
import kotlin.concurrent.thread

class AssociativityNetwork : MeasurementNetwork {
    private val store = mutableMapOf<IdEntityConcept, MutableSet<Measurement>>()

    override fun search(measurements: Iterator<Measurement>): Iterator<Pair<Measurement, Double>> {
        val idConcepts = measurements.asSequence().flatMap { extractIdConcepts(it) }.distinct()
        val associatedMeasurements = idConcepts.flatMap { store.getOrDefault(it, emptySet()) }.groupingBy { it }.eachCount()
        return associatedMeasurements.keys.sortedByDescending { associatedMeasurements[it] }
                .map { Pair(it, associatedMeasurements.getOrDefault(it, 0).toDouble()) }.iterator()
    }

    override fun store(measurements: Iterator<Measurement>) {
        measurements.forEach { m -> extractIdConcepts(m).forEach { store.getOrPut(it) { mutableSetOf() }.add(m) } }
    }

    override fun organize() {
    }

    private fun extractIdConcepts(m: Measurement): List<IdEntityConcept> {
        return extractIdConcepts(m.left) + extractIdConcepts(m.right)
    }

    private fun extractIdConcepts(m: Measure): List<IdEntityConcept> {
        return when (m) {
            is ConstantMeasure -> emptyList()
            is DerivedMeasure -> extractIdConcepts(m.concept) + m.measurable
        }
    }

    private fun extractIdConcepts(c: Concept): List<IdEntityConcept> {
        return when (c) {
            is IdEntityConcept -> listOf(c)
            is ByteEntityConcept -> emptyList()
            is RelationConcept -> extractIdConcepts(c.left) + extractIdConcepts(c.right)
            is MeasurementEntityConcept -> emptyList()
        }
    }
}

private val ASSOCIATIVITY = IdEntityConcept("associativity")

class AssociativityQuantifier : BaseSwitchSide() {
    private val network = AssociativityNetwork()
    private var currentSearchResults: Iterator<Measurement> = emptySequence<Measurement>().iterator();

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        val measurementsList = measurements.asSequence().toList()
        val pivot = MeasurementEntityConcept(measurementsList)
        currentSearchResults = network.search(measurementsList.iterator()).asSequence().map {
            Measurement(
                    DerivedMeasure(RelationConcept(MeasurementEntityConcept(listOf(it.first)), pivot), ASSOCIATIVITY),
                    Comparison.EQUALS,
                    ConstantMeasure(it.second),
                    TRUE
            )
        }.iterator()
    }

    override fun connect(otherSide: SwitchSide) {
        thread(start = true) {
            while (true) {
                if (currentSearchResults.hasNext()) {
                    otherSide.manifest(listOf(currentSearchResults.next()).iterator(), this)
                }
            }
        }
    }
}
