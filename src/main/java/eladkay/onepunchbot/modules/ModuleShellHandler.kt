package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleShellHandler : IModule {
    private var shell: Boolean = false

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        //todo
        /*
        if (message.channelReceiver.name.equals("shell", true) && message.content.startsWith("!toggleshell", true) && message.author.name == "Eladkay") {
            shell = !shell
            message.reply("shell = $shell")
        } else if (message.channelReceiver.name.equals("shell", true) && message.content.startsWith("!reset", true)) {
            KotlinShell.process = KotlinShell.builder.start()
            KotlinShell.builder = ProcessBuilder("cmd", "-c") //"kt/bin/kotlinc.bat" //"cmd", "-c"
            message.reply("shell = $shell")
        } else if (message.channelReceiver.name.equals("shell", true) && message.content.startsWith("!execute", true)) {
            if (message.author.isYourself) return true
            if (!shell) {
                message.reply("Shell not enabled. Ask Elad to enable it.")
                return true
            }
            println("Writing to KTShell: ${message.content}")
            KotlinShell.write(message.content.replace("!execute", ""))
            //Sheller.write(message.content)
        }*/
        return super.onMessage(api, message)
    }
}