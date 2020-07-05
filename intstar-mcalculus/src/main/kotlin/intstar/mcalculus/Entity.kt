package intstar.mcalculus

class Entity(val attention: SwitchSide, val action: SwitchSide, val bootstrap: Iterable<Measurement>) : SwitchSide {
    init {
        createSwitch(this, attention)
        createSwitch(this, action)
        attention.manifest(bootstrap, this)
    }

    var alive = true
    var context = bootstrap

    fun start() {
        while (alive) {
            attention.wait(this)
            action.manifest(context, this)
        }
    }

    fun stop() {
        alive = false
    }

    override fun manifest(measurements: Iterable<Measurement>, otherSide: SwitchSide) {
        if (otherSide == attention) {
            context = measurements
        } else {
            attention.manifest(measurements, otherSide)
        }
    }
}
