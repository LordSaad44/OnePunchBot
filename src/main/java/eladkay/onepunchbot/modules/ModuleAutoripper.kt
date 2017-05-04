package eladkay.onepunchbot.modules

import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleAutoripper : IModule {

    override fun processMessageOrEdit(message: Message) {
        if (message.content.matches("(?:^|[^/]):autorip:".toRegex()) && message.channelReceiver.name != "modlog") {
            Thread {
                Thread.sleep(5000 * ":autorip:".toRegex().findAll(message.content).toList().size.toLong())
                message.delete()
            }.start()
        }
    }
}
