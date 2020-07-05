package intstar.example

import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.ENTITY
import intstar.mcalculus.Entity
import intstar.mcalculus.FOCUS
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide

private fun main() {
    val entity = Entity(BaseAttention(), HelloAction(), listOf(ENTITY_FOCUSED))
    entity.start()
}

private val ENTITY_FOCUSED = DerivedMeasure(ENTITY, FOCUS).isGreaterThanZero()
private val ENTITY_DEFOCUSED = DerivedMeasure(ENTITY, FOCUS).isEqualToZero()

private class HelloAction : BaseAction() {
    override fun manifest(measurements: Iterable<Measurement>, otherSide: SwitchSide) {
        if (measurements.contains(ENTITY_FOCUSED)) {
            println("Hello World")
            manifestEntity(listOf(ENTITY_DEFOCUSED), this)
        } else if (measurements.contains(ENTITY_DEFOCUSED)) {
            stopEntity()
        }
    }
}
