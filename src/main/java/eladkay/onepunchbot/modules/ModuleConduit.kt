package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 3/13/2017.
 */
fun <K, V> Map<K, V>.reverse(): Map<V, K> {
    val map = mutableMapOf<V, K>()
    for((k, v) in this) map.put(v, k)
    return map
}
val Channel.members: List<User>
    get()
        = server.members.filter { this.getOverwrittenPermissions(it).getState(PermissionType.READ_MESSAGES) == PermissionState.ALLOWED || it.getRoles(server).any { it.name == "Admins"  || it.getOverwrittenPermissions(this).getState(PermissionType.READ_MESSAGES) == PermissionState.ALLOWED  } }

object ModuleConduit : IModule {
    val map = mutableMapOf<User, List<User>>()
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.channelReceiver?.server != null && message.content.startsWith("!startconduit") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val channel = message.channelReceiver
            val user = message.author
            user.sendMessage("Conduit established.")
            val members = channel.members.toList().filter { it != user }
            map.put(user, members)
            message.delete()
        } else if(message.userReceiver != null && message.content.startsWith("!endconduit")) {
            val user = message.author
            user.sendMessage("Conduit closed.")
            map.remove(user)
            message.delete()
        } else if(message.channelReceiver?.server != null && message.content.startsWith("!excludesomeone ") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val user = message.author
            val channel = message.channelReceiver
            val filteree = message.content.replace("!excludesomeone ", "")
            user.sendMessage("Conduit filitered on: $filteree")
            map.remove(user)
            val members = message.channelReceiver.members.toList().filter { it.name.toLowerCase() != filteree.toLowerCase() && it != user }
            map.put(user, members)
            message.delete()
        }

        if(message.userReceiver != null && message.author in map.values.flatMap { it }) {
            map.entries.first { message.author in it.value }.key.sendMessage("Conduit (${message.author.name}): ${message.content}")
        } else if(message.userReceiver != null && message.author in map.keys && "!exclude" !in message.content) {
            map[message.author]!!.filter { it != message.author }.forEach { it.sendMessage("Conduit (${message.author.name}): ${message.content}") }
        }
        return super.onMessage(api, message)
    }

}