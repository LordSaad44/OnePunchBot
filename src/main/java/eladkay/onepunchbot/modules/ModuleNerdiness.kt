package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/9/2017.
 */
object ModuleNerdiness : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.startsWith("hey bot, make me first", true) && message.channelReceiver != null) {
            /*message.reply("Sure lol")
            val content = message.content.replace("hey bot, char value of ", "", ignoreCase = true)
            val int = content.toInt().toChar()
            if(int == ' ') message.reply("Blankspace") else message.reply(int.toString())*/
            message.author.updateNickname(message.channelReceiver.server, 0.toChar() + (message.author.getNickname(message.channelReceiver.server) ?: message.author.name))
            message.reply("Your nick is now " + (2.toChar() + (message.author.getNickname(message.channelReceiver.server) ?: message.author.name)))
        } //else message.reply("poop")
        if (message.content.startsWith("hey bot, channel id?", true) && message.channelReceiver != null) {
            message.reply("${message.channelReceiver.id}@${message.channelReceiver.server.id}")
        }
        return super.onMessage(api, message)
    }
}
