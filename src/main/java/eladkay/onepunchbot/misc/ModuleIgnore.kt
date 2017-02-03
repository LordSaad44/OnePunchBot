package eladkay.onepunchbot.misc

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleIgnore : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        return "!shutup" in message.content || "!botignore" in message.content
    }
}