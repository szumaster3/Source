package core.auth

import core.ServerConstants
import core.game.node.entity.player.Player
import core.game.system.SystemManager
import core.storage.AccountStorageProvider
import core.storage.SQLStorageProvider
import java.sql.SQLDataException
import java.sql.Timestamp

class ProductionAuthenticator : AuthProvider<AccountStorageProvider>() {
    override fun configureFor(provider: AccountStorageProvider) {
        storageProvider = provider
        if (provider is SQLStorageProvider) {
            provider.configure(
                ServerConstants.DATABASE_ADDRESS!!,
                ServerConstants.DATABASE_NAME!!,
                ServerConstants.DATABASE_USER!!,
                ServerConstants.DATABASE_PASS!!,
            )
        }
    }

    override fun createAccountWith(info: UserAccountInfo): Boolean {
        try {
            info.password = SystemManager.encryption.hashPassword(info.password)
            info.joinDate = Timestamp(System.currentTimeMillis())
            storageProvider.store(info)
        } catch (e: SQLDataException) {
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    override fun checkLogin(
        username: String,
        password: String,
    ): Pair<AuthResponse, UserAccountInfo?> {
        val info: UserAccountInfo
        try {
            if (!storageProvider.checkUsernameTaken(username.lowercase())) {
                return Pair(AuthResponse.InvalidCredentials, null)
            }
            info = storageProvider.getAccountInfo(username.lowercase())
            val passCorrect = SystemManager.encryption.checkPassword(password, info.password)
            if (!passCorrect || info.password.isEmpty()) return Pair(AuthResponse.InvalidCredentials, null)
            if (info.banEndTime > System.currentTimeMillis()) return Pair(AuthResponse.AccountDisabled, null)
            if (info.online) return Pair(AuthResponse.AlreadyOnline, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(AuthResponse.CouldNotLogin, null)
        }
        return Pair(AuthResponse.Success, info)
    }

    override fun checkPassword(
        player: Player,
        password: String,
    ): Boolean {
        return SystemManager.encryption.checkPassword(password, player.details.password)
    }

    override fun updatePassword(
        username: String,
        newPassword: String,
    ) {
        val info = storageProvider.getAccountInfo(username)
        info.password = SystemManager.encryption.hashPassword(newPassword)
        storageProvider.update(info)
    }
}
