package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import eladkay.onepunchbot.remove
import eladkay.onepunchbot.startsWith

/**
 * Created by Elad on 5/12/2017.
 */
object ModuleBrainfuck : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if(message.startsWith("!brainfuck ")) {
            Thread {
                buildString {
                    val code = message.remove("!brainfuck ")
                    val LENGTH = 65535
                    val mem = IntArray(LENGTH)
                    var dataPointer: Int = 0
                    var i = 0
                    var l = 0
                    while (i < code.length) {
                        if (code[i] == '>') {
                            dataPointer = if (dataPointer == LENGTH - 1) 0 else dataPointer + 1
                        } else if (code[i] == '<') {
                            dataPointer = if (dataPointer == 0) LENGTH - 1 else dataPointer - 1
                        } else if (code[i] == '+') {
                            mem[dataPointer]++
                        } else if (code[i] == '-') {
                            mem[dataPointer]--
                        } else if (code[i] == '.') {
                            append(mem[dataPointer].toChar())
                        } else if (code[i] == ',') {
                            mem[dataPointer] = code[i + 1].toInt()
                        } else if (code[i] == '[') {
                            if (mem[dataPointer] == 0) {
                                i++
                                while (l > 0 || code[i] != ']') {
                                    if (code[i] == '[') l++
                                    if (code[i] == ']') l--
                                    i++
                                }
                            }
                        } else if (code[i] == ']') {
                            if (mem[dataPointer] != 0) {
                                i--
                                while (l > 0 || code[i] != '[') {
                                    if (code[i] == ']') l++
                                    if (code[i] == '[') l--
                                    i--
                                }
                                i--
                            }
                        }
                        i++
                    }
                }.apply { message.reply(this) }
            }.start()
        }
        return super.onMessage(api, message)
    }
}