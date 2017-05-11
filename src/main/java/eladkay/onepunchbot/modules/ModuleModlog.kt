package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.Role
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.WireDontTouchThisOrIllKillYouWhileYouSleep
import eladkay.onepunchbot.getOrCreateChannel

/**
 * Created by Elad on 2/3/2017.
 */
@WireDontTouchThisOrIllKillYouWhileYouSleep
object ModuleModlog : IModule {

    val ignoreMessages = mutableListOf<Message>()


    val handle = { a: String -> a.replace("`", "'"); "`$a`" }

    override fun onMessageDeleted(api: DiscordAPI, message: Message): Boolean {
        if (ignoreMessages.any { it.id == message.id }) {
            ignoreMessages.removeIf { it.id == message.id }
            return super.onMessageDeleted(api, message)
        }

        if ("Hangman < ---- > Hangman" in message.content) return super.onMessageDeleted(api, message)

        val modlog = message.channelReceiver.server.getOrCreateChannel("modlog")
        if (message.channelReceiver != modlog && "conduit" !in message.content) {
            if (":autorip:" !in message.content)
                modlog.sendMessage("Message by ${message.author.name} deleted: \"${handle(message.content)}\" in channel #${message.channelReceiver.name}")
            else
                modlog.sendMessage("Self destructing message by ${message.author.name} timed out: \"${handle(message.content)}\" in channel #${message.channelReceiver.name} after ${(5000 * ":autorip:".toRegex().findAll(message.content).toList().size.toLong()) / 1000} seconds")
        }
        return super.onMessageDeleted(api, message)
    }

    override fun onMessageEdited(api: DiscordAPI, message: Message, old: String): Boolean {
        if (ignoreMessages.any { it.id == message.id })
            return super.onMessageEdited(api, message, old)

        val modlog = message.channelReceiver.server.getOrCreateChannel("modlog")
        if (message.channelReceiver != modlog && message !in ModulePoll.polls.map { it.message })
            modlog.sendMessage("Message by ${message.author.name} edited: \"${handle(old)}\" -> \"${handle(message.content)}\" in channel #${message.channelReceiver.name}")
        return super.onMessageEdited(api, message, old)
    }

    override fun onUserRoleAdded(api: DiscordAPI, user: User, role: Role): Boolean {
        val modlog = role.server.getOrCreateChannel("modlog")
        modlog.sendMessage("Roles of ${user.name} edited: Added ${role.name}")
        return super.onUserRoleAdded(api, user, role)
    }

    override fun onUserRoleRemoved(api: DiscordAPI, user: User, role: Role): Boolean {
        val modlog = role.server.getOrCreateChannel("modlog")
        modlog.sendMessage("Roles of ${user.name} edited: Removed ${role.name}")
        return super.onUserRoleRemoved(api, user, role)
    }

    override fun onUserChangeNick(api: DiscordAPI, server: Server, user: User, oldnick: String?): Boolean {
        val modlog = server.getOrCreateChannel("modlog")
        val newNick = user.getNickname(server)
        if (newNick == null) modlog.sendMessage("${user.name}'s nickname was removed (was ${oldnick})")
        else if (oldnick != null) modlog.sendMessage("${user.name}'s nickname was changed from ${oldnick} to ${newNick}")
        else modlog.sendMessage("${user.name}'s nickname was set to ${user.getNickname(server)}")
        return super.onUserChangeNick(api, server, user, oldnick)
    }

    override fun onMemberUnban(api: DiscordAPI, userid: String, server: Server): Boolean {
        val modlog = server.getOrCreateChannel("modlog")
        modlog.sendMessage("$userid was unbanned")
        return super.onMemberUnban(api, userid, server)
    }

    override fun onMemberAdd(api: DiscordAPI, userid: User, server: Server): Boolean {
        val modlog = server.getOrCreateChannel("modlog")
        modlog.sendMessage("${userid.name} joined the server")
        return super.onMemberAdd(api, userid, server)
    }

    override fun onMemberRemove(api: DiscordAPI, userid: User, server: Server): Boolean {
        val modlog = server.getOrCreateChannel("modlog")
        modlog.sendMessage("${userid.name} left the server")
        return super.onMemberRemove(api, userid, server)
    }

    override fun onMemberBan(api: DiscordAPI, userid: User, server: Server): Boolean {
        val modlog = server.getOrCreateChannel("modlog")
        modlog.sendMessage("${userid.name} was banned from the server")
        return super.onMemberBan(api, userid, server)
    }

}
