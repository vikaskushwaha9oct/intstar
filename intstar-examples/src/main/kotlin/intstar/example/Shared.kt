package intstar.example

import intstar.mcalculus.Agent
import intstar.mcalculus.Comparison.EQUALS
import intstar.mcalculus.Comparison.GREATER_THAN
import intstar.mcalculus.ConstantMeasure
import intstar.mcalculus.DerivedMeasure
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
    var agent: SwitchSide? = null

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
    }

    override fun wait(otherSide: SwitchSide) {
    }

    override fun connect(otherSide: SwitchSide) {
        agent = otherSide
    }

    fun manifestAgent(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        agent?.manifest(measurements, otherSide)
    }

    fun stopEntity() {
        (agent as Agent).stop()
    }
}

fun DerivedMeasure.isGreaterThanZero(): Measurement {
    return Measurement(this, GREATER_THAN, ConstantMeasure(0.0), TRUE)
}

fun DerivedMeasure.isEqualToZero(): Measurement {
    return Measurement(this, EQUALS, ConstantMeasure(0.0), TRUE)
}
