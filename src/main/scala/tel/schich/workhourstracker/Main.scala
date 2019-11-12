package tel.schich.workhourstracker

import java.nio.file.Paths

object Main {

    def main(args: Array[String]): Unit = {
        args match {
            case Array("collect", filePath) =>
                Collector.collect(Paths.get(filePath))
            case Array("analyse", filePath) =>
                Analyser.analyse(Paths.get(filePath))
            case _ =>
                System.err.println("Usage: <collect|analyse> <file>")
                System.exit(1)
        }
    }

}
