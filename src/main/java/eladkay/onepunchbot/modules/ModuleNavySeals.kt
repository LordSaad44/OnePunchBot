package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.LargeStringHolder
import eladkay.onepunchbot.NavySealery

/**
 * Created by Elad on 2/19/2017.
 */
object ModuleNavySeals : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if("!navyseals" in message.content)
            if(":balance:" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_MC)
            else if(":latin:" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_LATIN)
            else if(":aquaThumbup:" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_VAZKII)
//            else if(message.content.split(" ").size > 1 && NavySealery.filter { message.content.split(" ")[1] in it.key }.any())
//                message.reply(NavySealery[message.content.split(" ")[1]])
            else {
                val value = NavySealery.entries.firstOrNull { it.key in message.content }?.value
                if(value == null) {
                    message.reply(LargeStringHolder.NAVY_SEAL)
                    return super.onMessage(api, message)
                }
                message.reply(value)
            }
        return super.onMessage(api, message)
    }
}
