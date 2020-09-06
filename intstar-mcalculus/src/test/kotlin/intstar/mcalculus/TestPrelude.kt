package intstar.mcalculus

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class TestPrelude {
    @Test
    fun testSumsToOne() {
        assertFalse(listOf(0.4, 0.3, 0.31).sumsToOne { it })
        assertFalse(listOf(0.4, 0.3, 0.29).sumsToOne { it })
        assertTrue(listOf(0.4, 0.3, 0.3).sumsToOne { it })
    }

    @Test
    fun testIsDefined() {
        assertFalse(Double.NaN.isDefined())
        assertFalse((-0.0).isDefined())
        assertTrue(INFINITY.isDefined())
        assertTrue(NEG_INFINITY.isDefined())
        assertTrue(2.0.isDefined())
    }

    @Test
    fun testIntervalCreation() {
        assertThrows<IllegalArgumentException> { OpenInterval(1.0, 1.0) }
        assertThrows<IllegalArgumentException> { OpenInterval(2.0, 1.0) }
        assertThrows<IllegalArgumentException> { OpenInterval(Double.NaN, 1.0) }
        assertThrows<IllegalArgumentException> { OpenInterval(1.0, Double.NaN) }
        assertThrows<IllegalArgumentException> { OpenInterval(INFINITY, 1.0) }
        assertThrows<IllegalArgumentException> { OpenInterval(1.0, NEG_INFINITY) }
        assertThrows<IllegalArgumentException> { OpenInterval(-0.0, 1.0) }
        assertThrows<IllegalArgumentException> { OpenInterval(-1.0, -0.0) }
        assertDoesNotThrow { OpenInterval(0.0, 1.0) }
        assertDoesNotThrow { OpenInterval(NEG_INFINITY, INFINITY) }
        assertThrows<IllegalArgumentException> { PointInterval(Double.NaN) }
        assertThrows<IllegalArgumentException> { PointInterval(NEG_INFINITY) }
        assertThrows<IllegalArgumentException> { PointInterval(INFINITY) }
        assertThrows<IllegalArgumentException> { PointInterval(-0.0) }
        assertDoesNotThrow { PointInterval(1.0) }
    }

    @Test
    fun testIntervalAnchors() {
        assertEquals(listOf(1.0, 2.0), OpenInterval(1.0, 2.0).anchors())
        assertEquals(listOf(1.0), PointInterval(1.0).anchors())
    }

    @Test
    fun testIntervalCompareStart() {
        assertEquals(-1, OpenInterval(1.0, 2.0).compareStart(OpenInterval(1.5, 2.0)))
        assertEquals(-1, OpenInterval(1.0, 2.0).compareStart(PointInterval(1.5)))
        assertEquals(-1, PointInterval(1.0).compareStart(OpenInterval(1.5, 2.0)))
        assertEquals(-1, PointInterval(1.0).compareStart(PointInterval(1.5)))
        assertEquals(1, OpenInterval(1.5, 2.0).compareStart(OpenInterval(1.0, 2.0)))
        assertEquals(1, OpenInterval(1.5, 2.0).compareStart(PointInterval(1.0)))
        assertEquals(1, PointInterval(1.5).compareStart(OpenInterval(1.0, 2.0)))
        assertEquals(1, PointInterval(1.5).compareStart(PointInterval(1.0)))
        assertEquals(0, OpenInterval(NEG_INFINITY, 2.0).compareStart(OpenInterval(NEG_INFINITY, 2.0)))
        assertEquals(1, OpenInterval(1.0, 2.0).compareStart(PointInterval(1.0)))
        assertEquals(-1, PointInterval(1.0).compareStart(OpenInterval(1.0, 2.0)))
        assertEquals(0, PointInterval(1.0).compareStart(PointInterval(1.0)))
    }

    @Test
    fun testIntervalIntersectsWith() {
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(1.0, 2.0)))
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(0.0, 1.5)))
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(1.5, 3.0)))
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(0.0, 3.0)))
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(1.5, 1.6)))
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(1.0, 1.5)))
        assertFalse(OpenInterval(1.0, 2.0).intersectsWith(OpenInterval(0.0, 1.0)))
        assertTrue(OpenInterval(1.0, 2.0).intersectsWith(PointInterval(1.5)))
        assertFalse(OpenInterval(1.0, 2.0).intersectsWith(PointInterval(1.0)))
        assertFalse(OpenInterval(1.0, 2.0).intersectsWith(PointInterval(2.0)))
        assertTrue(PointInterval(1.5).intersectsWith(OpenInterval(1.0, 2.0)))
        assertFalse(PointInterval(1.0).intersectsWith(OpenInterval(1.0, 2.0)))
        assertFalse(PointInterval(2.0).intersectsWith(OpenInterval(1.0, 2.0)))
        assertTrue(PointInterval(1.0).intersectsWith(PointInterval(1.0)))
        assertFalse(PointInterval(1.0).intersectsWith(PointInterval(2.0)))
    }

    @Test
    fun testIntervalsIsSortedByStart() {
        val s1 = listOf(
                OpenInterval(-1.0, 0.0), PointInterval(0.0),
                OpenInterval(0.0, 1.0), OpenInterval(9.0, 9.5)
        )
        val s2 = listOf(OpenInterval(0.0, 9.0))
        val s3 = listOf(OpenInterval(0.0, 5.0), OpenInterval(-3.0, INFINITY))
        assertTrue(s1.isSortedByStart())
        assertFalse(s3.isSortedByStart())
        assertTrue(listOf(s1, s2).isSortedByStart { it })
        assertTrue(listOf(s2, s3).isSortedByStart { it })
        assertFalse(listOf(s2, s1).isSortedByStart { it })
    }

    @Test
    fun testIntervalsIsDisjoint() {
        val s1 = listOf(
                OpenInterval(-1.0, 0.0), PointInterval(0.0),
                OpenInterval(0.0, 1.0), OpenInterval(9.0, 9.5)
        )
        val s2 = listOf(OpenInterval(10.0, 11.0), PointInterval(11.0))
        val s3 = listOf(OpenInterval(0.0, 5.0), OpenInterval(-3.0, INFINITY))
        val s4 = listOf(OpenInterval(10.5, 20.0))
        assertTrue(s1.isDisjoint())
        assertFalse(s3.isDisjoint())
        assertFalse(s1.isDisconnected())
        assertTrue(s2.isDisconnected())
        assertTrue(listOf(s1, s2).isDisjoint { it })
        assertFalse(listOf(s2, s4).isDisjoint { it })
    }

    @Test
    fun testByteString() {
        val byteArray = byteArrayOf(127, 0, -1)
        val byteString = ByteString(byteArray)
        byteArray[0] = 120
        assertEquals(3, byteString.size())
        assertEquals(0, byteString.byteAt(1))
        assertEquals("7f 00 ff", byteString.toString())
        assertEquals(1, setOf(byteString, ByteString(byteString.toList().toByteArray())).size)
        assertEquals("byte", ByteString("byte".toByteArray()).asString())
        val byteArray2 = byteString.toByteArray()
        val byteString2 = ByteString(byteArray2)
        byteArray2[0] = 120
        assertEquals(byteString, byteString2)
    }
}
