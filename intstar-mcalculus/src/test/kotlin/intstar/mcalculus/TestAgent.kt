package intstar.mcalculus

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration.ofSeconds

class TestAgent {
    @Test
    fun test() {
        assertTimeoutPreemptively(ofSeconds(2)) {
            val agent = Agent(TestAttention(), TestAction(), iteratorOf(MSG))
            assertThrows<UnsupportedOperationException> { agent.wait(agent) }
            agent.run()
        }
    }
}

private val MSG = Measurement(DerivedMeasure(IdEntityConcept(AGENT), IdEntityConcept(FOCUS)), Comparison.GREATER_THAN,
        ConstantMeasure(0.0), TRUE)

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
        otherSide.manifest(iteratorOf(MSG), this)
    }

    override fun wait(otherSide: SwitchSide) {
        throw UnsupportedOperationException()
    }

    override fun connect(otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
    }

    override fun disconnect(otherSide: SwitchSide) {
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
        otherSide.manifest(iteratorOf(MSG), this)
    }

    override fun connect(otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
    }

    override fun disconnect(otherSide: SwitchSide) {
        assertTrue(otherSide is Agent)
    }
}
