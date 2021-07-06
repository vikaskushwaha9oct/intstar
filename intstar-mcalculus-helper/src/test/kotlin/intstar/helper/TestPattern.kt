package intstar.helper

import intstar.mcalculus.TRUE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class TestPattern {
    @Test
    fun test() {
        val mt1 = ("x" rel "y" ms "a") gt 5.0 with TRUE
        val mt2 = ("x" ms "a") gt 5.0 with TRUE
        val mt3 = ("x" ms "a") eq ("y" ms "a") with TRUE
        val mt4 = ("x" ms "a") eq ("y" ms "b") with TRUE

        val mPattern1 = (v(0) rel v(1) ms "a") gt v(2) with v(3)
        val mMatch1 = mPattern1.match(mt1)!!
        val mResult1 = listOf(mMatch1.c(0).id, mMatch1.c(1).id, mMatch1.m(2).value, mMatch1.cf(3))
        assertEquals(listOf("x", "y", 5.0, TRUE), mResult1)
        assertNull(mPattern1.match(mt2))

        val mPattern2 = ("x" rel "y" ms "a") gt v(0) with TRUE
        assertEquals(5.0, mPattern2.match(mt1)!!.m(0).value)
        assertNull(mPattern2.match(mt2))

        val mPattern3 = ("x" ms v(0)) eq ("y" ms v(0)) with TRUE
        assertEquals("a", mPattern3.match(mt3)!!.c(0).id)
        assertNull(mPattern3.match(mt4))
    }
}
