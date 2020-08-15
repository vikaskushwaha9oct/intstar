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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class TestMLang {
    @Test
    fun testRenderer() {
        assertEquals(sampleMLangStr(), sampleMeasurements().iterator().renderMLang())
    }

    @Test
    fun testParser() {
        assertEquals(sampleMeasurements(), sampleMLangStr().parseMLang().asSequence().toList())
    }

    @Test
    fun testParserForBraces() {
        assertEquals(sampleMLangStrWithBracesExpanded(), sampleMLangStrWithBraces().parseMLang().renderMLang())
    }

    @Test
    fun testParserForErrors() {
        assertParseError("[1:6] Invalid char \\u2009", "hello\u2009world")
        assertParseError("[1:7] Expected close of [ first", "a [ b ( c")
        assertParseError("[1:3] Non matching )", "a ) b")
        assertParseError("[1:7] Expected close of ( first", "a ( b ] c")
        assertParseError("[1:13] Expected close of [ first", "a ( b ) c [ d")
        assertParseError("[1:5] Cannot have a } right after a {", "a { } b")
        assertParseError("[1:7] Cannot have a , right after a ,", "{ a , , b }")
        assertParseError("[1:1] Cannot have a } at the beginning", "} b")
        assertParseError("[1:1] Cannot have a , at the beginning", ", b")
        assertParseError("[1:3] Non matching }", "a } b")
        assertParseError("[1:3] No enclosing {} found for ,", "a , b")
        assertParseError("[1:2] Invalid escape seq \\0", "'\\0'")
        assertParseError("[1:2] Tab literal not allowed in quoted text", "'\t'")
        assertParseError("[1:2] Ascii escape seq should have 2 digits", "'\\x1'")
        assertParseError("[1:2] Ascii escape seq should be composed of 2 hexadecimal digits", "'\\x1g'")
        assertParseError("[1:2] Unicode escape seq should have 4 digits", "'\\u123'")
        assertParseError("[1:2] Unicode escape seq should be composed of 4 hexadecimal digits", "'\\u111g'")
        assertParseError("[1:6] Missing ending quote \"", "\"hello\n")
        assertParseError("[1:3] Expected a :", "a b")
        assertParseError("[1:5] Expected a identifier", "a : 'b'")
        assertParseError("[1:1] Expected a ( or string literal", "<a>")
        assertParseError("[1:2] Expected a hex based byte", "(zz 00)")
        assertParseError("[1:7] Expected a comparison", "a : b c")
        assertParseError("[1:24] Expected a ]", "a : b > 0.0 [0.0 % 1.0 % 1.0]")
        assertParseError("[1:25] Extraneous trailing tokens", "a : b > 0.0 [0.0 % 1.0] 1.0")
        assertParseError("[1:24] Expected a %", "a : b > 0.0 [0.0 : 1.0 * 1.0]")
        assertParseError("[1:18] Expected a :", "a : b > 0.0 [0.0 * 1.0]")
        assertParseError("[1:9] Expected a number", "a : b > c")
        assertParseError("[1:7] Reached end of tokens before completing measurement", "a : b >")
        assertParseError("[2:17] Confidence value should be > 0 and <= 1", "a : b \n> 0.0 [0.0 % 2.0]")
    }

    private fun assertParseError(err: String, str: String) {
        var ex: ParseException? = null
        try {
            str.parseMLang().asSequence().toList()
        } catch (e: ParseException) {
            ex = e
        }
        assertNotNull(ex, "no parse error found: $str")
        assertNotNull(ex!!.context, "no parse error context found: $str")
        val actualErr = with(ex.context!!) { "[$lineNo:$colNo] ${ex.error}" }
        assertEquals(err, actualErr, "wrong error found: $str")
    }

    private fun sampleMLangStr(): String {
        return """
            $ : * > hello ~ (f2 28 a1) : `<=~` [0.0 % 1.0];
            'I \'said\' "hello" and "world"\'' : hello = 5.0 [-Infinity : 0.0 ~ 0.0 % 0.56] [100.0 : 200.0 % 0.44];
            -Infinity < 'I \'said\' "hello" and "world"\'' ~ "hello\\\n\x00\u2009" : `hello world` [-Infinity : Infinity % 1.0];
            "" : `` >= 4.9E-324 [45.651 : Infinity % 1.0];
            5.0 <= -Infinity [-Infinity : 0.0 ~ 0.0 : Infinity % 1.0];""".trimIndent()
    }

    private fun sampleMeasurements(): List<Measurement> {
        val ie1 = IdEntityConcept("hello")
        val ie2 = IdEntityConcept("<=~")
        val ie3 = IdEntityConcept("hello world")
        val ie4 = IdEntityConcept("")
        val be1 = ByteEntityConcept(ByteString(byteArrayOf(-14, 40, -95)))
        val be2 = ByteEntityConcept(ByteString("hello\\\n\u0000\u2009".toByteArray()))
        val be3 = ByteEntityConcept(ByteString("I 'said' \"hello\" and \"world\"'".toByteArray()))
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
        return listOf(
            Measurement(dm1, Comparison.GREATER_THAN, dm2, TRUE),
            Measurement(dm3, Comparison.EQUALS, cm1, listOf(cv1, cv2)),
            Measurement(cm2, Comparison.LESS_THAN, dm4, UNKNOWN),
            Measurement(dm5, Comparison.GREATER_THAN_EQUALS, cm3, listOf(cv3)),
            Measurement(cm1, Comparison.LESS_THAN_EQUALS, cm2, FALSE)
        )
    }

    private fun sampleMLangStrWithBraces(): String {
        return "{a1, a2} {, ~ a3} : {{b1, b2} > {0.0} [-1.0 % 0.5], {b3, b4} <= 1.0 {[0.0 % 0.5], [1.0 % 0.5]}} [1.0 : 2.0 ~ 2.0 % 0.5];"
    }

    private fun sampleMLangStrWithBracesExpanded(): String {
        return """
            a1 : b1 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 : b2 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 : b3 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 : b3 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 : b4 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 : b4 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 ~ a3 : b1 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 ~ a3 : b2 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 ~ a3 : b3 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 ~ a3 : b3 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 ~ a3 : b4 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a1 ~ a3 : b4 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 : b1 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 : b2 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 : b3 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 : b3 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 : b4 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 : b4 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 ~ a3 : b1 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 ~ a3 : b2 > 0.0 [-1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 ~ a3 : b3 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 ~ a3 : b3 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 ~ a3 : b4 <= 1.0 [0.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];
            a2 ~ a3 : b4 <= 1.0 [1.0 % 0.5] [1.0 : 2.0 ~ 2.0 % 0.5];""".trimIndent()
    }
}
