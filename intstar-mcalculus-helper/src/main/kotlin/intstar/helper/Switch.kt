package intstar.helper

import intstar.mcalculus.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class BaseSwitchSide : SwitchSide {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
    }

    override fun wait(otherSide: SwitchSide) {
        throw UnsupportedOperationException()
    }

    override fun connect(otherSide: SwitchSide) {
    }

    override fun disconnect(otherSide: SwitchSide) {
    }
}

class UnionAttention : BaseSwitchSide() {
    private val contextLock = ReentrantLock()
    private val contextAvailable = contextLock.newCondition()
    private val context = mutableListOf<Measurement>()

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        contextLock.withLock {
            context.addAll(measurements.asSequence())
            if (context.isNotEmpty()) {
                contextAvailable.signal()
            }
        }
    }

    override fun wait(otherSide: SwitchSide) {
        var contextList: List<Measurement>
        contextLock.withLock {
            if (context.isEmpty()) {
                contextAvailable.await()
            }
            contextList = context.toList()
            context.clear()
        }
        otherSide.manifest(contextList.iterator(), this)
    }
}

class UnionAction(private val manifestCreator: (EntityConcept) -> SwitchSide) : BaseSwitchSide() {
    private val manifests = mutableMapOf<EntityConcept, SwitchSide>()

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        val mts = measurements.asSequence().toList()
        for (mt in mts.filter { it.left.measurable.id == MANIFEST }) {
            mt.left.concept.let {
                val manifest = manifestCreator(it.right!!)
                createSwitch(otherSide, manifest)
                manifests[it.left!!] = manifest
            }
        }
        for (mt in mts.filter { it.left.measurable.id == FOCUS }) {
            manifests[mt.left.concept]?.manifest(mts.iterator(), otherSide)
        }
    }
}
