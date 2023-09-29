import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.utils.regular


class BotBehaviour(
    private val bot: TelegramBot,
) {
    suspend fun startBot() {
        bot.buildBehaviourWithLongPolling {
            onCommand("help") {
                reply(it) { regular("asdfasdf") }
            }

            onCommand("asdfas"){

            }

            setMyCommands(getBaseCommands())
        }.join()
    }

    private fun getBaseCommands(): List<BotCommand> = listOf(
        BotCommand("help", "Ссылка на ноушен группы")
    )
}