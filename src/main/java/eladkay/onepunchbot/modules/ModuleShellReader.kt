package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleShellReader : IModule {
    override fun onInit(api: DiscordAPI) {
        //todo
        /*Thread {
            while (true) {
                val read = KotlinShell.read()
                if (read != "" && read.contains(">"))
                    api.servers.toMutableList()[0].channels.first { it.name == "shell" }.sendMessage("```$read```")
                Thread.sleep(200)
                if ("```$read```".length > 2000) api.servers.toMutableList()[0].channels.first { it.name == "shell" }.sendMessage("Message is too long!")
            }
        }.start()*/
    }
}

object KotlinShell {
    var builder: ProcessBuilder
    var process: Process

    init {
        builder = ProcessBuilder("cmd", "-c") //"kt/bin/kotlinc.bat" //"cmd", "-c" //"bash", "-i"
        process = builder.start()
    }

    fun read(any: Any?): String {
        var str = ""
        for (i in 0 until process.inputStream.available()) str += process.inputStream.read().toChar()
        return str
    }

    fun read(): String {
        val read = read(null)
        return read
    }

    fun write(string: String) {
        for (char in string)
            process.outputStream.write(char.toInt())
        process.outputStream.write(java.lang.Character.valueOf('\n').toInt())
        process.outputStream.flush()
    }
}
