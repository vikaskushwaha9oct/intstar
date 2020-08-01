package intstar.mcalculus

interface LanguageParser {
    fun parse(stream: InputStream): Iterator<Measurement>
}

interface LanguageRenderer {
    fun render(measurements: Iterator<Measurement>): InputStream
}
