package xyz.duckee.android

import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import co.ab180.airbridge.Airbridge
import timber.Timber
import xyz.duckee.android.core.navigation.navigateToCollectionTab
import xyz.duckee.android.core.navigation.navigateToDetailScreen
import xyz.duckee.android.core.navigation.navigateToExploreTab
import xyz.duckee.android.core.navigation.navigateToRecipeScreen
import xyz.duckee.android.core.navigation.navigateToSignInScreen

class DeeplinkHandler(private val navController: NavController?) {

    fun handleDeeplink(intent: Intent) {
        Airbridge.handleDeeplink(intent) { uri ->
            handleDeeplinkUri(uri)
        }
    }

    fun handleDeferredDeeplink() {
        Airbridge.handleDeferredDeeplink { uri ->
            uri?.let { handleDeeplinkUri(it) }
        }
    }

    private fun handleDeeplinkUri(uri: Uri) {
        val path = uri.path ?: return

        when {
            path.startsWith("/recipe/") -> {
                val recipePattern = "/recipe/(\\d+)".toRegex()
                val matchResult = recipePattern.find(path)
                matchResult?.let { result ->
                    val recipeId = result.groupValues[1]
                    navController?.navigateToRecipeScreen(recipeId)
                }
            }

            path.startsWith("/detail/") -> {
                val detailPattern = "/detail/([\\w-]+)".toRegex()
                val matchResult = detailPattern.find(path)
                matchResult?.let { result ->
                    val detailId = result.groupValues[1]
                    navController?.navigateToDetailScreen(detailId)
                }
            }

            path == "/explore" -> {
                navController?.navigateToExploreTab()
            }

            path == "/collection" -> {
                navController?.navigateToCollectionTab()
            }

            path == "/signin" -> {
                navController?.navigateToSignInScreen()
            }

            else -> {
                Timber.tag("[DeeplinkHandler]").w("Unhandled deep link path: $path")
            }
        }
    }
}