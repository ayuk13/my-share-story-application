package com.dicoding.mysharestory.ui.story

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.dicoding.mysharestory.R
import com.dicoding.mysharestory.databinding.FragmentDetailStoryBinding
import com.dicoding.mysharestory.ui.maps.MapsActivity
import com.dicoding.mysharestory.ui.story.CameraActivity.Companion.CAMERA_X_RESULT
import com.dicoding.mysharestory.ui.story.CameraActivity.Companion.PICTURE
import com.google.android.gms.maps.model.LatLng
import java.io.File

class DetailStoryFragment : Fragment() {

    private val viewModel: ListStoryViewModel by activityViewModels()
    private val args: DetailStoryFragmentArgs by navArgs()
    private var _binding: FragmentDetailStoryBinding? = null
    private val binding get() = _binding!!
    private var myFile: File? = null
    private var havePhoto: Boolean = false
    private var location : LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stories = args.detailStory
        val users = args.loginUser

        with(binding) {
            btnBack.setOnClickListener { requireActivity().onBackPressed() }

            stories?.let {
                Glide.with(this@DetailStoryFragment)
                    .asBitmap()
                    .fitCenter()
                    .transform(RoundedCorners(18))
                    .load(stories.photoUrl)
                    .into(ivStory)
                etNameUser.setText(stories.name)
                etDescription.setText(stories.description)
                btnUploadStory.visibility = View.GONE

                if(it.lat != null && it.lon != null){
                    etUserLoc.setText(
                        requireContext().getString(
                            R.string.latlon,
                            it.lat.toString(), it.lon.toString()
                        )
                    )

                    ivTagLocation.setOnClickListener { _ ->
                        Intent(requireActivity(), MapsActivity::class.java).apply{
                            putExtra(
                                MapsActivity.LOCATION_DATA,
                                LatLng(it.lat, it.lon)
                            )
                            startActivity(this)
                        }
                    }
                } else {
                    tvLocLabel.visibility = View.GONE
                    etUserLoc.visibility = View.GONE
                    ivTagLocation.visibility = View.GONE
                }
            }

            users?.let {
                etDescription.isEnabled = true

                llPhoto.visibility = View.VISIBLE
                tvLabelNameUser.visibility = View.GONE
                etNameUser.visibility = View.GONE
                ivStory.visibility = View.GONE

                llPhoto.setOnClickListener {
                    Intent(requireActivity(), CameraActivity::class.java).apply {
                        launcherIntentCameraX.launch(this)
                    }
                }

                ivStory.setOnClickListener {
                    Intent(requireActivity(), CameraActivity::class.java).apply {
                        launcherIntentCameraX.launch(this)
                    }
                }

                btnUploadStory.setOnClickListener {
                    if (etDescription.isNotEmpty && havePhoto && etUserLoc.isNotEmpty) {
                        args.loginUser?.token?.let {
                            viewModel.uploadStory(it,
                                myFile!!,
                                etDescription.text.toString(),
                                location?.latitude.toString(),
                                location?.longitude.toString())
                        }
                    } else {
                        Toast.makeText(requireContext(),
                            requireContext().getString(R.string.add_photo_desc_loc),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                ivTagLocation.setOnClickListener {
                    Intent(requireActivity(), MapsActivity::class.java).apply{
                        putExtra(
                            MapsActivity.INTENT_DATA, "get location"
                        )

                        if(etUserLoc.isNotEmpty){
                            val userLocation = etUserLoc.text
                                .toString()
                                .split(",")
                                .map{it.trim()}
                            putExtra(
                                MapsActivity.LOCATION_DATA,
                                LatLng(userLocation[0].toDouble(),userLocation[0].toDouble())
                            )
                        }

                        launcherMapsActivity.launch(this)
                    }
                }
            }
        }

        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.loadingStatus.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                if (isLoading) {
                    binding.btnBack.visibility = View.GONE
                    binding.btnUploadStory.visibility = View.GONE
                } else {
                    binding.btnBack.visibility = View.VISIBLE
                    binding.btnUploadStory.setImageResource(R.drawable.ic_confirm)
                }
            }
        }

        viewModel.successStatus.observe(viewLifecycleOwner) { isSuccess ->
            isSuccess.getContentIfNotHandled()?.let {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.upload_image_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    requireActivity().onBackPressed()
                } else {
                    Toast.makeText(
                        requireContext(),
                        requireContext().getString(R.string.upload_image_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            myFile = it.data?.getSerializableExtra(PICTURE) as File
            havePhoto = true
            binding.ivStory.visibility = View.VISIBLE
            val result = BitmapFactory.decodeFile(myFile?.path)
            Glide.with(this@DetailStoryFragment)
                .load(result)
                .transform(FitCenter(), RoundedCorners(18))
                .into(binding.ivStory)
            binding.llPhoto.visibility = View.GONE
        }
    }

    private val launcherMapsActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == MapsActivity.resultCode){
            location = it.data?.getParcelableExtra(MapsActivity.SET_LOCATION_DATA)
            binding.etUserLoc.setText(
                requireContext().getString(
                    R.string.latlon,
                    location?.latitude.toString(),
                    location?.longitude.toString()
                )
            )
        }
    }

}