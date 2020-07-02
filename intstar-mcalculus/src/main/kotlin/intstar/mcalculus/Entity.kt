package intstar.mcalculus

class Entity(val attention: SwitchSide, val action: SwitchSide, val bootstrap: Iterable<Measurement>) : SwitchSide {
    init {
        createSwitch(this, attention)
        createSwitch(this, action)
        attention.manifest(bootstrap)
    }

    var alive = true

    fun start() {
        while (alive) {
            val measurements = attention.manifest(null)
            action.manifest(measurements)
        }
    }

    fun stop() {
        alive = false
    }

    override fun manifest(measurements: Iterable<Measurement>?): Iterable<Measurement> {
        attention.manifest(measurements)
        return emptyList()
    }
}
