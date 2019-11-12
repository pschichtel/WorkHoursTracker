package tel.schich.workhourstracker

import java.nio.ByteBuffer

import org.freedesktop.dbus.DBusMatchRule
import org.freedesktop.dbus.connections.impl.DBusConnection
import org.freedesktop.dbus.connections.impl.DBusConnection.DBusBusType
import org.freedesktop.dbus.messages.DBusSignal

trait SignalSource {

    def start(emitter: SignalSource.Emitter): Unit = {

    }

    def shutdown(emitter: SignalSource.Emitter): Unit = {

    }

    def tick(emitter: SignalSource.Emitter): Unit = {

    }

}

object SignalSource {
    type Emitter = (String, Boolean) => Unit
}

// dbus-monitor --session "type='signal',interface='org.gnome.ScreenSaver'"
class ScreenSaverDbusSignalSource() extends SignalSource {
    private var connection: DBusConnection = _

    override def start(emitter: SignalSource.Emitter): Unit = {
        this.connection = DBusConnection.getConnection(DBusBusType.SESSION)

        this.connection.addGenericSigHandler(ScreenSaverDbusSignalSource.SignalMatchRule, (s: DBusSignal) => {

            val wireData = s.getWireData
            if (wireData != null && wireData.length == ScreenSaverDbusSignalSource.SignalDataExpectedSize) {
                val buf = wireData(ScreenSaverDbusSignalSource.SignalDataBufferIndex)
                val wrappedBuf = ByteBuffer.wrap(buf)

                val screenSaverActive = wrappedBuf.getInt() != 0
                val label = if (screenSaverActive) "locked" else "unlocked"
                emitter(label, !screenSaverActive)
            }
        })
    }

    override def shutdown(emitter: SignalSource.Emitter): Unit = {
        this.connection.disconnect()
        this.connection.close()
    }
}

object ScreenSaverDbusSignalSource {
    val SignalMatchRule = new DBusMatchRule("signal", "org.gnome.ScreenSaver", "ActiveChanged")
    val SignalDataExpectedSize = 3
    val SignalDataBufferIndex = 2
}

class StartStopSource extends SignalSource {

    override def start(emitter: SignalSource.Emitter): Unit = {
        emitter("logged in", true)
    }

    override def shutdown(emitter: SignalSource.Emitter): Unit = {
        emitter("logged out", false)
    }
}

