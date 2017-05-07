@file:WireDontTouchThisOrIllKillYouWhileYouSleep
package eladkay.onepunchbot

import com.google.common.reflect.ClassPath
import com.google.common.util.concurrent.FutureCallback
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.entities.permissions.Role
import de.btobastian.javacord.listener.message.MessageCreateListener
import de.btobastian.javacord.listener.message.MessageDeleteListener
import eladkay.onepunchbot.Holder.admins


val token = tokenHeld

object Holder {
    val adminChannels = mutableMapOf<String, Channel?>()
    lateinit var opm: Server
    val admins = mutableListOf<User>()
}
val Channel.members: List<User>
    get()
         = server.members.filter { this.getOverwrittenPermissions(it).getState(PermissionType.READ_MESSAGES) == PermissionState.ALLOWED || it.getRoles(server).any { it.name == "Admins"  || it.getOverwrittenPermissions(this).getState(PermissionType.READ_MESSAGES) == PermissionState.ALLOWED  } }

fun Server.getOrCreateRole(name: String): Role {
    return roles.firstOrNull { it.name == name } ?: createRole().get().apply { updateName(name) }
}
fun Server.getOrCreateChannel(name: String): Channel {
    return channels.firstOrNull { it.name == name } ?: createChannel(name).get()
}
fun Server.getChannel(name: String): Channel? {
    return channels.firstOrNull { it.name == name }
}
fun Message.startsWith(string: String) = content.startsWith(string, ignoreCase = true)
fun Message.remove(string: String) = content.replace(string, "")
fun slow(lambda: ()->Unit) {
    lambda()
    Thread.sleep(100)
}
fun tryCatch(server: Server, lambda: () -> Unit) {
    try {
        lambda()
    } catch(e: Throwable) {
        Holder.adminChannels[server.id]?.sendMessage(e.toString())
    }
}
fun tryCatch(message: String, lambda: () -> Unit) {
    try {
        lambda()
    } catch(e: Throwable) {
        println("$message: $e")
    }
}
/**
 * Created by Elad on 2/3/2017.
 */
fun main(args: Array<String>) {
    //val modules = mutableListOf<IModule>(
            //ModuleModlog, ModuleShellReader, ModuleIgnore, ModuleShellHandler, ModuleBotCourtesy, ModuleAdminCommands, ModuleScoldCommands, ModuleAutoripper, ModuleBotChoose, ModuleMath, ModuleAviation, ModuleNerdiness, ModuleNavySeals, ModuleSetup, ModulePoll, ModuleConduit, ModuleJava, ModuleHangman, ModuleTTT, ModuleVoiceChat
            //,ModuleDebug
    //)
    val modules = ClassPath.from(Main::class.java.classLoader).getTopLevelClassesRecursive("eladkay.onepunchbot.modules").map {
        var a: IModule? = null
        tryCatch(it.name) { a = it.load().getDeclaredField("INSTANCE").get(null) as? IModule }
        a
    }.filterNotNull()
    val api0 = Javacord.getApi(token, true)
    //api0.game = "saad's mom"
    api0.setAutoReconnect(false)
    api0.setWaitForServersOnStartup(false)
    modules.forEach { it.preInit(api0) }
    api0.connect(object : FutureCallback<DiscordAPI> {
        override fun onSuccess(api: DiscordAPI?) {
            modules.forEach { it.onInit(api0) }
            api!!

            // Wait for discord to catch up
            while (api0.servers.toMutableList().size == 0);

            for(server in api.servers) {
                Holder.adminChannels.put(server.id, server.getOrCreateChannel("admin-only"))
                if(server.id == "212123426356199425") {
                    server.getOrCreateRole("Admins").users.mapTo(admins) { it }
                    println("Admins: $admins")
                }
            }
            api.registerListener(MessageDeleteListener {
                api, message ->
                try {
                    modules.forEach { if (it.onMessageDeleted(api, message)) return@forEach }
                } catch(t: Throwable) {
                    val server = message.channelReceiver.server
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.message.MessageEditListener {
                api, message, old ->
                try {
                    modules.forEach { if (it.onMessageEdited(api, message, old)) return@forEach }
                    modules.forEach { it.processMessageOrEdit(message) }
                } catch(t: Throwable) {
                    val server = message.channelReceiver?.server
                    Holder.adminChannels[server?.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.user.UserRoleAddListener {
                api, user, role ->
                try {
                    modules.forEach { if (it.onUserRoleAdded(api, user, role)) return@forEach }
                } catch(t: Throwable) {
                    val server = role.server
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.user.UserRoleRemoveListener {
                api, user, role ->
                try {
                    modules.forEach { if (it.onUserRoleRemoved(api, user, role)) return@forEach }
                } catch(t: Throwable) {
                    val server = role.server
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.user.UserChangeNicknameListener {
                api, server, user, oldnick ->
                try {
                    modules.forEach { if (it.onUserChangeNick(api, server, user, oldnick)) return@forEach }
                } catch(t: Throwable) {
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberUnbanListener {
                api, userid, server ->
                try {
                    modules.forEach { if (it.onMemberUnban(api, userid, server)) return@forEach }
                } catch(t: Throwable) {
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberRemoveListener {
                api, user, server ->
                try {
                    modules.forEach { if (it.onMemberRemove(api, user, server)) return@forEach }
                } catch(t: Throwable) {
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberAddListener {
                api, user, server ->
                try {
                    modules.forEach { if (it.onMemberAdd(api, user, server)) return@forEach }
                } catch(t: Throwable) {
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberBanListener {
                api, user, server ->
                try {
                    modules.forEach { if (it.onMemberBan(api, user, server)) return@forEach }
                } catch(t: Throwable) {
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
            api.registerListener(MessageCreateListener {
                api, message ->
                try {
                    if (message.author.isYourself) return@MessageCreateListener
                    modules.forEach { if (it.onMessage(api, message)) return@forEach }
                    modules.forEach { it.processMessageOrEdit(message) }
                } catch(t: Throwable) {
                    val server: Server? = message.channelReceiver?.server
                    if(server == null) {
                        message.reply(t.stackTrace.joinToString("\n"))
                        return@MessageCreateListener
                    }
                    Holder.adminChannels[server.id]?.sendMessage(t.toString())
                }
            })
        }

        override fun onFailure(p0: Throwable?) {
            modules.forEach { it.onInitFailure(api0) }
        }

    })
}

/**
 * if wire touches classes annotated with this annotation he will automatically be :autoripped:
 */
@Target(AnnotationTarget.FILE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class WireDontTouchThisOrIllKillYouWhileYouSleep

interface IModule {
    fun processMessageOrEdit(message: Message) {

    }

    fun onMessage(api: DiscordAPI, message: Message): Boolean {
        return false
    }

    fun onMessageDeleted(api: DiscordAPI, message: Message): Boolean {
        return false
    }

    fun onMessageEdited(api: DiscordAPI, message: Message, old: String): Boolean {
        return false
    }

    fun onUserRoleAdded(api: DiscordAPI, user: User, role: Role): Boolean {
        return false
    }

    fun onUserRoleRemoved(api: DiscordAPI, user: User, role: Role): Boolean {
        return false
    }

    fun onUserChangeNick(api: DiscordAPI, server: Server, user: User, oldnick: String?): Boolean {
        return false
    }

    fun onMemberUnban(api: DiscordAPI, userid: String, server: Server): Boolean {
        return false
    }

    fun onMemberRemove(api: DiscordAPI, userid: User, server: Server): Boolean {
        return false
    }

    fun onMemberAdd(api: DiscordAPI, userid: User, server: Server): Boolean {
        return false
    }

    fun onMemberBan(api: DiscordAPI, userid: User, server: Server): Boolean {
        return false
    }

    fun onInit(api: DiscordAPI) {

    }

    fun preInit(api: DiscordAPI) {

    }

    fun onInitFailure(api: DiscordAPI) {

    }
}

abstract class CommandBase : IModule {
    abstract val paramCount: Int // -1 for vararg
    abstract val command: String
    companion object {
        const val commandChar = '!'
    }

    override fun processMessageOrEdit(message: Message) {
        if(message.startsWith("$commandChar$command${if (paramCount <= 0) "" else " "}")) {
            val args = message.remove("$commandChar$command${if (paramCount <= 0) "" else " "}")
            val split = args.split(" ")
            if(paramCount == split.size || paramCount == -1) processCommand(message, split.toTypedArray())
        }
        super.processMessageOrEdit(message)
    }

    abstract fun processCommand(message: Message, args: Array<String>)
}

//weep
class MessageStructure(templateIn: String) {
    var template: String = templateIn
        private set
}
