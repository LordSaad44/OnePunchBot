package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.setInterval

/**
 * @author WireSegal
 * Created at 2:45 PM on 5/7/17.
 */
object ModuleGameChanger : IModule {
    var currentIndex = 0

    val games = arrayOf(
            "Half-Life 3",
            "Thaumcraft 7",
            "XyCraft"
    )

    override fun onInit(api: DiscordAPI) {
        setInterval(60000) {
            api.game = games[currentIndex++ % games.size]
        }
    }
}
