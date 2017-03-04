package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.ReactionAddListener
import de.btobastian.javacord.listener.message.ReactionRemoveListener
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 3/4/2017.
 */
object ModulePoll : IModule {
    class Poll(var votes: Int = 0, val message: Message, val orig: String)
    val polls = mutableMapOf<String, Poll>()
    override fun onInit(api: DiscordAPI) {
        api.registerListener(ReactionAddListener {
            api, reaction, user ->
            if(!polls.values.any { it.message == reaction.message }) return@ReactionAddListener
            if((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsup")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👍"))) {
                val poll = polls.values.first { it.message == reaction.message }.apply { votes++ }
                reaction.message.edit("${poll.orig}? Votes: ${poll.votes}")
            } else if((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsdown")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👎"))) {
                val poll = polls.values.first { it.message == reaction.message }.apply { votes-- }
                reaction.message.edit("${poll.orig}? Votes: ${poll.votes}")
            }
        })
        api.registerListener(ReactionRemoveListener {
            api, reaction, user ->
            if((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsup")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👍"))) {
                val poll = polls.values.first { it.message == reaction.message }.apply { votes-- }
                reaction.message.edit("${poll.orig}? Votes: ${poll.votes}")
            } else if((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsdown")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👎"))) {
                val poll = polls.values.first { it.message == reaction.message }.apply { votes++ }
                reaction.message.edit("${poll.orig}? Votes: ${poll.votes}")
            }
        })
        super.onInit(api)
    }
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.content.startsWith("!poll ")) {
            var first = true
            val contents = message.content.replace("!poll ", "").replace("on", "").toCharArray().map { var something = it; if(first) { something = it.toUpperCase(); first = false}; something }.joinToString("")
            val msg = message.reply(contents + "?").get()
            polls.put(contents, Poll(message = msg, orig = contents))

        }
        return super.onMessage(api, message)
    }
}