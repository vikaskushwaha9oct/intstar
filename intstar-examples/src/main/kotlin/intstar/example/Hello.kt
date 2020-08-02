package intstar.example

import intstar.mcalculus.AGENT
import intstar.mcalculus.Agent
import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.FOCUS
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide

private fun main() {
    val agent = Agent(BaseAttention(), HelloAction(), listOf(AGENT_FOCUSED).iterator())
    agent.start()
}

private val AGENT_FOCUSED = DerivedMeasure(AGENT, FOCUS).isGreaterThanZero()
private val AGENT_DEFOCUSED = DerivedMeasure(AGENT, FOCUS).isEqualToZero()

private class HelloAction : BaseAction() {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        val ms = measurements.asSequence().toList()
        if (ms.contains(AGENT_FOCUSED)) {
            println("Hello World")
            manifestAgent(listOf(AGENT_DEFOCUSED).iterator(), this)
        } else if (ms.contains(AGENT_DEFOCUSED)) {
            stopEntity()
        }
    }
}
