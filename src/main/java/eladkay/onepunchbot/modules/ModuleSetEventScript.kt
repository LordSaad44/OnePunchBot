package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.Role
import eladkay.onepunchbot.Holder
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.startsWith

/**
 * Created by Elad on 5/12/2017.
 */
object ModuleSetEventScript : IModule {

    enum class EnumEventType {
        DELETE, EDIT, USER_ROLE_ADD, USER_ROLE_REMOVE, USER_CHANGE_NICK, UNBAN, MEMBER_ADD, MEMBER_REMOVE, BAN, MESSAGE;
        fun getName() = name.toLowerCase()
    }

    val events = mutableMapOf<EnumEventType, MutableList<Int>>()

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.startsWith("!setevent ") && message.author in Holder.admins) {
            println("Setting event")
            val split = message.content.split(" ")
            val args = split.toList().subList(1, split.size)
            val id = args.takeIf { it.isNotEmpty() }
            val idAsInt: Int?
            if (id == null) {
                message.reply("Invalid script ID!")
                return super.onMessage(api, message)
            } else {
                idAsInt = id[0].toIntOrNull()
                if (idAsInt == null) {
                    message.reply("Non-int script ID! ${id.joinToString("")}")
                    return super.onMessage(api, message)
                }
            }
            val type = EnumEventType.values().firstOrNull { it.getName() == args[1] }
            if(type == null) {
                message.reply("Invalid event type! Valid types: ${EnumEventType.values().joinToString()}")
                return super.onMessage(api, message)
            }
            Thread {
                ModuleScripting.engine.eval(ModuleScripting.scripts[idAsInt])
                events.getOrPut(type) { mutableListOf() }.add(idAsInt)
            }.start()
            message.reply("Added $idAsInt to $type")
        } else {
            events[EnumEventType.MESSAGE]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", message)?.toString() }
        }
        return super.onMessage(api, message)
    }

    override fun onMessageDeleted(api: DiscordAPI, message: Message): Boolean {
        events[EnumEventType.DELETE]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", message)?.toString() }
        return super.onMessageDeleted(api, message)
    }

    override fun onMessageEdited(api: DiscordAPI, message: Message, old: String): Boolean {
        events[EnumEventType.EDIT]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", message)?.toString() }
        return super.onMessageEdited(api, message, old)
    }

    override fun onUserRoleAdded(api: DiscordAPI, user: User, role: Role): Boolean {
        events[EnumEventType.USER_ROLE_ADD]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", user)?.toString() }
        return super.onUserRoleAdded(api, user, role)
    }

    override fun onUserRoleRemoved(api: DiscordAPI, user: User, role: Role): Boolean {
        events[EnumEventType.USER_ROLE_REMOVE]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", user)?.toString() }
        return super.onUserRoleRemoved(api, user, role)
    }

    override fun onUserChangeNick(api: DiscordAPI, server: Server, user: User, oldnick: String?): Boolean {
        events[EnumEventType.USER_CHANGE_NICK]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", user)?.toString() }
        return super.onUserChangeNick(api, server, user, oldnick)
    }

    override fun onMemberUnban(api: DiscordAPI, userid: String, server: Server): Boolean {
        events[EnumEventType.UNBAN]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", server)?.toString() }
        return super.onMemberUnban(api, userid, server)
    }

    override fun onMemberAdd(api: DiscordAPI, userid: User, server: Server): Boolean {
        events[EnumEventType.MEMBER_ADD]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", userid)?.toString() }
        return super.onMemberAdd(api, userid, server)
    }

    override fun onMemberRemove(api: DiscordAPI, userid: User, server: Server): Boolean {
        events[EnumEventType.USER_ROLE_REMOVE]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", userid)?.toString() }
        return super.onMemberRemove(api, userid, server)
    }

    override fun onMemberBan(api: DiscordAPI, userid: User, server: Server): Boolean {
        events[EnumEventType.BAN]?.forEach { ModuleScripting.engine.invokeFunction("script_$it", userid)?.toString() }
        return super.onMemberBan(api, userid, server)
    }
}