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
import dev.inmo.tgbotapi.extensions.utils.types.buttons.*
import dev.inmo.tgbotapi.types.*
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.InlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.URLInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.ReplyKeyboardMarkup
import dev.inmo.tgbotapi.types.buttons.RequestContactKeyboardButton
import dev.inmo.tgbotapi.types.buttons.SimpleKeyboardButton
import dev.inmo.tgbotapi.types.buttons.reply.requestChannelReplyButton
import dev.inmo.tgbotapi.types.message.textsources.regular
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.types.request.RequestId
import dev.inmo.tgbotapi.utils.RiskFeature
import dev.inmo.tgbotapi.utils.regular
import dev.inmo.tgbotapi.utils.row
import dev.inmo.tgbotapi.utils.toJson
import utils.Messages
suspend fun check(usId: UserId, cyId: IdChatIdentifier, bot:TelegramBot): Boolean {
    if(bot.getChatMember(cyId.toChatId(),usId).user.id != usId){
        return false;
    }
    return true;
}

class BotBehaviour(
    private val bot: TelegramBot,
    //доделать
) {
    private var dataQuery = CallbackDataInlineKeyboardButton("Проверить подписку", null.toString())
    @OptIn(RiskFeature::class)
    suspend fun startBot() {

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

                    reply(it, replyMarkup = replyMarkup) { regular(Messages.startMessage)

                    }

            }

            onText {
                if(check(it.from!!.id,bot.getChat(ChatId( -1001821814522)).id,bot)){
                    when (it.content.text) {
                        "Принять участие" -> reply(it) { regular(Messages.startMessage) }

                    }
                } else {
                    dataQuery = CallbackDataInlineKeyboardButton("Проверить подписку", it.from!!.id.toString())
                    print(dataQuery.callbackData)
                        var inlineKeyboardButton = InlineKeyboardMarkup(URLInlineKeyboardButton("Подпишись","https://t.me/+Ima3S3ui4Y9kMTAy"),
                            dataQuery)
                        bot.sendMessage(it.chat.id,Messages.channelMessage,parseMode = null,
                            disableWebPagePreview = false,threadId = null,
                            disableNotification = false,protectContent = false,
                            replyToMessageId = null,allowSendingWithoutReply = false,inlineKeyboardButton)
                }
                }
            onDataCallbackQuery{
                if(!check(it.from.id,bot.getChat(ChatId( -1001821814522)).id,bot)){
                    var inlineKeyboardButton = InlineKeyboardMarkup(URLInlineKeyboardButton("Подпишись","https://t.me/+Ima3S3ui4Y9kMTAy"),
                        dataQuery)
                    bot.sendMessage(it.from.id,Messages.channelMessage,parseMode = null,
                        disableWebPagePreview = false,threadId = null,
                        disableNotification = false,protectContent = false,
                        replyToMessageId = null,allowSendingWithoutReply = false,inlineKeyboardButton)
                }
                else {
                    bot.sendMessage(it.from.id,Messages.thanksMessage)
                }
}
            onContact {
                if (it.from?.id   == it.content.contact.userId) {
                    reply(it) { regular(Messages.thanksMessage) }
                } else {
                    reply(it) { regular(Messages.wrongNumberMessage) }
                }
            }

            setMyCommands(getBaseCommands())
        }.join()


    }

    private fun getBaseCommands(): List<BotCommand> = listOf(
        BotCommand("start", "Начать работу бота")
    )
}