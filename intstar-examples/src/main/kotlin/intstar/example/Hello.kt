package intstar.example

import intstar.mcalculus.Agent
import intstar.mcalculus.EntityConcept
import intstar.mcalculus.FOCUS
import intstar.mcalculus.MANIFEST
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide
import intstar.mcalculus.TRUE

private fun main() {
    val bootstrap = listOf(HELLO_ACTION_MANIFEST, HELLO_ACTION_FOCUSED).iterator()
    val agent = Agent(UnionAttention(), UnionAction(::manifestCreator), bootstrap)
    agent.start()
}

private val HELLO_ACTION_FOCUSED = ("hello" ms FOCUS) gt 0.0 with TRUE
private val HELLO_ACTION_MANIFEST = ("hello" rel b("HelloAction") ms MANIFEST) gt 0.0 with TRUE

private fun manifestCreator(concept: EntityConcept): SwitchSide {
    return if (concept.bstr?.asString() == "HelloAction") HelloAction() else throw UnsupportedOperationException()
}

private class HelloAction : BaseSwitchSide() {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        val mts = measurements.asSequence().toList()
        if (mts.contains(HELLO_ACTION_MANIFEST)) {
            println("Hello World")
            otherSide.manifest(listOf(HELLO_ACTION_FOCUSED).iterator(), this)
        } else {
            (otherSide as Agent).stop()
        }
    }
}
