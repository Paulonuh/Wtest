package com.paulo.wtest.ui.home

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.paulo.wtest.extensions.showToast
import com.paulo.wtest.R
import com.paulo.wtest.base.BaseFragment
import com.paulo.wtest.databinding.FragmentHomeBinding
import com.paulo.wtest.extensions.isAtLeastQ
import com.paulo.wtest.extensions.unaccent
import com.paulo.wtest.ui.home.adapters.PostalCodeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Paulo Henrique Teixeira.
 */

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    //region BaseFragment
    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate
    override val spaceIdToTop: Int = R.id.spaceTopHome


    val adapter = PostalCodeAdapter()

    override fun onInitViews() {
        binding.ivClearSearch.setOnClickListener {
            clearSearchText()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearSearch.isInvisible = s.isNullOrEmpty()
                val rancher = s?.unaccent()
                viewModel.getPostalCode(rancher)
                adapter.refresh()
            }
        })
        storagePermissionLauncher.launch(permissions)
        binding.rvPostalCode.adapter = adapter


        binding.clHome.viewTreeObserver.addOnGlobalLayoutListener {
            val insets = view?.let {
                ViewCompat.getRootWindowInsets(it)
            }
            binding.spaceKeyboard.isVisible =
                insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
            binding.spaceKeyboard.layoutParams.height =
                insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
        }
    }

    private fun clearSearchText() {
        binding.etSearch.setText("")
    }

    override fun onInitObservers() {
        lifecycleScope.launch {
            viewModel.flPostalCode.collectLatest { adapter.submitData(it) }
        }
        viewModel.ldDownloadProgress.observe(this) { progress ->
            showProgress(progress)
        }
        viewModel.ldDownloadCompleted.observe(this) {
            lifecycleScope.launch {
                binding.tvProgress.setText(R.string.downloaded_with_success)
                delay(500)
            }
        }

        viewModel.ldAlreadyDownloaded.observe(this) {
            binding.tvProgress.isVisible = false
            onLoading(false)
        }

        viewModel.ldDbPopulated.observe(this) {
            clearSearchText()
        }
        viewModel.ldDbPopulating.observe(this) {
            binding.tvProgress.setText(R.string.populating_database)
        }
    }

    override fun onFetchInitialData() {}

    override fun showError(message: Int) {
        showToast(message)
    }

    override fun onLoading(isLoading: Boolean) {
        binding.cpiHomeList.isVisible = isLoading
    }
    //endregion BaseFragment

    //region Local

    private val permissions = if (isAtLeastQ()) {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    private val storagePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { fullMap ->
        val isGranted = fullMap.all { it.value }
        if (isGranted) {
            viewModel.init()
        } else {
            if (shouldShowRequestPermissionRationale(permissions)) {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.review_your_permissions_in_settings)
                    .setPositiveButton(R.string.go_to_permissions) { _, _ ->
                        showAndroidSettings()
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        showToast(getString(R.string.review_your_permissions_in_settings))
                    }
                    .create()
                    .show()
            }
        }
    }

    private fun showAndroidSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context?.packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        androidSettingResultLauncher.launch(intent)
    }

    private val androidSettingResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.init()
        }
    }

    private fun showProgress(progress: Int) {
        binding.tvProgress.text = resources.getString(R.string.downloading_progress_percent, progress)
    }

    //endregion local
}