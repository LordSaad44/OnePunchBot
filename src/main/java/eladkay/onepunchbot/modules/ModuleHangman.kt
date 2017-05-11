package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.LargeStringHolder
import java.awt.Color
import java.util.*

/**
 * Created by Elad on 4/15/2017.
 */
object ModuleHangman : IModule {
    class Hangman(val word: String, val creator: String) {
        companion object {
            private val alphabet = (0 until 26).map { (it + 0x61).toChar() }.joinToString("")
        }

        var lastMessage: Message? = null

        fun start(channel: Channel) {
            channel.sendMessage("$creator has started a game of Hangman!")
            hangman.put(channel, this)
            update(channel)
        }

        fun update(channel: Channel) {
            val last = lastMessage
            last?.delete()
            lastMessage = channel.sendMessage(createMessage()).get()
            ModuleModlog.ignoreMessages.add(lastMessage!!)
        }

        fun endAndQueue(channel: Channel) {
            lastMessage?.delete()
            hangman.remove(channel)

            if (q.getOrPut(channel) { ArrayDeque() }.peek() != null) {
                val newHangmanObj = q.getOrPut(channel) { ArrayDeque() }.poll()!!
                newHangmanObj.start(channel)
            }
        }

        fun handleResult(message: Message, result: EnumResult) {
            when (result) {
                ModuleHangman.Hangman.EnumResult.LOSS -> {
                    message.reply(LargeStringHolder.LOSS)
                    message.reply("Phrase: $word")
                    endAndQueue(message.channelReceiver)
                }
                ModuleHangman.Hangman.EnumResult.WIN -> {
                    message.reply(LargeStringHolder.CORRECT)
                    message.reply("Phrase: $word")
                    endAndQueue(message.channelReceiver)
                }
                ModuleHangman.Hangman.EnumResult.CONTINUE -> update(message.channelReceiver)
            }
        }

        fun forceEnd(message: Message) {
            message.reply("This hangman game was forcefully ended.")
            message.reply("Phrase: $word")
            endAndQueue(message.channelReceiver)
        }

        fun createMessage(): String {
            return "Hangman < ---- > Hangman\nGuessed Letters: $guessedLetters\nGuessed Phrases: $guessedPhrases\n$this\n``$wordWithUnderscores``"
        }

        var stage: EnumHangmanStage = EnumHangmanStage.NO_MAN
        val guessed: MutableList<Char> = mutableListOf()
        val phrases: MutableList<String> = mutableListOf()
        val lowerPhrases: MutableList<String> = mutableListOf()

        val lowerWord = word.toLowerCase()

        fun advance(): Boolean {
            if (stage.ordinal == EnumHangmanStage.values().size - 1)
                return false
            else
                stage = EnumHangmanStage.values()[stage.ordinal + 1]

            return true
        }

        val wordWithUnderscores: String
            get() = word.map {if (!it.isLetter() || it.toLowerCase() in guessed) it.toString() else "_" }.joinToString("")

        enum class EnumResult {
            CONTINUE, LOSS, WIN
        }

        fun addChar(char: Char): EnumResult {
            if (!char.isLetter() || char.toLowerCase() in guessed) return EnumResult.CONTINUE

            if (char.toLowerCase() !in guessed)
                guessed.add(char.toLowerCase())

            if (char.toLowerCase() !in lowerWord) {
                if (advance())
                    return EnumResult.CONTINUE
                else
                    return EnumResult.LOSS
            }

            if (wordWithUnderscores == word) return EnumResult.WIN

            return EnumResult.CONTINUE
        }

        fun guessPhrase(phrase: String): EnumResult {
            if (phrase.toLowerCase() in lowerPhrases) return EnumResult.CONTINUE

            if (phrase.toLowerCase() !in lowerPhrases) {
                lowerPhrases.add(phrase.toLowerCase())
                phrases.add(phrase)
            }

            if (word.toLowerCase() != phrase.toLowerCase()) {
                if (advance())
                    return EnumResult.CONTINUE
                else
                    return EnumResult.LOSS
            } else return EnumResult.WIN
        }

        val guessedLetters: String
            get() = alphabet.map { if (it in guessed) "**$it**" else "~~__${it}__~~" }.joinToString(" ")

        val guessedPhrases: String
            get() = phrases.joinToString("\n")

        override fun toString(): String {
            return "${LargeStringHolder.HANGMAN_1}${stage.man.joinToString("\n")}\n${LargeStringHolder.HANGMAN_2}"
        }
    }



    val hangman = mutableMapOf<Channel, Hangman>()
    val q = mutableMapOf<Channel, Queue<Hangman>>()

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!hangman ")) {
            message.delete()

            if (message.content.replace("!hangman ", "").isNotEmpty()) {
                val letter = message.content.replace("!hangman", "").trim()[0]
                if (letter.isLetter()) {
                    val hangmanObj = hangman[message.channelReceiver]
                    hangmanObj?.handleResult(message, hangmanObj.addChar(letter))
                }

            }
        } else if (message.content.startsWith("!hangman ")) {
            start(message.content.split(" "), message, api)
        } else if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!guess ")) {
            message.delete()
            val hangmanObj = hangman[message.channelReceiver]
            val phrase = message.content.replace("!guess", "").trim()
            if ('@' !in phrase)
                hangmanObj?.handleResult(message, hangmanObj.guessPhrase(phrase))
        } else if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!executioner")) {
            val roles = message.author.getRoles(message.channelReceiver.server)
            val manage = roles.any { it.getOverwrittenPermissions(message.channelReceiver).getState(PermissionType.ADMINISTRATOR) == PermissionState.ALLOWED } ||
                    roles.any { it.getOverwrittenPermissions(message.channelReceiver).getState(PermissionType.MANAGE_MESSAGES) == PermissionState.ALLOWED } ||
                    message.channelReceiver.getOverwrittenPermissions(message.author).getState(PermissionType.MANAGE_MESSAGES) == PermissionState.ALLOWED

            message.delete()
            if (manage) {
                val hangmanObj = hangman[message.channelReceiver]
                hangmanObj?.forceEnd(message)
            }
        }
        return super.onMessage(api, message)
    }

    fun start(args: List<String>, message: Message, api: DiscordAPI) {
        if (args.size < 3) {
            message.reply("Invalid! Use: !hangman <channelid> <word>")
            return
        }

        val id = args[1]
        val word = args.subList(2, args.size).joinToString(" ")
        if ("@" in word) {
            message.reply("@ tags are not permitted for hangman words, because of possible abuse. Please try again.")
            return
        } else if (word.none { it.isLetter() }) {
            message.reply("Your hangman doesn't have any letters to guess! Please try again.")
            return
        }
        val channelobj = if (id == "here") {
            message.channelReceiver
        } else {
            val server = id.split("@")[1]
            val channel = id.split("@")[0]
            api.getServerById(server).getChannelById(channel)
        }

        if (channelobj == null) {
            message.reply("That isn't a channel!")
            return
        }

        if (hangman[channelobj] == null) {
            val hangmanObj = Hangman(word, message.author.name)
            message.reply("$word\n\nThis hangman is now running on $channelobj.")

            hangmanObj.start(channelobj)
        } else {
            val queue = q.getOrPut(channelobj) { ArrayDeque() }
            val position = queue.size + 1
            queue.add(Hangman(word, message.author.name))
            message.reply("$word\n\nThis hangman has now been queued on $channelobj. Position on queue: $position")
        }
    }

    enum class EnumHangmanStage(vararg val man: String) {
        NO_MAN(
                "  |",
                "  |",
                "  |"
        ), HEAD(
                "  |                   O",
                "  |",
                "  |"
        ), BODY(
                "  |                   O",
                "  |                    |",
                "  |"
        ), RIGHT_ARM(
                "  |                  O",
                "  |                   |\\",
                "  |"
        ), LEFT_ARM(
                "  |                  O",
                "  |                 /|\\",
                "  |"
        ), LEFT_LEG(
                "  |                  O",
                "  |                 /|\\",
                "  |                  /"
        ), RIGHT_LEG(
                "  |                  O",
                "  |                 /|\\",
                "  |                  /\\"
        )
    }
}
