package eladkay.onepunchbot.misc

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.LargeStringHolder

/**
 * Created by Elad on 2/8/2017.
 */
object ModuleAviation : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.content.startsWith("!lawsofaviation"))
            message.reply(LargeStringHolder.LAWS_OF_AVIATION);
        return super.onMessage(api, message)
    }
}