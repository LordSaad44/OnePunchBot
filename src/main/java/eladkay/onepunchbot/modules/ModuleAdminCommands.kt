package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleAdminCommands : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.startsWith("!giveall ", true) && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val rank = message.channelReceiver.server.roles.firstOrNull { it.name == message.content.replace("!giveall ", "") }
            for (player in message.channelReceiver.server.members) {
                if (!player.getRoles(message.channelReceiver.server).contains(rank)) {
                    rank?.addUser(player)
                    Thread.sleep(2000)
                    message.reply("Gave $player rank $rank")
                }
            }
            message.reply("Gave everyone rank $rank")
        } else  if (message.content.startsWith("!giverank ", true) && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val rank = message.channelReceiver.server.roles.firstOrNull { it.name == message.content.replace("!giverank ", "") }
                if (!message.author.getRoles(message.channelReceiver.server).contains(rank)) {
                    rank?.addUser(message.author)
                    Thread.sleep(2000)
                    message.reply("Gave ${message.author} rank $rank")
                }
        }
        return super.onMessage(api, message)
    }
}