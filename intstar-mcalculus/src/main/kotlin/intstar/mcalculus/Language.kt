package intstar.mcalculus

import java.io.InputStream
import java.io.OutputStream

interface LanguageParser {
    fun parse(stream: InputStream): Iterable<Measurement>
}

interface LanguageRenderer {
    fun render(measurements: Iterable<Measurement>): OutputStream
}
