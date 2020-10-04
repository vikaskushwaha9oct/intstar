package intstar.example

import intstar.base.*
import intstar.mcalculus.*

private fun main() {
    val bootstrap = iteratorOf(HELLO_MANIFEST, HELLO_FOCUSED)
    val agent = Agent(UnionAttention(), UnionAction(::manifestCreator), bootstrap)
    agent.run()
}

private const val HELLO = "hello"
private val HELLO_FOCUSED = (HELLO ms FOCUS) gt 0.0 with TRUE
private val HELLO_MANIFEST = (HELLO rel b(HELLO) ms MANIFEST) gt 0.0 with TRUE

private fun manifestCreator(concept: EntityConcept): SwitchSide {
    return when (concept.bstr?.asString()) {
        HELLO -> HelloAction()
        else -> throw UnsupportedOperationException()
    }
}

private class HelloAction : BaseSwitchSide() {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        if (measurements.asSequence().contains(HELLO_MANIFEST)) {
            println("Hello World")
            otherSide.manifest(iteratorOf(HELLO_FOCUSED), this)
        } else {
            (otherSide as Agent).stop()
        }
    }
}
