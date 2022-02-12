package intstar.mcalculus

interface MeasurementNetwork {
    fun search(measurements: Iterator<Measurement>): Iterator<Measurement>

    fun store(measurements: Iterator<Measurement>)

    fun organize()
}
