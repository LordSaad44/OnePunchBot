package eladkay.onepunchbot.modules

import com.udojava.evalex.Expression
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.message.Message
import eladkay.onepunchbot.IModule
import java.math.BigDecimal

/**
 * Created by Elad on 2/6/2017.
 */
object ModuleMath : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if("hey bot, math " in message.content.toLowerCase()) {
            val expression = message.content.replace("hey bot, math" , "", ignoreCase = true)
            val exp = ExpressionBot(expression)
            message.reply(exp.eval().toPlainString())
        }
        return super.onMessage(api, message)
    }
    class ExpressionBot(expression: String) : Expression(expression) {
        init {
            addOperator(OperatorBot("+", 10000000, false) {
                p0, p1 ->
                if((p0.intValueExact() == 9 && p1.intValueExact() == 10) || (p1.intValueExact() == 9 && p0.intValueExact() == 10)) BigDecimal(21)
                p0 + p1
            })
        }
        inner class OperatorBot(name: String, precedence: Int, leftAssoc: Boolean, val impl: (v1: BigDecimal, v2: BigDecimal) -> BigDecimal) : Operator(name, precedence, leftAssoc) {
            override fun eval(v1: BigDecimal, v2: BigDecimal) = impl(v1, v2)
        }
    }
}