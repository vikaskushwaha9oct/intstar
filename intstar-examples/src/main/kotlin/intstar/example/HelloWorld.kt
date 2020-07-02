package intstar.example

import intstar.mcalculus.Comparison
import intstar.mcalculus.ConstantMeasure
import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.Entity
import intstar.mcalculus.IdEntityConcept
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide

fun main() {
    val entity = Entity(TestAttention(), TestAction(), emptyList())
    entity.start()
}

class TestAttention(var alive: Boolean = false) : SwitchSide {
    override fun manifest(measurements: Iterable<Measurement>?): Iterable<Measurement> {
        if (measurements == null) {
            return listOf(
                Measurement(
                    DerivedMeasure(IdEntityConcept("$"), IdEntityConcept("alive")),
                    ConstantMeasure(0.0),
                    Comparison.GREATER_THAN
                )
            )
        }
        return emptyList()
    }
}

class TestAction : SwitchSide {
    override fun manifest(measurements: Iterable<Measurement>?): Iterable<Measurement> {
        println("Hello World")
        return emptyList()
    }
}