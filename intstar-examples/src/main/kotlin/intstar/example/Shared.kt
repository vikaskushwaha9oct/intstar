package intstar.example

import intstar.mcalculus.Comparison.EQUALS
import intstar.mcalculus.Comparison.GREATER_THAN
import intstar.mcalculus.ConstantMeasure
import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.Entity
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide
import intstar.mcalculus.TRUE

open class BaseAttention : SwitchSide {
    var context = mutableListOf<Measurement>()

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        updateContext(measurements, otherSide)
    }

    override fun wait(otherSide: SwitchSide) {
        otherSide.manifest(context.toList().iterator(), this)
        resetContext()
    }

    override fun connect(otherSide: SwitchSide) {
    }

    fun updateContext(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        context.addAll(measurements.asSequence())
    }

    fun resetContext() {
        context.clear()
    }
}

open class BaseAction : SwitchSide {
    var entity: SwitchSide? = null

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
    }

    override fun wait(otherSide: SwitchSide) {
    }

    override fun connect(otherSide: SwitchSide) {
        entity = otherSide
    }

    fun manifestEntity(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        entity?.manifest(measurements, otherSide)
    }

    fun stopEntity() {
        (entity as Entity).stop()
    }
}

fun DerivedMeasure.isGreaterThanZero(): Measurement {
    return Measurement(this, GREATER_THAN, ConstantMeasure(0.0), TRUE)
}

fun DerivedMeasure.isEqualToZero(): Measurement {
    return Measurement(this, EQUALS, ConstantMeasure(0.0), TRUE)
}
