package intstar.mcalculus

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestAgent {
    @Test
    fun test() {
        val agent = Agent(TestAttention(), TestAction(), listOf(MSG).iterator())
        assertThrows<UnsupportedOperationException> { agent.wait(agent) }
        agent.start()
    }
}

private val MSG = Measurement(DerivedMeasure(AGENT, FOCUS), Comparison.GREATER_THAN, ConstantMeasure(0.0), TRUE)

private class TestAction : SwitchSide {
    var stop = false

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
        if (stop) {
            (otherSide as Agent).stop()
        } else {
            stop = true
        }
        assertEquals(listOf(MSG), measurements.asSequence().toList())
        otherSide.manifest(listOf(MSG).iterator(), this)
    }

    override fun wait(otherSide: SwitchSide) {
        throw UnsupportedOperationException()
    }

    override fun connect(otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
    }
}

private class TestAttention : SwitchSide {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        assertTrue(otherSide is Agent || otherSide is TestAction)
        assertEquals(listOf(MSG), measurements.asSequence().toList())
    }

    override fun wait(otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
        otherSide.manifest(listOf(MSG).iterator(), this)
    }

    override fun connect(otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
    }
}
