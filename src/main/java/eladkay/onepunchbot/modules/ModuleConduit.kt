package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 3/13/2017.
 */
fun <K, V> Map<K, V>.reverse(): Map<V, K> {
    val map = mutableMapOf<V, K>()
    for((k, v) in this) map.put(v, k)
    return map
}
object ModuleConduit : IModule {
    val map = mutableMapOf<User, List<User>>()
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.channelReceiver?.server != null && message.content.startsWith("!startconduit") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val channel = message.channelReceiver
            val user = message.author
            user.sendMessage("Conduit established.")
            map.put(user, channel.server.members.toList())
            message.delete()
        } else if(message.userReceiver != null && message.author in map.keys && "!exclude" !in message.content) {
            map[message.author]!!.filter { it != message.author }.forEach { it.sendMessage("Conduit (${message.author}): ${message.content}") }
        } else if(message.channelReceiver?.server != null && message.content.startsWith("!endconduit") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val user = message.author
            user.sendMessage("Conduit closed.")
            map.remove(user)
            message.delete()
        } else if(message.author != null && message.author in map.values.flatMap { it }) {
            map.entries.first { message.author in it.value }.key.sendMessage("Conduit (${message.author.name}): ${message.content}")
        }
        return super.onMessage(api, message)
    }

}