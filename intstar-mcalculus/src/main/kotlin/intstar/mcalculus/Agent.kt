package intstar.mcalculus

class Agent(
        private val attention: SwitchSide,
        private val action: SwitchSide,
        bootstrap: Iterator<Measurement>
) : SwitchSide {
    init {
        createSwitch(this, attention)
        createSwitch(this, action)
        attention.manifest(bootstrap, this)
    }

    private var alive = true
    private var context = bootstrap

    fun start() {
        while (alive) {
            attention.wait(this)
            action.manifest(context, this)
        }
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
    }
}
