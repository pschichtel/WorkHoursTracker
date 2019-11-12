package tel.schich.workhourstracker

import java.io.BufferedWriter
import java.nio.file.{Files, Path}
import java.time.LocalDateTime

object Collector {

    def collect(output: Path): Unit = {

        val writer = Files.newBufferedWriter(output)

        val sources: Seq[SignalSource] = Seq(
            new ScreenSaverDbusSignalSource,
            new StartStopSource,
        )

        val handler = handleSignal(writer) _

        for (source <- sources) {
            source.start(handler)
        }

        Runtime.getRuntime.addShutdownHook(new Thread(() => stopSources(sources, handler)))
    }

    def handleSignal(writer: BufferedWriter)(label: String, state: Boolean): Unit = {
        val now = LocalDateTime.now()
        val stateName = if (state) "active" else "inactive"
        println(s"$now -> $label ($stateName)")
        writer.write(s"$now,$stateName,$label\n")
    }

    def stopSources(sources: Seq[SignalSource], emitter: SignalSource.Emitter): Unit = {
        for (source <- sources) {
            source.shutdown(emitter)
        }
    }
}
