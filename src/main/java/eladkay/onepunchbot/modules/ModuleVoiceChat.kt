package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.listener.voice.UserJoinVoiceChannelListener
import de.btobastian.javacord.listener.voice.UserLeaveVoiceChannelListener
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.getOrCreateRole

/**
 * Created by Elad on 4/29/2017.
 */
object ModuleVoiceChat : IModule {
    override fun onInit(api: DiscordAPI) {
        api.registerListener(UserJoinVoiceChannelListener {
            api, user, channel ->
            channel.server.getOrCreateRole("Voice Chat").addUser(user)
        })
        api.registerListener(UserLeaveVoiceChannelListener {
            api, user ->
            api.servers.forEach {
                if (it.roles.any { it.name == "Voice Chat" })
                    it.roles.first { it.name == "Voice Chat" }.removeUser(user)
            }

        })
        super.onInit(api)
    }
}
