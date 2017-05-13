package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.Holder
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.startsWith

object ModuleInvokeScript : IModule {

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.author !in Holder.admins) return super.onMessage(api, message)
        if(message.startsWith("!invoke")) {
            val split = message.content.split(" ")
            val args = split.toList().subList(1, split.size)
            val id = args.takeIf { it.isNotEmpty() }
            val idAsInt: Int?
            if(id == null) {
                message.reply("Invalid script ID!")
                return super.onMessage(api, message)
            } else {
                idAsInt = id[0].toIntOrNull()
                if(idAsInt == null) {
                    message.reply("Non-int script ID! ${id.joinToString("")}")
                    return super.onMessage(api, message)
                }
            }
            try {
                val methodArgs = args.toList().subList(1, args.size)
                ModuleScripting.engine.eval(ModuleScripting.scripts[args[0].toInt()])
                message.reply(ModuleScripting.engine.invokeFunction("script_${args[0]}", message)?.toString() ?: "")
            } catch(e: Exception) {
                message.reply(e.toString())
            }
        }
        return super.onMessage(api, message)
    }

}