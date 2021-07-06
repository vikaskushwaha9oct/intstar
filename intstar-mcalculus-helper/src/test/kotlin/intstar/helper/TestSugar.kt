package intstar.helper

import intstar.mcalculus.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestSugar {
    @Test
    fun test() {
        val ie1 = IdEntityConcept("a")
        val ie2 = IdEntityConcept("b")
        val be1 = ByteEntityConcept(ByteString(byteArrayOf(-14, 40, -95)))
        val be2 = ByteEntityConcept(ByteString("hello".toByteArray()))
        val cm1 = ConstantMeasure(5.0)
        val cv1 = ConfidenceValue(listOf(OpenInterval(NEG_INFINITY, 0.0), PointInterval(0.0)), 0.56)
        val cv2 = ConfidenceValue(listOf(PointInterval(100.0), OpenInterval(100.0, 200.0)), 0.44)
        val cv3 = ConfidenceValue(listOf(PointInterval(0.0), PointInterval(1.0), PointInterval(2.0)), 1.0)
        val dm1 = DerivedMeasure(ie1, ie2)
        val dm2 = DerivedMeasure(RelationConcept(ie1, be1), ie2)
        val dm3 = DerivedMeasure(be2, ie1)
        val dm4 = DerivedMeasure(RelationConcept(be1, be2), ie2)
        val dm5 = DerivedMeasure(RelationConcept(ie1, ie2), ie2)
        val dm6 = DerivedMeasure(RelationConcept(be1, ie1), ie2)
        val measurements = listOf(
                Measurement(dm1, Comparison.GREATER_THAN, dm2, TRUE),
                Measurement(dm1, Comparison.EQUALS, cm1, listOf(cv1, cv2)),
                Measurement(dm4, Comparison.LESS_THAN, cm1, UNKNOWN),
                Measurement(dm3, Comparison.GREATER_THAN_EQUALS, dm5, listOf(cv3)),
                Measurement(dm1, Comparison.LESS_THAN_EQUALS, dm6, FALSE)
        )
        val measurementsInSugar = listOf(
                ("a" ms "b") gt ("a" rel b(-14, 40, -95) ms "b") with conf(0.0 at 1.0),
                ("a" ms "b") eq 5.0 with conf(NEG_INFINITY to 0.0 and 0.0 at 0.56, 100.0 and 100.0 to 200.0 at 0.44),
                (b(-14, 40, -95) rel b("hello") ms "b") lt 5.0 with conf(NEG_INFINITY to INFINITY at 1.0),
                (b("hello") ms "a") gte ("a" rel "b" ms "b") with conf(0.0 and 1.0 and 2.0 at 1.0),
                ("a" ms "b") lte (b(-14, 40, -95) rel "a" ms "b") with conf(NEG_INFINITY to 0.0 and 0.0 to INFINITY at 1.0)
        )
        assertEquals(measurements, measurementsInSugar)
    }
}
