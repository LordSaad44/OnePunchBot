package eladkay.onepunchbot.modules

import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.CommandBase
import eladkay.onepunchbot.Holder.admins
import jdk.nashorn.api.scripting.NashornScriptEngine
import javax.script.ScriptEngineManager



/**
 * Created by Elad on 5/12/2017.
 */
object ModuleScripting : CommandBase() {
    override val paramCount: Int
        get() = -1
    override val command: String
        get() = "definescript"

    var engineManager = ScriptEngineManager()
    var engine = engineManager.getEngineByName("nashorn") as NashornScriptEngine

    var lastId = 0
    val scripts = mutableMapOf<Int, String>()
    override fun processCommand(message: Message, args: Array<String>) {
        if(message.author !in admins) return
        try {
            lastId++
            val method = "function script_$lastId(obj) { ${args.joinToString(" ")} }"
            scripts.put(lastId, method)
            message.reply("Script ID: ${lastId}")
        } catch (e: Exception) {
            message.reply(e.toString())
        }
    }
}

