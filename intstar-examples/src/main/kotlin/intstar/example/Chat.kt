package intstar.example

import intstar.base.*
import intstar.mcalculus.*
import java.io.IOException
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

private fun main() {
    val bootstrap = iteratorOf(TERMINAL_MANIFEST, NETWORK_MANIFEST)
    val agent = Agent(UnionAttention(), UnionAction(::manifestCreator), bootstrap)
    agent.run()
}

private const val ATTRIBUTION = "attribution"
private const val TERMINAL = "terminal"
private const val NETWORK = "network"
private const val OTHER_AGENT = "other"
private val MSG_ATTRIBUTION_PAT = (v(0) rel v(1) ms ATTRIBUTION) gt 0.0 with TRUE
private val TERMINAL_MANIFEST = (TERMINAL rel b(1) ms MANIFEST) gt 0.0 with TRUE
private val NETWORK_MANIFEST = (NETWORK rel b(2) ms MANIFEST) gt 0.0 with TRUE
private val TERMINAL_FOCUS = (TERMINAL ms FOCUS) gt 0.0 with TRUE
private val NETWORK_FOCUS = (NETWORK ms FOCUS) gt 0.0 with TRUE

private fun manifestCreator(concept: EntityConcept): SwitchSide {
    return when (concept.bstr?.byteAt(0)?.toInt()) {
        1 -> ChatTerminal()
        2 -> ChatNetwork()
        else -> throw UnsupportedOperationException()
    }
}

private class ChatTerminal : BaseSwitchSide() {
    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        measurements.asSequence().map(MSG_ATTRIBUTION_PAT::match).filterNotNull().forEach {
            val speaker = it.c(0).id!!
            val message = it.c(1).bstr!!.asString()
            println("\n[$speaker] $message")
        }
    }

    override fun connect(otherSide: SwitchSide) {
        thread(start = true) {
            while (true) {
                val message = readLine()!!
                val messageM = (AGENT rel b(message) ms ATTRIBUTION) gt 0.0 with TRUE
                otherSide.manifest(iteratorOf(messageM, NETWORK_FOCUS), this)
            }
        }
    }
}

private class ChatNetwork : BaseSwitchSide() {
    private lateinit var socket: Socket
    private lateinit var socketReader: Scanner
    private lateinit var socketWriter: PrintWriter

    override fun manifest(measurements: Iterator<Measurement>, otherSide: SwitchSide) {
        measurements.asSequence().map(MSG_ATTRIBUTION_PAT::match).filterNotNull().forEach {
            val message = it.c(1).bstr!!.asString()
            socketWriter.println(message)
            socketWriter.flush()
        }
    }

    override fun connect(otherSide: SwitchSide) {
        thread(start = true) {
            socket = try {
                ServerSocket(9005).accept()
            } catch (e: IOException) {
                Socket("localhost", 9005)
            }
            socketReader = Scanner(socket.getInputStream())
            socketWriter = PrintWriter(socket.getOutputStream())
            while (true) {
                val message = socketReader.nextLine()
                val messageM = (OTHER_AGENT rel b(message) ms ATTRIBUTION) gt 0.0 with TRUE
                otherSide.manifest(iteratorOf(messageM, TERMINAL_FOCUS), this)
            }
        }
    }

    override fun disconnect(otherSide: SwitchSide) {
        socketReader.close()
        socketWriter.close()
        socket.close()
    }
}
