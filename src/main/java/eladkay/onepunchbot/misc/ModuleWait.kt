package eladkay.onepunchbot.misc

import de.btobastian.javacord.DiscordAPI
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 2/3/2017.
 */
object ModuleWait : IModule {
    override fun onInit(api: DiscordAPI) {
        while (api.servers?.toMutableList()?.size != 1);
    }
}