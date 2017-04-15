package eladkay.onepunchbot.modules

import com.google.common.base.Joiner
import com.google.common.base.Throwables
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.MessageBuilder
import de.btobastian.javacord.entities.message.MessageDecoration
import eladkay.onepunchbot.IModule
import javassist.CannotCompileException
import javassist.ClassClassPath
import javassist.ClassPool
import javassist.CtNewMethod
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


/**
 * Created by Elad on 3/24/2017.
 */
object ModuleJava : IModule {
    override fun onMessage(api: DiscordAPI, message: Message): Boolean {
        if (message.content.startsWith("!java ") && message.author.getRoles(message.channelReceiver.server).any { it.name == "Admins" }) {
            val command = message.content.replace("!java ", "")
            val executionThread = arrayOfNulls<Thread>(1)
            val future = api.threadPool.executorService.submit<String> {
                executionThread[0] = Thread.currentThread()
                try {
                    return@submit firstTry(api, command, command.split(" ").toTypedArray(), message)
                } catch (e: Throwable) {
                    return@submit sendStack(e, message)
                }
            }
            var reply: Message? = null
//            try {
//                reply = message.reply("```Executing...```").get()
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//                message.reply(sendStack(e, message))
//            } catch (e: ExecutionException) {
//                e.printStackTrace()
//                message.reply(sendStack(e, message))
//            }

            for (i in 0..59) {
                try {
                    val response = future.get(1, TimeUnit.SECONDS)
                    reply?.delete()
                } catch (e: InterruptedException) {
                } catch (e: ExecutionException) {
                } catch (e: TimeoutException) {
                    if (executionThread[0] != null && i < 59) {
                        val builder = MessageBuilder()
                        builder.append(MessageDecoration.CODE_LONG.prefix)
                                .appendNewLine()
                                .append("Execution details:")
                                .appendNewLine()
                                .append("Time running: ~" + (i + 1) + "s")
                                .appendNewLine()
                                .appendNewLine()
                                .append("Current stack trace:")
                                .appendNewLine()
                        executionThread[0]!!.stackTrace
                                .takeWhile { !(it.methodName == "executeCode" && it.className.startsWith("ExecuteCode")) }
                                .forEach {
                                    builder.append("| ")
                                            .append(it.className)
                                            .append("#")
                                            .append(it.methodName)
                                            .append("(line: ").append(it.lineNumber.toString()).append(")")
                                            .appendNewLine()
                                }
                        builder.append(MessageDecoration.CODE_LONG.suffix)
                        reply?.edit(builder.toString())
                    }
                }

            }
            val builder = MessageBuilder()
                    .append(MessageDecoration.CODE_LONG.prefix)
                    .appendNewLine()
                    .append("Execution canceled (took more than 60 seconds)")
            if (executionThread[0] != null) {
                builder.appendNewLine()
                        .appendNewLine()
                        .append("Execution details:")
                        .appendNewLine()
                        .append("Time ran: 60s")
                        .appendNewLine()
                        .appendNewLine()
                        .append("Complete Stack Trace when cancelled:")
                        .appendNewLine()
                for (element in executionThread[0]!!.stackTrace) {
                    builder.append("| ")
                            .append(element.className)
                            .append("#")
                            .append(element.methodName)
                            .append("(line: ").append(element.lineNumber.toString()).append(")")
                            .appendNewLine()
                }
                builder.append(MessageDecoration.CODE_LONG.suffix)
            }
            future.cancel(true)
            reply?.delete()
            //message.reply(builder.toString())
        }
        return super.onMessage(api, message)

    }

    private fun firstTry(api: DiscordAPI, cmd: String, args: Array<String>, message: Message): String? {
        var code = Joiner.on(" ").join(args)
        // wrap the returned value cause javassist has some problems with primitive types
        code = code.replace("return (.+);".toRegex(), "return String.valueOf($1);")
        val pool = ClassPool.getDefault()
        pool.insertClassPath(ClassClassPath(api.javaClass))
        pool.importPackage("de.btobastian.javacord.entities")
        pool.importPackage("de.btobastian.javacord.entities.message")
        pool.importPackage("de.btobastian.javacord.entities.permissions")
        pool.importPackage("de.btobastian.javacord.exceptions")
        pool.importPackage("de.btobastian.javacord.utils")
        pool.importPackage("de.btobastian.javacord")
        val executeClass = pool.makeClass("ExecuteCode" + System.currentTimeMillis())
        try {
            executeClass.addMethod(
                    CtNewMethod.make(
                            "public java.lang.Object executeCode (DiscordAPI api, Message msg, Channel c, Server s, User u) { $code }",
                            executeClass))
            val clazz = executeClass.toClass(api.javaClass.classLoader, api.javaClass.protectionDomain)
            val obj = clazz.newInstance()
            val meth = clazz.getDeclaredMethod("executeCode", DiscordAPI::class.java, Message::class.java, Channel::class.java, Server::class.java, User::class.java)
            val server = if (message.channelReceiver != null) message.channelReceiver.server else null
            return "```\n" + meth.invoke(
                    obj, api, message, message.channelReceiver, server, message.author).toString() + "```"
        } catch (e: Throwable) {
            if (e is CannotCompileException) {
                return secondTry(api, cmd, args, message)
            }
            return sendStack(e, message)
        }
    }

    private fun secondTry(api: DiscordAPI, cmd: String, args: Array<String>, message: Message): String? {
        val code = Joiner.on(" ").join(args)
        val pool = ClassPool.getDefault()
        pool.insertClassPath(ClassClassPath(api.javaClass))
        val executeClass = pool.makeClass("ExecuteCodeVoid" + System.currentTimeMillis())
        try {
            executeClass.addMethod(
                    CtNewMethod.make(
                            "public void executeCode (DiscordAPI api, Message msg, Channel c, Server s, User u) { $code }",
                            executeClass))
            val clazz = executeClass.toClass(api.javaClass.classLoader, api.javaClass.protectionDomain)
            val obj = clazz.newInstance()
            val meth = clazz.getDeclaredMethod("executeCode", DiscordAPI::class.java, Message::class.java, Channel::class.java, Server::class.java, User::class.java)
            val server = if (message.channelReceiver != null) message.channelReceiver.server else null
            meth(obj, api, message, message.channelReceiver, server, message.author)
            return "```\n" + "Executed!" + "\n```"
        } catch (e: Throwable) {
            return sendStack(e, message)
        }

    }


    private fun sendStack(e: Throwable, message: Message): String? {
        val stack = Throwables.getStackTraceAsString(e)
        if (stack.length < 1989) {
            return "```\n$stack\n```"
        }
        val split = stack.split("Caused by:".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var i = 0
        for (piece in split) {
            try {
                message.reply("```\n" + (if (i++ > 0) "Caused by:" else "") + piece + "\n```").get()
            } catch (ex: ExecutionException) {
                ex.printStackTrace()
            } catch (ex: InterruptedException) {
                ex.printStackTrace()
            }

        }
        return null
    }
}