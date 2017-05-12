package eladkay.onepunchbot.modules

import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.CommandBase
import eladkay.onepunchbot.setInterval

/**
 * Created by Elad on 5/12/2017.
 */
object ModuleTetris : CommandBase() {
    override val paramCount: Int
        get() = -1
    override val command: String
        get() = "tetris"

    //\u25A1 is white square
    //\u25A0 is black square
    class Tetris(val channel: Channel, val board: Array<Array<Char>> = Array(16) { Array(16) { '\u25A1' } }) {
        val threadId: Long = setInterval(2000) {
            update()
        }

        var lastMessage: Message = channel.sendMessage(toString()).get()

        fun update() {
            //tetris logic here

            lastMessage.delete()
            lastMessage = channel.sendMessage(toString()).get()
        }

        companion object {
            const val WHITE_SQUARE = '\u25A1'
            const val BLACK_SQUARE = '\u25A1'
        }

        fun set(x: Int, y: Int, char: Char) {
            board[x][y] = char
        }

        override fun toString(): String {
            return "----Tetris----\n${board.map { it.joinToString("") }.joinToString("")}"
        }
    }
    val tetrisGames = mutableMapOf<Channel, Tetris>()
    override fun processCommand(message: Message, args: Array<String>) {
        if(message.channelReceiver in tetrisGames.keys) {
            message.reply("Tetris game already running!")
            return
        }
        tetrisGames.put(message.channelReceiver, Tetris(message.channelReceiver))
        message.reply("Tetris game starting!")

    }
}