import dev.inmo.micro_utils.common.Warning
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.chat.get.getChat
import dev.inmo.tgbotapi.extensions.api.chat.members.getChatMember
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.api.send.sendMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onContact
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onText
import dev.inmo.tgbotapi.extensions.utils.extensions.raw.from
import dev.inmo.tgbotapi.extensions.utils.memberChatMemberOrNull
import dev.inmo.tgbotapi.extensions.utils.ownerChatMemberOrNull
import dev.inmo.tgbotapi.extensions.utils.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.URLInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.regular
import utils.Config.TelegramConfig.telegramChannelId
import utils.Messages

@OptIn(Warning::class)
suspend fun check(usId: UserId, cyId: IdChatIdentifier, bot: TelegramBot): Boolean {
    println(cyId)
    println(bot.getChatMember(cyId.toChatId(), usId).memberChatMemberOrNull())
    println(usId.userLink)

    return bot.getChatMember(cyId.toChatId(), usId).ownerChatMemberOrNull() != null ||
            bot.getChatMember(cyId.toChatId(), usId).memberChatMemberOrNull() != null
}

class BotBehaviour(
    private val bot: TelegramBot,
    // TODO: доделать
    ) {
    @OptIn(RiskFeature::class)
    suspend fun startBot() {
        var dataQuery = CallbackDataInlineKeyboardButton("Проверить подписку", null.toString())

        bot.buildBehaviourWithLongPolling {
            onCommand("start") {
                val replyMarkup = ReplyKeyboardMarkup(
                    keyboard = listOf(
                        listOf(
                            SimpleKeyboardButton("Принять участие"),
                            RequestContactKeyboardButton("Добавить номер телефона"),
                        ),
                    )
                )

                reply(it, replyMarkup = replyMarkup) { regular(Messages.startMessage) }
            }

            onText {
                if (check(it.chat.id.toChatId(), bot.getChat(ChatId(telegramChannelId)).id, bot)) {
                    when (it.content.text) {
                        "Принять участие" -> reply(it) { regular(Messages.startMessage) }
                    }

                } else {
                    dataQuery = CallbackDataInlineKeyboardButton("Проверить подписку", it.from!!.id.toString())
                    print(dataQuery.callbackData)
                    val inlineKeyboardButton = InlineKeyboardMarkup(
                        URLInlineKeyboardButton("Подпишись", "https://t.me/sdjghasjdha"),
                        dataQuery
                    )
                    bot.sendMessage(it.chat.id, Messages.channelMessage, replyMarkup = inlineKeyboardButton)
                }
            }

            onDataCallbackQuery {
                if (!check(it.from.id, bot.getChat(ChatId(telegramChannelId)).id, bot)) {
                    val inlineKeyboardButton = InlineKeyboardMarkup(
                        URLInlineKeyboardButton("Подпишись", "https://t.me/sdjghasjdha"),
                        dataQuery
                    )
                    bot.sendMessage(it.from.id, Messages.channelMessage, replyMarkup = inlineKeyboardButton)
                } else {
                    bot.sendMessage(it.from.id, Messages.thanksMessage)
                }
            }

            onContact { reply(it) { regular(Messages.thanksMessage) } }

            setMyCommands(getBaseCommands())
        }.join()


    }

    private fun getBaseCommands(): List<BotCommand> = listOf(
        BotCommand("start", "Начать работу бота")
    )
}