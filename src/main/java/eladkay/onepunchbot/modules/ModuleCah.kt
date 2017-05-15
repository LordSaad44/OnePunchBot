package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.startsWith
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*


/**
 * Created by Elad on 5/15/2017.
 */
object ModuleCah : IModule {
    val whiteCards = mutableListOf<String>()
    val blackCards = mutableListOf<String>()
    class BlackCard(val card: String) {
        val formattableCard: String get() = card.replace("__________", "%s")
        val numberOfSpots: Int get() {
            var lastIndex = formattableCard.indexOf("%s", 0)
            var count = 0

            while (lastIndex != -1) {
                count++
                lastIndex += "%s".length - 1
                lastIndex = formattableCard.indexOf("%s", lastIndex)
            }
            return count

        }

        fun complete(vararg string: String): String {
            if(string.size != numberOfSpots) return "Invalid"
            return String.format(formattableCard, string)
        }

    }
    override fun onInit(api: DiscordAPI) {
        Thread {
            println("WCards 1")
            val wcards = getText("https://www.cardsagainsthumanity.com/wcards.txt")
            whiteCards.addAll(wcards.removePrefix("cards=").split("<>").filter { it.isNotBlank() && it.isNotEmpty() }.filterNotNull())
            println("WCards 2 $wcards")
        }.start()
        Thread {
            println("BCards 1")
            val bcards1 = getText("https://www.cardsagainsthumanity.com/bcards.txt")
            val bcards2 = getText("https://www.cardsagainsthumanity.com/bcards1.txt")
            val bcards3 = getText("https://www.cardsagainsthumanity.com/bcards2.txt")
            blackCards.addAll(bcards1.removePrefix("cards=").split("<>").filter { it.isNotBlank() && it.isNotEmpty() }.filterNotNull())
            blackCards.addAll(bcards2.removePrefix("cards=").split("<>").filter { it.isNotBlank() && it.isNotEmpty() }.filterNotNull())
            blackCards.addAll(bcards3.removePrefix("cards=").split("<>").filter { it.isNotBlank() && it.isNotEmpty() }.filterNotNull())
            println("BCards 2 $blackCards")
        }.start()
        super.onInit(api)
    }

    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.startsWith("!randomcah")) {
            val random = Random()
            message.reply("```${whiteCards[random.nextInt(whiteCards.size)]}```")
            message.reply("```${blackCards[random.nextInt(blackCards.size)]}```")
        }
        return super.onMessage(api, message)
    }

    @Throws(IOException::class)
    fun getText(url: String): String {
        val `in` = URL(url).openStream()
        `in`.use { `in` ->
            return buildString {
                val inR = InputStreamReader(`in`)
                val buf = BufferedReader(inR)
                var line: String? = null
                line = buf.readLine()

                while (line != null) {
                    append(line)
                    line = buf.readLine()
                }
            }
        }

    }
}