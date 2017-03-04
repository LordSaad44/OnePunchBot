package eladkay.onepunchbot.modules

import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/5/2017.
 */
object ModuleDebug : IModule {
    override fun processMessageOrEdit(message: Message) {
        println(message.content)
    }
}