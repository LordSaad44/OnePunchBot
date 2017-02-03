package eladkay.onepunchbot.misc

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleAutoripper : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.contains("<:autorip:277120850975653900>", true) && message.channelReceiver.name != "modlog") {
            Thread {
                Thread.sleep(5000 * "<:autorip:277120850975653900>".toRegex().findAll(message.content).toList().size.toLong())
                message.delete()
            }.start()
        }
        return super.onMessage(api, message)
    }
}