/*
 * Copyright 2023 The Duckee Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.duckee.android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import timber.log.Timber
import xyz.duckee.android.core.designsystem.theme.DuckeeTheme
import xyz.duckee.android.core.domain.auth.CheckAuthenticateStateUseCase
import xyz.duckee.android.core.ui.LocalNavigationPopStack
import xyz.duckee.android.core.ui.LocalPaymentSheet
import xyz.duckee.android.core.ui.PurchaseEventManager
import javax.inject.Inject
import xyz.duckee.android.core.navigation.navigateToCollectionTab
import xyz.duckee.android.core.navigation.navigateToDetailScreen
import xyz.duckee.android.core.navigation.navigateToExploreTab
import xyz.duckee.android.core.navigation.navigateToRecipeScreen
import xyz.duckee.android.core.navigation.navigateToSignInScreen
import co.ab180.airbridge.Airbridge

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var paymentSheet: PaymentSheet

    @Inject
    lateinit var checkAuthenticateStateUseCase: CheckAuthenticateStateUseCase

    @Inject
    lateinit var purchaseEventManager: PurchaseEventManager

    private var navigationController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        paymentSheet = PaymentSheet(this) {
            when (it) {
                is PaymentSheetResult.Canceled -> {
                    purchaseEventManager.purchaseComplete()
                    Timber.e("Canceled")
                }

                is PaymentSheetResult.Failed -> {
                    purchaseEventManager.purchaseComplete()
                    Timber.e("Error: ${it.error}")
                }

                is PaymentSheetResult.Completed -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(2000)
                        purchaseEventManager.purchaseComplete()
                    }

                    purchaseEventManager.purchaseComplete()
                    Timber.e("Completed")
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        setContent {
            val navController = rememberMaterialMotionNavController()

            LaunchedEffect(navController) {
                navigationController = navController
            }

            DuckeeTheme {
                CompositionLocalProvider(
                    LocalNavigationPopStack provides { navController.popBackStack() },
                    LocalPaymentSheet provides paymentSheet,
                ) {
                    DuckeeApp(
                        isAuthenticated = { checkAuthenticateStateUseCase() },
                        navController = navController,
                    )
                }
            }
        }

        // Airbridge 딥링크 처리
        Airbridge.handleDeeplink(intent) { deeplink ->
            handleDeeplink(deeplink)
        }

        // 지연된 딥링크 처리
        Airbridge.handleDeferredDeeplink { deeplink ->
            deeplink?.let { handleDeeplink(it) }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        // Airbridge 딥링크 처리
        intent?.let { newIntent ->
            Airbridge.handleDeeplink(newIntent) { deeplink ->
                handleDeeplink(deeplink)
            }
        }
    }

    private fun handleDeeplink(deeplink: Uri) {
        val path = deeplink.path ?: return
        when {
            path.startsWith("/recipe/") -> {
                val recipePattern = "/recipe/(\\d+)".toRegex()
                val matchResult = recipePattern.find(path)
                matchResult?.let { result ->
                    val recipeId = result.groupValues[1]
                    navigationController?.navigateToRecipeScreen(recipeId)
                }
            }
            path.startsWith("/detail/") -> {
                val detailPattern = "/detail/([\\w-]+)".toRegex()
                val matchResult = detailPattern.find(path)
                matchResult?.let { result ->
                    val detailId = result.groupValues[1]
                    navigationController?.navigateToDetailScreen(detailId)
                }
            }
            path == "/explore" -> {
                navigationController?.navigateToExploreTab()
            }
            path == "/collection" -> {
                navigationController?.navigateToCollectionTab()
            }
            path == "/signin" -> {
                navigationController?.navigateToSignInScreen()
            }
            else -> {
                Timber.tag("[DuckeeMainActivity]").w("Unhandled deep link path: $path")
            }
        }
    }
}
