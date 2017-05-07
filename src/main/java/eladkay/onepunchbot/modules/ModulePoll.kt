package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.ReactionAddListener
import de.btobastian.javacord.listener.message.ReactionRemoveListener
import eladkay.onepunchbot.IModule
import java.util.*

/**
 * Created by Elad on 3/4/2017.
 */
object ModulePoll : IModule {
    class Poll(var votes: Int = 0, val message: Message, val orig: String, val id: Long)

    val polls = mutableListOf<Poll>()
    override fun onInit(api: DiscordAPI) {
        api.registerListener(ReactionAddListener {
            api, reaction, user ->
            if (!polls.any { it.id.toString() in reaction.message.content }) {
                //println(reaction.message.content)
                return@ReactionAddListener
            }
            if ((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsup")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👍"))) {
                var uuid = 0.toLong()
                val poll = polls.first { it.id.toString() in reaction.message.content }.apply { votes++; uuid = id }
                reaction.message.edit("(id: $uuid) Poll (vote with 👍 and 👎 reactions): ${poll.orig}? Votes: ${poll.votes}")
            } else if ((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsdown")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👎"))) {
                var uuid = 0.toLong()
                val poll = polls.first { it.id.toString() in reaction.message.content }.apply { votes--; uuid = id }
                reaction.message.edit("(id: $uuid) Poll (vote with 👍 and 👎 reactions): ${poll.orig}? Votes: ${poll.votes}")
            } //else println(reaction.unicodeEmoji)
        })
        api.registerListener(ReactionRemoveListener {
            api, reaction, user ->
            if (!polls.any { it.id.toString() in reaction.message.content }) {
                println(reaction.message.content)
                //return@ReactionRemoveListener
            }

            if ((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsup")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👍"))) {
                var uuid = 0.toLong()
                val poll = polls.first { it.id.toString() in reaction.message.content }.apply { votes--; uuid = id }
                reaction.message.edit("(id: $uuid) Poll (vote with 👍 and 👎 reactions): ${poll.orig}? Votes: ${poll.votes}")
            } else if ((reaction.isCustomEmoji && reaction.customEmoji.name.contains("thumbsdown")) || (reaction.isUnicodeEmoji && reaction.unicodeEmoji.contains("👎"))) {
                var uuid = 0.toLong()
                val poll = polls.first { it.id.toString() in reaction.message.content }.apply { votes++; uuid = id }
                reaction.message.edit("(id: $uuid) Poll (vote with 👍 and 👎 reactions): ${poll.orig}? Votes: ${poll.votes}")
            }
        })
        super.onInit(api)
    }

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.startsWith("!poll ")) {
            var first = true
            val contents = message.content.replace("!poll ", "").replace("on", "").toCharArray().map {
                var something = it; if (first) {
                something = it.toUpperCase(); first = false
            }; something
            }.joinToString("")
            val uuid = UUID.randomUUID().leastSignificantBits % 1000
            val msg = message.reply("(id: $uuid) Poll (vote with 👍 and 👎 reactions): $contents? Votes: 0").get()
            polls.add(Poll(message = msg, orig = contents, id = uuid))

        } else if (message.content.startsWith("!pollpolls ")) {
            val contents = message.content.replace("!pollpolls ", "")
            message.reply(polls.firstOrNull { it.orig == contents }.toString())
        }
        return super.onMessage(api, message)
    }
}
