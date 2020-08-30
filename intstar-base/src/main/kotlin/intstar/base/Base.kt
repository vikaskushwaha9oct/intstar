package intstar.base

import intstar.mcalculus.*

abstract class BaseSwitchSide : SwitchSide {
    override fun wait(otherSide: SwitchSide) {
        throw UnsupportedOperationException()
    }

    override fun connect(otherSide: SwitchSide) {
    }
}

class UnionAttention : BaseSwitchSide() {
    private var context = mutableListOf<Measurement>()

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        context.addAll(measurements.asSequence())
    }

    override fun wait(otherSide: SwitchSide) {
        otherSide.manifest(context.toList().iterator(), this)
        context.clear()
    }
}

class UnionAction(private val manifestCreator: (EntityConcept) -> SwitchSide) : BaseSwitchSide() {
    private val manifests = mutableMapOf<EntityConcept, SwitchSide>()

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        val mts = measurements.asSequence().toList()
        for (mt in mts.filter { it.left.measurable == MANIFEST }) {
            mt.left.concept.let {
                val manifest = manifestCreator(it.right!!)
                createSwitch(otherSide, manifest)
                manifests[it.left!!] = manifest
            }
        }
        for (mt in mts.filter { it.left.measurable == FOCUS }) {
            manifests[mt.left.concept]?.manifest(mts.iterator(), otherSide)
        }
    }
}
