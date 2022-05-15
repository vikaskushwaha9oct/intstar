package intstar.mcalculus

interface MeasurementNetwork {
    fun search(measurements: Iterator<Measurement>): Iterator<Pair<Measurement, Double>>

    fun store(measurements: Iterator<Measurement>)

    fun organize()
}
