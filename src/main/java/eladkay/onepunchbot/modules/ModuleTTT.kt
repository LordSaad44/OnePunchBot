package eladkay.onepunchbot.modules

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule

/**
 * Created by Elad on 4/17/2017.
 */
object ModuleTTT : IModule {
    class TTTGame(val board: Array<Array<Char>>, val player1: User, val player2: User, var turn: Int = 0) {
        fun getUserFromTurn() = if (turn == 0) player1 else player2
        fun turn() {
            turn = if (turn == 0) 1 else 0
        }

        private fun g(int1: Int, int2: Int) = board[int1][int2]
        fun checkForWin(): User? {
            board.forEach {
                if (it.joinToString("") == "XXX") return player1
                else if (it.joinToString("") == "OOO") return player2
            }
            val array0 = arrayOf(board[0][0], board[0][1], board[0][2])
            val array1 = arrayOf(board[1][0], board[1][1], board[1][2])
            val array2 = arrayOf(board[2][0], board[2][1], board[2][2])
            val metarray = arrayOf(array0, array1, array2)
            metarray.apply {
                if (joinToString("") == "XXX") return player1
                else if (joinToString("") == "OOO") return player2
            }
            val array0d = arrayOf(board[0][0], board[1][1], board[2][2])
            val array1d = arrayOf(board[0][2], board[1][1], board[2][0])
            val metarrayd = arrayOf(array0d, array1d)
            metarrayd.apply {
                if (joinToString("") == "XXX") return player1
                else if (joinToString("") == "OOO") return player2
            }
            return null
        }

        override fun toString(): String {
            return "```_______\n|${g(0, 0)}|${g(1, 0)}|${g(2, 0)}|\n_______\n|${g(0, 1)}|${g(1, 1)}|${g(2, 1)}|\n_______\n|${g(0, 2)}|${g(1, 2)}|${g(2, 2)}|\n_______```"
        }
    }

    val games = mutableMapOf<Channel, MutableList<TTTGame>>()
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.startsWith("!tttchallenge ", true) && message.channelReceiver != null) {
            val player2 = message.mentions[0]//message.channelReceiver.server.members.firstOrNull { message.content.replace("!tttchallenge ", "") in it.name }
            if (player2 == null) {
                message.reply("Invalid player2! Tag player2")
                return super.onMessage(api, message)
            }
            val game = TTTGame(Array(3) { arrayOf(' ', ' ', ' ') }, message.author, player2)
            games.getOrPut(message.channelReceiver) {
                mutableListOf()
            }.add(game)
            message.reply(game.toString())
        } else if (message.channelReceiver != null && message.channelReceiver in games && message.content.startsWith("!ttt ")) {
            val games = games[message.channelReceiver]!!
            val players = games.flatMap { listOf(it.player1, it.player2) }
            if (message.author !in players) return super.onMessage(api, message)
            val array = message.content.replace("!ttt ", "").replace("[", "").replace("]", "").split(",").map(String::toInt)
            val game = games.first { (it.player1 == message.author || it.player2 == message.author) && it.getUserFromTurn() == message.author }
            game.board[array[0]][array[1]] = if (game.player2 == message.author) 'O' else 'X'
            message.reply(game.toString())
            game.turn()
            val winner = game.checkForWin()
            if (winner != null) {
                message.reply("${winner.name} won!")
                this.games[message.channelReceiver]!!.remove(game)
            } else if(game.board.none { it.any { it == ' ' } }) {
                message.reply("Tie!")
                this.games[message.channelReceiver]!!.remove(game)
            }
        } else if (message.channelReceiver != null && message.channelReceiver in games && message.content == ("!clearttt")) {
            message.reply("TTT games cleared")
            this.games[message.channelReceiver]!!.clear()
        }
        return super.onMessage(api, message)
    }
}
