package eladkay.onepunchbot

import com.google.common.util.concurrent.FutureCallback
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.Role
import de.btobastian.javacord.listener.message.MessageCreateListener
import de.btobastian.javacord.listener.message.MessageDeleteListener
import eladkay.onepunchbot.misc.*
import eladkay.onepunchbot.scold.ModuleScoldCommands
import eladkay.onepunchbot.shell.ModuleShellHandler
import eladkay.onepunchbot.shell.ModuleShellReader


val token = tokenHeld
/**
 * Created by Elad on 2/3/2017.
 */
fun main(args: Array<String>) {
    val modules = listOf<IModule>(
            ModuleWait, ModuleModlog, ModuleShellReader, ModuleIgnore, ModuleShellHandler, ModuleBotCourtesy, ModuleAdminCommands, ModuleScoldCommands, ModuleAutoripper
    )
    val api0 = Javacord.getApi(token, true)
    api0.game = "saad's mom"
    api0.setAutoReconnect(false)
    api0.setWaitForServersOnStartup(false)
    modules.forEach { it.preInit(api0) }
    api0.connect(object : FutureCallback<DiscordAPI> {
        override fun onSuccess(api: DiscordAPI?) {
            modules.forEach { it.onInit(api0) }
            api!!
            api.registerListener(MessageDeleteListener {
                api, message -> modules.forEach { if(it.onMessageDeleted(api, message)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.message.MessageEditListener {
                api, message, old -> modules.forEach { if(it.onMessageEdited(api, message, old)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.user.UserRoleAddListener {
                api, user, role -> modules.forEach { if(it.onUserRoleAdded(api, user, role)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.user.UserRoleRemoveListener {
                api, user, role -> modules.forEach { if(it.onUserRoleRemoved(api, user, role)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.user.UserChangeNicknameListener {
                api, server, user, oldnick-> modules.forEach { if(it.onUserChangeNick(api, server, user, oldnick)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberUnbanListener {
                api, userid, server -> modules.forEach { if(it.onMemberUnban(api, userid, server)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberRemoveListener {
                api, user, server -> modules.forEach { if(it.onMemberRemove(api, user, server)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberAddListener {
                api, user, server -> modules.forEach { if(it.onMemberAdd(api, user, server)) return@forEach  }
            })
            api.registerListener(de.btobastian.javacord.listener.server.ServerMemberBanListener {
                api, user, server -> modules.forEach { if(it.onMemberBan(api, user, server)) return@forEach  }
            })
            api.registerListener(MessageCreateListener {
                api, message -> modules.forEach { if(it.onMessage(api, message)) return@forEach }
            })
        }

        override fun onFailure(p0: Throwable?) {
            modules.forEach { it.onInitFailure(api0) }
        }

    })
}

interface IModule {
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
    fun onUserChangeNick(api: DiscordAPI, server: Server, user: User, oldnick: String): Boolean {
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