package intstar.example

import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.ENTITY
import intstar.mcalculus.Entity
import intstar.mcalculus.FOCUS
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide

private fun main() {
    val entity = Entity(BaseAttention(), HelloAction(), listOf(ENTITY_FOCUSED).iterator())
    entity.start()
}

private val ENTITY_FOCUSED = DerivedMeasure(ENTITY, FOCUS).isGreaterThanZero()
private val ENTITY_DEFOCUSED = DerivedMeasure(ENTITY, FOCUS).isEqualToZero()

private class HelloAction : BaseAction() {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        val ms = measurements.asSequence().toList()
        if (ms.contains(ENTITY_FOCUSED)) {
            println("Hello World")
            manifestEntity(listOf(ENTITY_DEFOCUSED).iterator(), this)
        } else if (ms.contains(ENTITY_DEFOCUSED)) {
            stopEntity()
        }
    }
}
