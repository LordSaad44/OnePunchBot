package eladkay.onepunchbot

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.entities.permissions.Role

/**
 * Created by Elad on 3/4/2017.
 */
object ModuleSetup : IModule {

    override fun onMemberAdd(api: DiscordAPI, userid: User, server: Server): Boolean {
        if(userid.isYourself)
            server.channels.first { "general" in it.name }.sendMessage("Welcome to One Punch Bots! Please create channel named \"setup\" and type !startsetup there to continue setting up OPB.")
        return super.onMemberAdd(api, userid, server)
    }

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        val reply: (String)->Unit = {
            message.reply(it)
            Thread.sleep(200)
        }
        val doTryCatched: (()->Unit)->Unit = {
            try {
                it()
            } catch(e: Exception) {
                reply("Error: $e")
            }
            Thread.sleep(50)
        }
        val server = message.channelReceiver?.server ?: return true
        var adminRole: Role? = null
        if(message.channelReceiver.name == "setup" && message.content == "!startsetup") {
            reply("Welcome to One Punch Bot!")
            reply("Let's begin with a little bit of setup.")
            reply("If you see any errors, please make an Admins role with every permission and apply it to the bot temporarily.")

            Thread.sleep(5000)
            if(!api.yourself.getRoles(server).any { it.permissions.getState(PermissionType.ADMINISTATOR) == PermissionState.ALLOWED }) {
                reply("Please give the bot an admin role before proceeding.")
                return super.onMessage(api, message)
            }
            if(!server.roles.any { it.name == "Admins" }) {
                reply("Creating Admins role...")
                doTryCatched { server.createRole().get().apply { updateName("Admins") }.apply { adminRole = this } }
                reply("Done. Update this role's permissions to \"Administrator\" and apply it to your server admins.")
            } else adminRole = server.roles.first { it.name == "Admins" }
            Thread.sleep(5000)
            if(!server.roles.any { it.name == "Moderators" }) {
                reply("Creating Moderators role...")
                doTryCatched { server.createRole().get().apply { updateName("Moderators") } }
                reply("Done. Update this role's permissions to whatever permissions your server moderators need and apply it to them.")
            }
            Thread.sleep(5000)
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
            Thread.sleep(5000)
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
            Thread.sleep(5000)
            /*if(!server.channels.any { it.name == "shell" }) {
                reply("Creating shell channel... (currently unused)")
                doTryCatched {
                    val permissionsAllowed = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build()
                    val permissionsDenied = api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).build()
                    val everyone: Role = server.roles.firstOrNull { it.name == "@everyone" } ?: throw RuntimeException("No everyone role?")
                    server.createChannel("shell").get().apply { updateOverwrittenPermissions(adminRole!!, permissionsAllowed) }.apply { updateOverwrittenPermissions(everyone, permissionsDenied) }
                }
                reply("Done.")
            }*/
            Thread.sleep(5000)
            reply("In order to enjoy a complete One Punch Bot experience, we're going to ask you to add a few emojis to the server.")
            reply("One Punch Bot is built to work with these emojis, and it is important that they are named exactly as we ask. The image itself can be changed.")
            reply(":kt:         https://cdn.discordapp.com/emojis/230142911751258122.png")
            Thread.sleep(5000)
            reply(":balance:         https://cdn.discordapp.com/emojis/270964604438315020.png")
            reply(":latin:         https://cdn.discordapp.com/emojis/282944396112953344.png")
            reply(":autorip:         https://cdn.discordapp.com/emojis/277120850975653900.png")
            Thread.sleep(5000)
            reply("Once you have added the emojis, One Punch Bot is fully set up and ready to use! Enjoy!")
            reply("You may now delete this channel. If you wish to see this message again, you may do !startsetup again now or in the future.")
            reply("If you wish to undo the changes done to your server in the process of this installation, please type !uninstall in a #setup channel. You need the Admins role to do that.")
            Thread.sleep(5000)
        } else if(message.channelReceiver.name == "setup" && message.content == "!uninstall" && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            reply("We are sorry you decided to uninstall One Punch Bot from your server.")
            reply("We will now uninstall all OPB related channels and roles. After that, you may install it back up using !startsetup or kick OPB from your server to finalize the uninstallation.")
            reply("One Punch Bot will not cease to work until you actually kick it out. Only the changes made in the setup process will be undone.")
            Thread.sleep(5000)
            reply("Deleting #modlog...")
            doTryCatched {
                server.channels.first { it.name == "modlog" }.delete()
            }
            reply("Done.")
            Thread.sleep(5000)
            reply("Deleting #admin-only...")
            doTryCatched {
                server.channels.first { it.name == "admin-only" }.delete()
                Holder.adminChannels.remove(server.id)
            }
            reply("Done.")
            Thread.sleep(5000)
            reply("Deleting Admins...")
            doTryCatched {
                server.roles.first { it.name == "Admins" }.delete()
            }
            reply("Done.")
            Thread.sleep(5000)
            reply("To finalize the deletion, please delete the OPB emojis: :kt:, :balance:, :latin:, :autorip:")
            reply("Thank you, and goodbye.")
        }
        return super.onMessage(api, message)
    }
}