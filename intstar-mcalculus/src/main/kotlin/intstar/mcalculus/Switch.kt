package intstar.mcalculus

interface SwitchSide {
    fun manifest(measurements: Iterable<Measurement>?): Iterable<Measurement>

    fun connect(otherSide: SwitchSide) {
    }
}

fun createSwitch(left: SwitchSide, right: SwitchSide) {
    left.connect(right)
    right.connect(left)
}
