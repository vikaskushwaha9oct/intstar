package intstar.mcalculus

interface SwitchSide {
    fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide)

    fun wait(otherSide: SwitchSide)

    fun connect(otherSide: SwitchSide)

    fun disconnect(otherSide: SwitchSide)
}

fun createSwitch(left: SwitchSide, right: SwitchSide) {
    left.connect(right)
    right.connect(left)
}
