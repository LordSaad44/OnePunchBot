package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.LargeStringHolder

/**
 * Created by Elad on 4/15/2017.
 */
object ModuleHangman : IModule {
    class Hangman(val word: String, var stage: EnumHangmanStage = EnumHangmanStage.NO_MAN, val guessed: MutableList<Char> = mutableListOf(' ')) {
        fun advance(): Boolean {
            if(stage == EnumHangmanStage.RIGHT_LEG)
                return false
            else stage = EnumHangmanStage.values()[stage.ordinal + 1]
            return true
        }
        fun getCharsFromWord() = mutableSetOf<Char>().apply { word.toCharArray().forEach { add(it) } }
        fun getWordWithUnderscores(): String {
            val builder = StringBuilder()
            for (char in word)  if(char in guessed) builder.append(char) else builder.append("_")
            return builder.toString()
        }
        enum class EnumResult {
            CONTINUE, LOSS, WIN
        }
        fun addChar(char: Char): EnumResult {
            if(char !in word) {
                if (advance())
                    return EnumResult.CONTINUE
                else
                    return EnumResult.LOSS
            }

            guessed.add(char)
            if(getWordWithUnderscores() == word) return EnumResult.WIN
            return EnumResult.CONTINUE
        }
        override fun toString(): String {
            return "${LargeStringHolder.HANGMAN_1}${stage.man}${LargeStringHolder.HANGMAN_2}``${getWordWithUnderscores()}``"
        }
    }
    val hangman = mutableMapOf<Channel, Hangman>()
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.userReceiver != null && message.content.startsWith("!hangman ")) {
            val args = message.content.split(" ")
            if (args.size < 3) {
                message.reply("Invalid! use: !hangman <channelid> <word>")
            } else {
                val id = args[1]
                val word = args.subList(2, args.size).joinToString(" ")
                val server = id.split("@")[1]
                val channel = id.split("@")[0]
                val channelobj = api.getServerById(server).getChannelById(channel)
                hangman.put(channelobj, Hangman(word.toLowerCase()))
                message.reply("$word hangman is now running on $channelobj")
                channelobj.sendMessage("${message.author.name} has started a game of Hangman!")
                channelobj.sendMessage(hangman[channelobj].toString())
            }

        } else if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!hangman ")) {
            if (message.content.replace("!hangman ", "").length != 1) {
                message.reply("Invalid! use: !hangman <char>")
            } else {
                val letter = message.content.replace("!hangman ", "")[0]
                when (hangman[message.channelReceiver]!!.addChar(letter)) {

                    ModuleHangman.Hangman.EnumResult.LOSS -> {
                        message.reply(LargeStringHolder.LOSS)
                        message.reply("Phrase: ${hangman[message.channelReceiver]!!.word}")
                        hangman.remove(message.channelReceiver)
                    }
                    ModuleHangman.Hangman.EnumResult.WIN -> {
                        message.reply(LargeStringHolder.CORRECT)
                        message.reply("Phrase: ${hangman[message.channelReceiver]!!.word}")
                        hangman.remove(message.channelReceiver)
                    }
                    ModuleHangman.Hangman.EnumResult.CONTINUE -> {
                        //noop
                    }
                }
                message.reply(hangman[message.channelReceiver]!!.toString())
            }
        } else if (message.channelReceiver != null && message.channelReceiver in hangman && message.content.startsWith("!guess ")) {
            if (hangman[message.channelReceiver]!!.word == message.content.replace("!guess ", "").toLowerCase()) {
                message.reply(LargeStringHolder.CORRECT)
                message.reply("Phrase: ${hangman[message.channelReceiver]!!.word}")
                hangman.remove(message.channelReceiver)
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
        LEFT_ARM("  |                  O\n  |                   /|\\\n  |\n"), //yup
        LEFT_LEG("  |                  O\n  |                   /|\\\n  |                  /\n"), //mehhhhh
        RIGHT_LEG("  |                  O\n  |                   /|\\\n  |                  /\\\n"), //it's over, finally
    }
}
