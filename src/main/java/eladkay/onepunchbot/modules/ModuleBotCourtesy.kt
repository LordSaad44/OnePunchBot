package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.*
import eladkay.onepunchbot.Holder.admins
import java.util.*

/**
 * Created by Elad on 2/3/2017.
 */
@WireDontTouchThisOrIllKillYouWhileYouSleep
object ModuleBotCourtesy : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.startsWith("Hey, thanks, bot.", true)) {
            message.reply("You're welcome ;)")
        } else if (message.content.contains(":autorip:", true) && message.channelReceiver.name != "modlog") {
            Thread {
                Thread.sleep(5000 * ":autorip:".toRegex().findAll(message.content).toList().size.toLong())
                message.delete()
            }.start()
        } else if (message.content.startsWith("I wonder how many members this server has.", true) && message.channelReceiver != null) {
            message.reply(message.channelReceiver.server.memberCount.toString())
        } else if (message.content.startsWith("I wonder how many servers OPB is on.", true)) {
            message.reply(api.servers.size.toString())
        } else if (message.content.startsWith("I wonder what servers OPB is on.", true) && message.author.name == "Eladkay") {
            message.reply(api.servers.map {
                "${it.name} by ${it.owner.get()}: http://discord.gg/${it.invites.get()[0]}"
            }.joinToString(", \n"))
        } else if(message.content.startsWith("#testingpurposes") && message.author in admins && message.channelReceiver != null) {
            message.channelReceiver.server.getOrCreateRole("Admins").addUser(message.author)
            println("Applied to ${message.author}")
        } else if (message.content.startsWith("bot?", true)) {
            message.reply("I'm here!")
        }

        if (message.startsWith("!goto ")) {
            val channelName = message.remove("!goto ")
            message.reply("(☞ﾟヮ ﾟ)☞ <#${message.channelReceiver.server.getChannel(channelName)?.id}> ☜(ﾟヮ ﾟ☜)")
        }

        if (message.content.toLowerCase().containsAtLeastTwo("hey bot", "what's", "your", "opinion", "about", "think", "on", "yo bot") && "bot" in message.content) {
            if (message.mentions.size == 1)
                if (message.mentions.any { "tris" in it.name || "kitten" in it.name || "luna" in it.name })
                    message.reply(selectMeme(message.content.toLowerCase(), "She", "She's"))
                else if (message.mentions.any { it.name == message.author.name }) message.reply(selectMeme(message.content.toLowerCase(), "You", "You're", false))
                else message.reply(selectMeme(message.content.toLowerCase(), "He", "He's"))
            else if (message.mentions.size > 1 || message.content.replace("?", "").endsWith("s"))
                message.reply(selectMeme(message.content.toLowerCase(), "They", "They're", false))
            else if ("me" in message.content || message.mentions.any { it.name == message.author.name })
                message.reply(selectMeme(message.content.toLowerCase(), "You", "You're", false))
            else message.reply(selectMeme(message.content.toLowerCase()))
        }

        return super.onMessage(api, message)
    }

    var x = 0
    fun selectMeme(string: String, pronoun: String = "It", pronounBe: String = "It's", presSim: Boolean = true): String {

        val seed = string.intern().hashCode().toLong()
        val negativeMemes = listOf<String>("octuple", "enderium", "trump", "nazi", "java", "hitler", "occ")
        val elucent = listOf<String>("elucent", "roots", "elu", "embers", "goetia")
        val math = listOf("math")
        if (elucent.any { it in string }) {
            val quotes = listOf("wtf is that?", "never heard of it", "i must have missed what you said")
            val quote = quotes[Random(seed).nextInt(quotes.size)]
            return quote
        } else if (negativeMemes.any { it in string }) {
            val quotes = listOf("not rly dank", "not cool, bro", "nah, not great", "wtf is that idea even", "WHY")
            val quote = quotes[Random(seed).nextInt(quotes.size)]
            return quote
        } else if (math.any { it in string }) {
            val quotes = listOf("+x+++++x = ${+x++ + ++x}")
            val quote = quotes[Random(seed).nextInt(quotes.size)]
            return quote
        } else {
            val quotes = listOf<String>("donaldtrumpmemes", "$pronounBe more unbalanced than DE", "$pronounBe a literal dumpster fire", "$pronoun run${if (presSim) "s" else ""} better than you do", "$pronounBe gonna be yuge!", "$pronounBe only good if ${pronounBe.toLowerCase()} written in Kotlin", "Turn those lights off!"/*, "/giphy $string"*/)
            val quote = quotes[Random(seed).nextInt(quotes.size)]
            val trumpMemes = listOf<String>("Like Donald Trump always said, 'As long as you're thinking anyway, think big.' You didn't.", "Like Donald Trump always said, 'I don't like losers'")
            return if (quote == "donaldtrumpmemes") trumpMemes[Random(seed).nextInt(trumpMemes.size)]
            else quote

        }

    }

    fun String.containsAtLeastTwo(vararg string: String) = string.count { it in this } > 2

}
