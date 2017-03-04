package eladkay.onepunchbot

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.entities.permissions.Role

/**
 * Created by Elad on 3/4/2017.
 */
object ModuleSetup : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        val reply: (String)->Unit = {
            message.reply(it)
            Thread.sleep(50)
        }
        val doTryCatched: (()->Unit)->Unit = {
            try {
                it()
            } catch(e: Exception) {
                reply("Error: $e")
            }
        }
        if(message.channelReceiver.name == "setup" && message.content == "!startsetup") {
            reply("Welcome to One Punch Bot!")
            reply("Let's begin with a little bit of setup.")
            reply("If you see any errors, please make an Admins role with every permission and apply it to the bot temporarily.")
            val server = message.channelReceiver.server
            var adminRole: Role? = null
            if(!api.yourself.getRoles(server).any { it.permissions.getState(PermissionType.ADMINISTATOR) == PermissionState.ALLOWED }) {
                reply("Please give the bot an admin role before proceeding.")
            }
            if(!server.roles.any { it.name == "Admins" }) {
                reply("Creating Admins role...")
                doTryCatched { server.createRole().get().apply { updateName("Admins") }.apply { adminRole = this } }
                reply("Done. Update this role's permissions to \"Administrator\" and apply it to your server admins.")
            }
            if(!server.channels.any { it.name == "admin-only" }) {
                reply("Creating admin-only channel...")
                doTryCatched {
                    val permissionsAllowed = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build()
                    val permissionsDenied = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).build()
                    val everyone: Role = server.roles.firstOrNull { it.name == "@everyone" } ?: throw RuntimeException("No everyone role?")
                    server.createChannel("admin-only").get().apply { updateOverwrittenPermissions(adminRole!!, permissionsAllowed) }.apply { updateOverwrittenPermissions(everyone, permissionsDenied) }.apply { Holder.adminChannels.put(server.id, this) }
                }
                reply("Done.")
            }
            if(!server.channels.any { it.name == "modlog" }) {
                reply("Creating modlog channel...")
                doTryCatched {
                    val permissionsAllowed = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build()
                    val permissionsDenied = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).build()
                    val everyone: Role = server.roles.firstOrNull { it.name == "@everyone" } ?: throw RuntimeException("No everyone role?")
                    server.createChannel("modlog").get().apply { updateOverwrittenPermissions(adminRole!!, permissionsAllowed) }.apply { updateOverwrittenPermissions(everyone, permissionsDenied) }
                }
                reply("Done.")
            }
            if(!server.channels.any { it.name == "shell" }) {
                reply("Creating shell channel... (currently unused)")
                doTryCatched {
                    val permissionsAllowed = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build()
                    val permissionsDenied = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).build()
                    val everyone: Role = server.roles.firstOrNull { it.name == "@everyone" } ?: throw RuntimeException("No everyone role?")
                    server.createChannel("shell").get().apply { updateOverwrittenPermissions(adminRole!!, permissionsAllowed) }.apply { updateOverwrittenPermissions(everyone, permissionsDenied) }
                }
                reply("Done.")
            }
            reply("In order to enjoy a complete One Punch Bot experience, we're going to ask you to add a few emojis to the server.")
            reply("One Punch Bot is built to work with these emojis, and it is important that they are named exactly as we ask. The image itself can be changed.")
            reply(":kt:         https://cdn.discordapp.com/emojis/230142911751258122.png")
            reply(":balance:         https://cdn.discordapp.com/emojis/270964604438315020.png")
            reply(":latin:         https://cdn.discordapp.com/emojis/282944396112953344.png")
            reply(":autorip         https://cdn.discordapp.com/emojis/277120850975653900.png")
            reply("Once you have added the emojis, One Punch Bot is fully set up and ready to use! Enjoy!")
        }
        return super.onMessage(api, message)
    }
}