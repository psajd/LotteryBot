import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject
import utils.configureDatabase

suspend fun main() {
    startKoin {
        modules(appModule)
    }
    val botBehaviour: BotBehaviour by inject(BotBehaviour::class.java)
    configureDatabase()
    botBehaviour.startBot()
}