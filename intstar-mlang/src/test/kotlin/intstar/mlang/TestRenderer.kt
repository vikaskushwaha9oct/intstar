package intstar.mlang

import intstar.mcalculus.AGENT
import intstar.mcalculus.ByteEntityConcept
import intstar.mcalculus.ByteString
import intstar.mcalculus.Comparison
import intstar.mcalculus.ConfidenceValue
import intstar.mcalculus.ConstantMeasure
import intstar.mcalculus.DerivedMeasure
import intstar.mcalculus.FALSE
import intstar.mcalculus.FOCUS
import intstar.mcalculus.INFINITY
import intstar.mcalculus.IdEntityConcept
import intstar.mcalculus.Measurement
import intstar.mcalculus.NEG_INFINITY
import intstar.mcalculus.OpenInterval
import intstar.mcalculus.PointInterval
import intstar.mcalculus.RelationConcept
import intstar.mcalculus.TRUE
import intstar.mcalculus.UNKNOWN
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestRenderer {
    @Test
    fun test() {
        val ie1 = IdEntityConcept("hello")
        val ie2 = IdEntityConcept("<=~")
        val ie3 = IdEntityConcept("hello world")
        val ie4 = IdEntityConcept("")
        val be1 = ByteEntityConcept(ByteString(byteArrayOf(-14, 40, -95)))
        val be2 = ByteEntityConcept(ByteString("hello\n\u0000\u2009".toByteArray()))
        val be3 = ByteEntityConcept(ByteString("I 'said' \"hello\" and \"world\"".toByteArray()))
        val be4 = ByteEntityConcept(ByteString())
        val cm1 = ConstantMeasure(5.0)
        val cm2 = ConstantMeasure(NEG_INFINITY)
        val cm3 = ConstantMeasure(Double.MIN_VALUE)
        val cv1 = ConfidenceValue(listOf(OpenInterval(NEG_INFINITY, 0.0), PointInterval(0.0)), 0.56)
        val cv2 = ConfidenceValue(listOf(OpenInterval(100.0, 200.0)), 0.44)
        val cv3 = ConfidenceValue(listOf(OpenInterval(45.651, INFINITY)), 1.0)
        val dm1 = DerivedMeasure(AGENT, FOCUS)
        val dm2 = DerivedMeasure(RelationConcept(ie1, be1), ie2)
        val dm3 = DerivedMeasure(be3, ie1)
        val dm4 = DerivedMeasure(RelationConcept(be3, be2), ie3)
        val dm5 = DerivedMeasure(be4, ie4)
        assertEquals(
            """
            |$ : * > hello ~ (f2 28 a1) : `<=~` [0.0 % 1.0];
            |'I \'said\' "hello" and "world"' : hello = 5.0 [-Infinity : 0.0 ~ 0.0 % 0.56] [100.0 : 200.0 % 0.44];
            |-Infinity < 'I \'said\' "hello" and "world"' ~ "hello\n\x00\u2009" : `hello world` [-Infinity : Infinity % 1.0];
            |"" : `` >= 4.9E-324 [45.651 : Infinity % 1.0];
            |5.0 <= -Infinity [-Infinity : 0.0 ~ 0.0 : Infinity % 1.0];""".trimMargin(),
            listOf(
                Measurement(dm1, Comparison.GREATER_THAN, dm2, TRUE),
                Measurement(dm3, Comparison.EQUALS, cm1, listOf(cv1, cv2)),
                Measurement(cm2, Comparison.LESS_THAN, dm4, UNKNOWN),
                Measurement(dm5, Comparison.GREATER_THAN_EQUALS, cm3, listOf(cv3)),
                Measurement(cm1, Comparison.LESS_THAN_EQUALS, cm2, FALSE)
            ).iterator().renderMLang()
        )
    }
}
