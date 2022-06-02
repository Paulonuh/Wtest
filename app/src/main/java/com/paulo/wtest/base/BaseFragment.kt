package com.paulo.wtest.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.paulo.wtest.extensions.increaseMarginTop
import com.paulo.wtest.extensions.isAtLeastPie
import com.paulo.wtest.extensions.isAtLeastR


/**
 * Created by Paulo Henrique Teixeira.
 */

abstract class BaseFragment<VB : ViewBinding> : Fragment(),
    BaseFragmentContract {


    private var mViewBinding: VB? = null

    abstract val viewModel: BaseViewModel?
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val binding: VB
        get() = mViewBinding as VB

    abstract val spaceIdToTop: Int?
    private var mIsLayoutCreated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewBinding = bindingInflater.invoke(inflater)
        onInitObservers()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!mIsLayoutCreated) {
            setNotchHeightToTopMargin()

            viewModel?.messaging?.observe(viewLifecycleOwner) {
                showError(it)
            }

            viewModel?.loading?.observe(viewLifecycleOwner) { isLoading ->
                onLoading(isLoading)
            }

            onFetchInitialData()
            onInitViews()

            mIsLayoutCreated = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewBinding = null
    }

    private fun setFullScreen() {
        if (isAtLeastR()) {
            activity?.window?.setDecorFitsSystemWindows(false)
            view?.setPadding(0, 0, 0, 0)
        } else {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    private fun setNotchHeightToTopMargin() {
        val spaceId = spaceIdToTop
        if (spaceId != null) {
            val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    applyMarginByNotchHeight(view, spaceId)
                    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            }
            view?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    protected fun applyMarginByNotchHeight(parent: View?, spaceId: Int) {
        val spaceOnTop = parent?.findViewById<View?>(spaceId)

        var statusBarHeight = if (isAtLeastPie()) {
            getNotchHeight()
        } else {
            0
        }

        if (statusBarHeight == 0) {
            statusBarHeight = getStatusBarHeight()
        }

        spaceOnTop?.increaseMarginTop(statusBarHeight)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun getNotchHeight(): Int {
        val displayCutout =
            activity?.window?.decorView?.rootWindowInsets?.displayCutout

        return if (displayCutout?.boundingRects?.isNullOrEmpty() == false
            && displayCutout.boundingRects[0].height() > 0
        ) {
            val notchRect = displayCutout.boundingRects[0]
            notchRect.height()
        } else {
            0
        }
    }

    private fun getStatusBarHeight(): Int {
        val statusBarHeightId = resources.getIdentifier(
            "status_bar_height",
            "dimen",
            "android"
        )

        return resources.getDimensionPixelSize(statusBarHeightId)
    }


    fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        permissions.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it)) {
                return true
            }
        }

        return false
    }
}