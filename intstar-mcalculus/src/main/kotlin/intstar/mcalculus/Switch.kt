package intstar.mcalculus

interface SwitchSide {
    fun manifest(measurements: Iterable<Measurement>, otherSide: SwitchSide)

    fun wait(otherSide: SwitchSide) {
        throw UnsupportedOperationException()
    }

    fun connect(otherSide: SwitchSide) {
    }
}

fun createSwitch(left: SwitchSide, right: SwitchSide) {
    left.connect(right)
    right.connect(left)
}
