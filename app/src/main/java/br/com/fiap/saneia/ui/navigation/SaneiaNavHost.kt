package br.com.fiap.saneia.ui.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.fiap.saneia.ui.screens.CadastroScreen
import br.com.fiap.saneia.ui.screens.DetalheScreen
import br.com.fiap.saneia.ui.screens.InicioScreen
import br.com.fiap.saneia.ui.screens.ListaScreen
import br.com.fiap.saneia.viewmodel.CadastroViewModel
import br.com.fiap.saneia.viewmodel.DetalheEstado
import br.com.fiap.saneia.viewmodel.RelatosViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

/** Rotas das telas do app. */
object Rotas {
    const val INICIO = "inicio"
    const val RELATOS = "relatos"
    const val CADASTRO = "cadastro"
    const val DETALHE = "detalhe/{id}"
    fun detalhe(id: Long) = "detalhe/$id"
}

/**
 * Raiz do app: cria o ViewModel compartilhado, o Snackbar global
 * e o grafo de navegação (Navigation Compose).
 */
@Composable
fun SaneiaApp() {
    val navController = rememberNavController()
    val relatosViewModel: RelatosViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostra no Snackbar toda mensagem de ação realizada (salvar, confirmar, excluir...)
    LaunchedEffect(Unit) {
        relatosViewModel.mensagem.filterNotNull().collect { texto ->
            relatosViewModel.mensagemExibida()
            snackbarHostState.currentSnackbarData?.dismiss()
            launch { snackbarHostState.showSnackbar(texto) }
        }
    }

    NavHost(navController = navController, startDestination = Rotas.INICIO) {

        composable(Rotas.INICIO) {
            val resumo by relatosViewModel.resumo.collectAsStateWithLifecycle()
            InicioScreen(
                resumo = resumo,
                snackbarHostState = snackbarHostState,
                onNovoRelato = { navController.navigate(Rotas.CADASTRO) },
                onVerRelatos = { navController.navigate(Rotas.RELATOS) }
            )
        }

        composable(Rotas.RELATOS) {
            val relatos by relatosViewModel.relatosFiltrados.collectAsStateWithLifecycle()
            val filtro by relatosViewModel.filtro.collectAsStateWithLifecycle()
            ListaScreen(
                relatos = relatos,
                filtro = filtro,
                snackbarHostState = snackbarHostState,
                onFiltroSelecionado = relatosViewModel::selecionarFiltro,
                onRelatoClick = { id -> navController.navigate(Rotas.detalhe(id)) },
                onNovoRelato = { navController.navigate(Rotas.CADASTRO) },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(Rotas.CADASTRO) {
            // ViewModel do formulário, ligado ao ciclo de vida desta tela
            val cadastroViewModel: CadastroViewModel = viewModel()
            val estado by cadastroViewModel.uiState.collectAsStateWithLifecycle()
            CadastroScreen(
                estado = estado,
                snackbarHostState = snackbarHostState,
                onCategoria = cadastroViewModel::alterarCategoria,
                onGravidade = cadastroViewModel::alterarGravidade,
                onEndereco = cadastroViewModel::alterarEndereco,
                onBairro = cadastroViewModel::alterarBairro,
                onDescricao = cadastroViewModel::alterarDescricao,
                onEnviar = {
                    cadastroViewModel.salvar(onSucesso = {
                        relatosViewModel.mostrarMensagem("Relato enviado com sucesso!")
                        // Vai para a lista, mantendo Início como tela anterior
                        navController.navigate(Rotas.RELATOS) {
                            popUpTo(Rotas.INICIO)
                            launchSingleTop = true
                        }
                    })
                },
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(
            route = Rotas.DETALHE,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            val estado by remember(id) { relatosViewModel.observarRelato(id) }
                .collectAsStateWithLifecycle(initialValue = DetalheEstado.Carregando)
            DetalheScreen(
                estado = estado,
                snackbarHostState = snackbarHostState,
                onConfirmar = { relatosViewModel.confirmar(it) },
                onAvancarStatus = { relatosViewModel.avancarStatus(it) },
                onExcluir = {
                    relatosViewModel.excluir(it)
                    navController.popBackStack()
                },
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
