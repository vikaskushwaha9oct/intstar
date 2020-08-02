package intstar.mcalculus

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestMeasurement {
    @Test
    fun testConstantMeasureChecks() {
        assertThrows<IllegalArgumentException> { ConstantMeasure(Double.NaN) }
        assertThrows<IllegalArgumentException> { ConstantMeasure(-0.0) }
    }

    @Test
    fun testConfidenceValueChecks() {
        assertThrows<IllegalArgumentException> { ConfidenceValue(emptyList(), 0.5) }
        val intervals1 = listOf(PointInterval(1.0), PointInterval(0.0))
        assertThrows<IllegalArgumentException> { ConfidenceValue(intervals1, 0.5) }
        val intervals2 = listOf(OpenInterval(-1.0, 0.0), PointInterval(0.0), OpenInterval(0.0, 1.0))
        assertThrows<IllegalArgumentException> { ConfidenceValue(intervals2, 0.5) }
        val intervals3 = listOf(PointInterval(0.0))
        assertThrows<IllegalArgumentException> { ConfidenceValue(intervals3, 0.0) }
        assertThrows<IllegalArgumentException> { ConfidenceValue(intervals3, 1.1) }
    }

    @Test
    fun testMeasurementChecks() {
        val m1 = DerivedMeasure(AGENT, MANIFEST)
        val m2 = ConstantMeasure(5.0)
        val cv1 = ConfidenceValue(listOf(OpenInterval(0.0, 3.5)), 0.5)
        val cv2 = ConfidenceValue(listOf(OpenInterval(3.0, 4.0)), 0.5)
        val cv3 = ConfidenceValue(listOf(OpenInterval(9.0, 10.0)), 0.5)
        val cv4 = ConfidenceValue(cv3.intervals, 0.4)
        assertThrows<IllegalArgumentException> { Measurement(m1, Comparison.GREATER_THAN, m2, listOf(cv1, cv2)) }
        assertThrows<IllegalArgumentException> { Measurement(m1, Comparison.EQUALS, m2, listOf(cv3, cv1)) }
        assertThrows<IllegalArgumentException> { Measurement(m1, Comparison.LESS_THAN, m2, listOf(cv1, cv4)) }
    }

    @Test
    fun testMeasurement() {
        val m1 = DerivedMeasure(RelationConcept(AGENT, ByteEntityConcept(ByteString("hello".toByteArray()))), FOCUS)
        val m2 = ConstantMeasure(5.0)
        val set = Comparison.values().map { Measurement(m1, it, m2, TRUE) }.toMutableSet()
        set.addAll(listOf(TRUE, FALSE, UNKNOWN).map { Measurement(m1, Comparison.EQUALS, m2, it) })
        assertEquals(7, set.size)
    }
}
