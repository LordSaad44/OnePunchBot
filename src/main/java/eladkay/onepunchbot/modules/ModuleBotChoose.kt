package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.WireDontTouchThisOrIllKillYouWhileYouSleep
import java.util.*

/**
 * Created by Elad on 2/6/2017.
 */
@WireDontTouchThisOrIllKillYouWhileYouSleep
object ModuleBotChoose : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if ("hey bot, choose " in message.content.toLowerCase()) {
            val msg = message.content.replace("hey bot, choose", "", ignoreCase = true).replace("between ", "", true)
            val content = "(-?(?:\\d+|\\d*\\.\\d+))\\.\\.(-?(?:\\d+|\\d*\\.\\d+))".toRegex().replace(msg) {
                val n1 = it.groups[1]!!.value.toDouble().toInt()
                val n2 = it.groups[2]!!.value.toDouble().toInt()
                (n1..n2).joinToString("/")
            }
            val list = content.split("(?:\\/|\\bor\\b|\\band\\b)".toRegex())
            message.reply(list[Random().nextInt(list.size)].trim())
        }
        return super.onMessage(api, message)
    }
}
