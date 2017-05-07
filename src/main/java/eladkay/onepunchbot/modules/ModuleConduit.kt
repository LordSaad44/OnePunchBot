package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.WireDontTouchThisOrIllKillYouWhileYouSleep
import eladkay.onepunchbot.members

/**
 * Created by Elad on 3/13/2017.
 */
@WireDontTouchThisOrIllKillYouWhileYouSleep
object ModuleConduit : IModule {
    val map = mutableMapOf<User, List<User>>()
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.channelReceiver?.server != null && message.content.startsWith("!startconduit") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val channel = message.channelReceiver
            val user = message.author
            user.sendMessage("Conduit established.")
            val members = channel.members.toList().filter { it != user }
            map.put(user, members)
            message.delete()
        } else if (message.userReceiver != null && message.content.startsWith("!endconduit")) {
            val user = message.author
            user.sendMessage("Conduit closed.")
            map.remove(user)
            message.delete()
        } else if (message.channelReceiver?.server != null && message.content.startsWith("!excludesomeone ") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val user = message.author
            val channel = message.channelReceiver
            val filteree = message.content.replace("!excludesomeone ", "")
            user.sendMessage("Conduit filitered on: $filteree")
            map.remove(user)
            val members = message.channelReceiver.members.toList().filter { it.name.toLowerCase() != filteree.toLowerCase() && it != user }
            map.put(user, members)
            message.delete()
        }

        if (message.userReceiver != null && message.author in map.values.flatMap { it }) {
            map.entries.first { message.author in it.value }.key.sendMessage("Conduit (${message.author.name}): ${message.content}")
        } else if (message.userReceiver != null && message.author in map.keys && "!exclude" !in message.content) {
            map[message.author]!!.filter { it != message.author }.forEach { it.sendMessage("Conduit (${message.author.name}): ${message.content}") }
        }
        return super.onMessage(api, message)
    }

}


