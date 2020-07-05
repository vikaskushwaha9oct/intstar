package intstar.example

import intstar.mcalculus.Entity
import intstar.mcalculus.Measurement
import intstar.mcalculus.SwitchSide

private fun main() {
    val entity = Entity(BaseAttention(), ChatAction(), listOf())
    entity.start()
}

private class ChatAction : BaseAction() {
    override fun manifest(measurements: Iterable<Measurement>, otherSide: SwitchSide) {
    }
}
