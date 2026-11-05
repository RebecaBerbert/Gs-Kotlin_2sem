package br.com.fiap.saneia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.fiap.saneia.ui.navigation.SaneiaApp
import br.com.fiap.saneia.ui.theme.SaneiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaneiaTheme {
                SaneiaApp()
            }
        }
    }
}
