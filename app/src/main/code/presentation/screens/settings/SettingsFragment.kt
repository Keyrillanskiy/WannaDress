package presentation.screens.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.keyrillanskiy.cloather.R
import extensions.reObserve
import extensions.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import presentation.common.ActivityInteractor
import presentation.common.Failure
import presentation.common.Loading
import presentation.common.Success
import presentation.screens.main.MainViewModel

/**
 * Экран настроек.
 *
 * @author Keyrillanskiy
 * @since 22.02.2019, 10:12.
 */
class SettingsFragment : Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var viewHolder: SettingsViewHolder
    private var parentInteractor: SettingsInteractor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.apply {
            userLiveData.reObserve(this@SettingsFragment, Observer { response ->
                when (response) {
                    is Loading -> parentInteractor?.onShowLoading()
                    is Success -> {
                        viewHolder.run {
                            parentInteractor?.onHideLoading()
                            user = response.value
                        }
                    }
                    is Failure -> {
                        val title = getString(R.string.error)
                        val message = getString(R.string.unknown_error)
                        parentInteractor?.onHideLoading()
                        parentInteractor?.onShowErrorDialog(title, message)
                    }
                }
            })

            uploadSettingsLiveData.observe(this@SettingsFragment, Observer { response ->
                when(response) {
                    is Loading -> parentInteractor?.onShowLoading()
                    is Failure -> {
                        val title = getString(R.string.error)
                        val message = getString(R.string.unknown_error)
                        parentInteractor?.onHideLoading()
                        parentInteractor?.onShowErrorDialog(title, message)
                        viewModel.fetchUserFromCache()
                    }
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewHolder = SettingsViewHolder(view).setup {
            onLogOutClick = { parentInteractor?.onLogOut() }
            onGenderChanged = { gender ->
                viewModel.saveGender(gender)
                parentInteractor?.onSettingsChanged()
            }
            onNotificationsClick = { context?.toast(R.string.feature_in_development) }
        }

        viewModel.fetchUser()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentInteractor = context as? SettingsInteractor ?: throw IllegalArgumentException(
            "Invalid parent attached: $context"
        )
    }

    override fun onDetach() {
        parentInteractor = null
        super.onDetach()
    }

}

interface SettingsInteractor : ActivityInteractor {
    fun onLogOut()
    fun onSettingsChanged()
}
