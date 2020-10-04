package intstar.mcalculus

class Agent(
        private val attention: SwitchSide,
        private val action: SwitchSide,
        bootstrap: Iterator<Measurement>
) : SwitchSide {
    private var alive = true
    private var context = bootstrap
    private var otherSides = mutableListOf<SwitchSide>()

    init {
        createSwitch(this, attention)
        createSwitch(this, action)
        attention.manifest(context, this)
    }

    fun run() {
        while (alive) {
            attention.wait(this)
            action.manifest(context, this)
        }
        otherSides.forEach { it.disconnect(this) }
    }

    fun stop() {
        alive = false
    }

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        if (otherSide == attention) {
            context = measurements
        } else {
            attention.manifest(measurements, otherSide)
        }
    }

    override fun wait(otherSide: SwitchSide) {
        throw UnsupportedOperationException()
    }

    override fun connect(otherSide: SwitchSide) {
        otherSides.add(otherSide)
    }

    override fun disconnect(otherSide: SwitchSide) {
    }
}
