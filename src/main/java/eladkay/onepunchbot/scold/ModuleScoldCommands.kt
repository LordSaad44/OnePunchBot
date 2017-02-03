package eladkay.onepunchbot.scold

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.entities.permissions.Role
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleScoldCommands : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" } && message.content.startsWith("!scold ", true)) {
            val server = message.channelReceiver.server
            val everyone: Role? = server.roles.firstOrNull { it.name == "@everyone" } ?: throw RuntimeException("g")
            val userName = message.content.replace("!scold ", "")
            println("Scolding $userName")
            val channel = server.createChannel("temp_$userName").get()
            channel.updateTopic("\$temp")
            val user = server.members.firstOrNull { it.name == userName } ?: throw RuntimeException("g${userName}g")
            val permission = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
            val permission2 = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
            channel.updateOverwrittenPermissions(user, permission)
            channel.updateOverwrittenPermissions(everyone, permission2)
        } else if (message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" } && message.content.startsWith("!endscold", true)) {
            val server = message.channelReceiver
            if (server.topic.contains("\$temp")) server.delete()
        } else if (message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" } && message.content.startsWith("!courtmode", true)) {
            val server = message.channelReceiver
            if (!server.topic.contains("\$temp")) return true
            val attornies = server.server.roles.firstOrNull { it.name == "Attorneys" } ?: throw RuntimeException("g")
            val users = mutableListOf<User>()
            attornies.users.forEach { users.add(it) }
            if (!server.topic.contains("\$court")) {
                val permission = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
                for (user in users)
                    server.updateOverwrittenPermissions(user, permission)
                server.updateTopic("${server.topic}\$court")
            } else {
                val permission2 = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
                for (user in users)
                    server.updateOverwrittenPermissions(user, permission2)
                server.updateTopic(server.topic.replace("\$court", ""))
            }
        } else if (message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" } && message.content.startsWith("!spectate", true)) {
            val server = message.channelReceiver
            if (!server.topic.contains("\$temp")) return true
            val everyone: Role = server.server.roles.firstOrNull { it.name == "@everyone" } ?: throw RuntimeException("g")
            val users = mutableListOf<User>()
            println("Spectating ${everyone.users}")
            everyone.users.forEach { users.add(it); message.reply(it.toString()) }
            if (!server.topic.contains("\$spectate")) {
                val permission = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
                /*for(user in users)
                    server.updateOverwrittenPermissions(user, permission)*/
                server.updateOverwrittenPermissions(everyone, permission)
                server.updateTopic("${server.topic}\$spectate")
            } else {
                val permission = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
                server.updateOverwrittenPermissions(everyone, permission)
                server.updateTopic(server.topic.replace("\$spectate", ""))
            }
        } else if (message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" } && message.content.startsWith("!endscold", true)) {
            val server = message.channelReceiver
            if (server.topic.contains("\$temp")) server.delete()
        }
        return super.onMessage(api, message)
    }
}