package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.LargeStringHolder

/**
 * Created by Elad on 2/19/2017.
 */
object ModuleNavySeals : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if("!navyseals" in message.content)
            if("<:kt:230142911751258122>" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_KT)
            else if("<:balance:270964604438315020>" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_MC)
            else if("<:latin:282944396112953344>" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_LATIN)
            else if("🤗" in message.content)
                message.reply(LargeStringHolder.NAVY_SEAL_FRIENDLINESS)
            else message.reply(LargeStringHolder.NAVY_SEAL)
        return super.onMessage(api, message)
    }
}