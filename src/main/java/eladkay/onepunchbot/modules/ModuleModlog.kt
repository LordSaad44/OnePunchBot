package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.Role
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleModlog : IModule {
    val handle = { a: String -> a.replace("`", "'"); "`$a`" }
    override fun onMessageDeleted(api: DiscordAPI, message: Message): Boolean {
        val modlog = message.channelReceiver.server.channels.firstOrNull { it.name == "modlog" } ?: return super.onMessageDeleted(api, message)
        if (message.channelReceiver != modlog) {
            if("<:autorip:277120850975653900>" !in message.content)
                modlog.sendMessage("Message by ${message.author.name} deleted: \"${handle(message.content)}\" in channel #${message.channelReceiver.name}")
            else
                modlog.sendMessage("Self destructing message by ${message.author.name} timed out: \"${handle(message.content)}\" in channel #${message.channelReceiver.name} after ${(5000 * "<:autorip:277120850975653900>".toRegex().findAll(message.content).toList().size.toLong())/1000} seconds")
        }
        return super.onMessageDeleted(api, message)
    }

    override fun onMessageEdited(api: DiscordAPI, message: Message, old: String): Boolean {
        
        val modlog = message.channelReceiver.server.channels.firstOrNull { it.name == "modlog" } ?: return super.onMessageDeleted(api, message)
        if (message.channelReceiver != modlog)
            modlog.sendMessage("Message by ${message.author.name} edited: \"${handle(old)}\" -> \"${handle(message.content)}\" in channel #${message.channelReceiver.name}")
        return super.onMessageEdited(api, message, old)
    }

    override fun onUserRoleAdded(api: DiscordAPI, user: User, role: Role): Boolean {
        val modlog = role.server.channels.firstOrNull { it.name == "modlog" } ?: return super.onUserRoleAdded(api, user, role)
        modlog.sendMessage("Roles of ${user.name} edited: Added ${role.name}")
        return super.onUserRoleAdded(api, user, role)
    }

    override fun onUserRoleRemoved(api: DiscordAPI, user: User, role: Role): Boolean {
        val modlog = role.server.channels.firstOrNull { it.name == "modlog" } ?: return super.onUserRoleRemoved(api, user, role)
        modlog.sendMessage("Roles of ${user.name} edited: Removed ${role.name}")
        return super.onUserRoleRemoved(api, user, role)
    }

    override fun onUserChangeNick(api: DiscordAPI, server: Server, user: User, oldnick: String?): Boolean {
        val modlog = server.channels.firstOrNull { it.name == "modlog" } ?: return super.onUserChangeNick(api, server, user, oldnick)
        val newNick = user.getNickname(server)
        if (newNick == null) modlog.sendMessage("${user.name}'s nickname was removed (was ${oldnick})")
        else if (oldnick != null) modlog.sendMessage("${user.name}'s nickname was changed from ${oldnick} to ${newNick}")
        else modlog.sendMessage("${user.name}'s nickname was set to ${user.getNickname(server)}")
        return super.onUserChangeNick(api, server, user, oldnick)
    }

    override fun onMemberUnban(api: DiscordAPI, userid: String, server: Server): Boolean {
        val modlog = server.channels.firstOrNull { it.name == "modlog" } ?: return super.onMemberUnban(api, userid, server)
        modlog.sendMessage("$userid was unbanned")
        return super.onMemberUnban(api, userid, server)
    }

    override fun onMemberAdd(api: DiscordAPI, userid: User, server: Server): Boolean {
        val modlog = server.channels.firstOrNull { it.name == "modlog" } ?: return super.onMemberAdd(api, userid, server)
        modlog.sendMessage("${userid.name} joined the server")
        return super.onMemberAdd(api, userid, server)
    }

    override fun onMemberRemove(api: DiscordAPI, userid: User, server: Server): Boolean {
        val modlog = server.channels.firstOrNull { it.name == "modlog" } ?: return super.onMemberRemove(api, userid, server)
        modlog.sendMessage("${userid.name} left the server")
        return super.onMemberRemove(api, userid, server)
    }

    override fun onMemberBan(api: DiscordAPI, userid: User, server: Server): Boolean {
        val modlog = server.channels.firstOrNull { it.name == "modlog" } ?: return super.onMemberBan(api, userid, server)
        modlog.sendMessage("${userid.name} was banned from the server")
        return super.onMemberBan(api, userid, server)
    }

}
