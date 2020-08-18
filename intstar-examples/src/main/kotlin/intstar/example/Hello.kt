package intstar.example

import intstar.mcalculus.AGENT
import intstar.mcalculus.Agent
import intstar.mcalculus.FOCUS
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide
import intstar.mcalculus.TRUE

private fun main() {
    val agent = Agent(BaseAttention(), HelloAction(), listOf(AGENT_FOCUSED).iterator())
    agent.start()
}

private val AGENT_FOCUSED = (AGENT ms FOCUS) gt 0.0 with TRUE
private val AGENT_DEFOCUSED = (AGENT ms FOCUS) eq 0.0 with TRUE

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
