package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.LargeStringHolder
import eladkay.onepunchbot.WireDontTouchThisOrIllKillYouWhileYouSleep
import java.util.*

/**
 * Created by Elad on 4/15/2017.
 */
@WireDontTouchThisOrIllKillYouWhileYouSleep
object ModuleHangman : IModule {
    class Hangman(val word: String, val creator: String, var stage: EnumHangmanStage = EnumHangmanStage.NO_MAN, val guessed: MutableList<Char> = mutableListOf()) {
        companion object {
            private val alphabet = (0 until 26).map { (it + 0x61).toChar() }.joinToString("")
        }

        val lowerWord = word.toLowerCase()

        fun advance(): Boolean {
            if (stage == EnumHangmanStage.RIGHT_LEG)
                return false
            else stage = EnumHangmanStage.values()[stage.ordinal + 1]
            return true
        }

        val wordWithUnderscores: String
            get() = word.map {if (!it.isLetter() || it.toLowerCase() in guessed) it else '_' }.joinToString(" ")

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

        val guessedLetters: String
            get() = "Guessed: ${alphabet.map { if (it in guessed) it else '_' }.joinToString("")}\n"

        override fun toString(): String {
            return "$guessedLetters${LargeStringHolder.HANGMAN_1}${stage.man}${LargeStringHolder.HANGMAN_2}``${wordWithUnderscores}``"
        }
    }

    val hangman = mutableMapOf<Channel, Hangman>()
    val q = mutableMapOf<Channel, Queue<Hangman>>()

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.userReceiver != null && message.content.startsWith("!hangman ")) {
            val args = message.content.split(" ")
            if (args.size < 3) {
                message.reply("Invalid! use: !hangman <channelid> <word>")
            } else {
                val id = args[1]
                val word = args.subList(2, args.size).joinToString(" ")
                if ("@" in word) {
                    message.reply("@ tags are not permitted for hangman words, because of possible abuse. Please try again.")
                    return super.onMessage(api, message)
                } else if (word.none { it.isLetter() }) {
                    message.reply("Your hangman doesn't have any letters to guess! Please try again.")
                    return super.onMessage(api, message)
                }
                val server = id.split("@")[1]
                val channel = id.split("@")[0]
                val channelobj = api.getServerById(server).getChannelById(channel)
                if (hangman[channelobj] == null) {
                    hangman.put(channelobj, Hangman(word, message.author.name))
                    message.reply("$word hangman is now running on $channelobj")
                    channelobj.sendMessage("${message.author.name} has started a game of Hangman!")
                    channelobj.sendMessage(hangman[channelobj].toString())
                } else {
                    val queue = q.getOrPut(channelobj) {
                        ArrayDeque()
                    }
                    val position = queue.size + 1
                    queue.add(Hangman(word, message.author.name))
                    message.reply("$word hangman is now queued on $channelobj. Position on queue: $position")
                }
            }

        } else if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!hangman ")) {
            if (message.content.replace("!hangman ", "").length != 1) {
                message.reply("Invalid! use: !hangman <char>")
            } else {
                val letter = message.content.replace("!hangman ", "")[0]
                if (!letter.isLetter()) message.reply("That's not a letter!")
                else {
                    when (hangman[message.channelReceiver]!!.addChar(letter)) {

                        ModuleHangman.Hangman.EnumResult.LOSS -> {
                            message.reply(LargeStringHolder.LOSS)
                            message.reply("Phrase: ${hangman[message.channelReceiver]!!.word}")
                            hangman.remove(message.channelReceiver)
                            if (q.getOrPut(message.channelReceiver) { ArrayDeque() }.peek() != null) {
                                val hangmanObj = q.getOrPut(message.channelReceiver) { ArrayDeque() }.poll()!!
                                val channelobj = message.channelReceiver
                                hangman.put(channelobj, hangmanObj)
                                channelobj.sendMessage("\n${hangmanObj.creator} has started a game of Hangman!")
                                Thread.sleep(500)
                                channelobj.sendMessage(hangmanObj.toString())
                            }

                        }
                        ModuleHangman.Hangman.EnumResult.WIN -> {
                            message.reply(LargeStringHolder.CORRECT)
                            message.reply("Phrase: ${hangman[message.channelReceiver]!!.word}")
                            hangman.remove(message.channelReceiver)
                            if (q.getOrPut(message.channelReceiver) { ArrayDeque() }.peek() != null) {
                                val hangmanObj = q.getOrPut(message.channelReceiver) { ArrayDeque() }.poll()!!
                                val channelobj = message.channelReceiver
                                hangman.put(channelobj, hangmanObj)
                                channelobj.sendMessage("\n${hangmanObj.creator} has started a game of Hangman!")
                                Thread.sleep(500)
                                channelobj.sendMessage(hangmanObj.toString())
                            }
                        }
                        ModuleHangman.Hangman.EnumResult.CONTINUE -> {
                            //noop
                        }
                    }
                    message.reply(hangman[message.channelReceiver]!!.toString())
                }

            }
        } else if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!guess ")) {
            if (hangman[message.channelReceiver]!!.lowerWord == message.content.replace("!guess ", "").toLowerCase()) {
                message.reply(LargeStringHolder.CORRECT)
                message.reply("Phrase: ${hangman[message.channelReceiver]!!.word}")
                hangman.remove(message.channelReceiver)
                if (q.getOrPut(message.channelReceiver) { ArrayDeque() }.peek() != null) {
                    val hangmanObj = q.getOrPut(message.channelReceiver) { ArrayDeque() }.poll()!!
                    val channelobj = message.channelReceiver
                    hangman.put(channelobj, hangmanObj)
                    channelobj.sendMessage("\n${hangmanObj.creator} has started a game of Hangman!")
                    Thread.sleep(500)
                    channelobj.sendMessage(hangmanObj.toString())
                }
            } else {
                message.reply("Nope.")
            }

            return super.onMessage(api, message)
        }
        return super.onMessage(api, message)
    }

    enum class EnumHangmanStage(val man: String) {
        NO_MAN("  |\n  |\n  |\n"), //'s sky
        HEAD("  |                   O\n  |\n  |\n"), //, shoulders, knees and toes
        /* 'cause i'm, missing more than just your */ BODY("  |                   O\n  |                  |\n  |\n"),
        RIGHT_ARM("  |                  O\n  |                   |\\\n  |\n"), //that's when the puns start getting boring
        LEFT_ARM("  |                  O\n  |                 /|\\\n  |\n"), //yup
        LEFT_LEG("  |                  O\n  |                 /|\\\n  |                  /\n"), //mehhhhh
        RIGHT_LEG("  |                  O\n  |                 /|\\\n  |                  /\\\n"), //it's over, finally
    }
}
