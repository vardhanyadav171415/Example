package com.example.example.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.example.BuildConfig
import com.example.example.R
import com.example.example.data.Employee
import com.example.example.data.Gender
import com.example.example.data.Role
import com.example.example.databinding.FragmentEmployeeDetailBinding
import com.example.example.ui.EmployeeDetailFragmentArgs
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

const val PERMISSION_REQUEST_CAMERA=0
const val CAMERA_PHOTO_REQUEST=1
const val GALLERY_PHOTO_REQUEST=2
class EmployeeDetailFragment : Fragment() {
    private lateinit var toolbar_detail:androidx.appcompat.widget.Toolbar
    private lateinit var selectedPhotoPath: String
    private lateinit var binding:FragmentEmployeeDetailBinding
    private lateinit var employeerole:Spinner
    private lateinit var employeeage:Spinner
    private lateinit var employeename:EditText
    private lateinit var gendermale:RadioButton
    private lateinit var genderfemale:RadioButton
    private lateinit var genderother:RadioButton
    private lateinit var gendergroup:RadioGroup
    private lateinit var saveEmployee: Button
    private lateinit var employeePhoto:ImageView
    private lateinit var deleteEmployee:Button
    private lateinit var cameraButton:ImageView
    private lateinit var galleryButton:ImageView
    private lateinit var employeeResponisibility:EditText
    private lateinit var employeeEducation:EditText
    private lateinit var employeeExperience:EditText
    private lateinit var employeePhone:EditText
    private lateinit var employeeEmail:EditText
    private lateinit var employeeAddress:EditText
private lateinit var viewModel:EmployeeDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentEmployeeDetailBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        employeerole=binding.employeeRole
        employeename=binding.empName
        employeeage=binding.employeeAge
        gendermale=binding.genderMale
        genderfemale=binding.genderFemale
        genderother=binding.genderOther
        saveEmployee=binding.saveEmployee
        deleteEmployee=binding.deleteEmployee
        gendergroup=binding.genderGroup
        employeePhoto=binding.employeePhoto
        cameraButton=binding.photoFromCamera
        galleryButton=binding.photoFromGallery
        toolbar_detail=binding.toolbarDetail
        employeeResponisibility=binding.employeeResponsibilities
        employeeEducation=binding.employeeEducation
        employeeExperience=binding.employeeExperience
        employeePhone=binding.employeePhone
        employeeAddress=binding.employeeAddress
        employeeEmail=binding.employeeEmail
         return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel=ViewModelProviders.of(this).get(EmployeeDetailViewModel::class.java)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
         inflater.inflate(R.menu.detail_menu,menu)

    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return when(item.itemId){
             R.id.menu_share_data->{
                 shareEmployeeData()
                 true
             }
             else->super.onOptionsItemSelected(item)
         }
    }

    private fun shareEmployeeData() {
        val name=employeename.text.toString()
        val role=employeerole.selectedItemPosition.toString()
        val age=employeeage.selectedItemPosition+18
        val selectedStatusButton =gendergroup.findViewById<Button>(gendergroup.checkedRadioButtonId)
        val gender=selectedStatusButton.text
        val string ="Name=$name,role=$role,age=$age,gender=$gender"
         val sendIntent=Intent().apply {
             action=Intent.ACTION_SEND
             putExtra(Intent.EXTRA_TEXT,string)
             type="text/plain"
         }
        val shareIntent=Intent.createChooser(sendIntent,"Choose the app to send the data")
        startActivity(shareIntent)
    }
    private fun handleMenuItem(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_share_data -> {
                shareEmployeeData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        toolbar_detail.inflateMenu(R.menu.detail_menu)
        toolbar_detail.setOnMenuItemClickListener {
            handleMenuItem(it)
        }
        val navController=findNavController()
        val appBarConfiguration=AppBarConfiguration(navController.graph)
        toolbar_detail.setupWithNavController(navController,appBarConfiguration)
        val roles= mutableListOf<String>()
        Role.values().forEach { roles.add(it.name) }
        val arrayAdapter=ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item,roles)
        employeerole.adapter=arrayAdapter

        val ages= mutableListOf<Int>()
        for(i in 18 until 81){ages.add(i)}
        employeeage.adapter=ArrayAdapter(requireActivity(),android.R.layout.simple_spinner_item,ages)

        val id =EmployeeDetailFragmentArgs.fromBundle(requireArguments()).id
        viewModel.setEmployee(id)

        viewModel.employee.observe(viewLifecycleOwner, Observer{
            it?.let { setData(it) }
        })

        saveEmployee.setOnClickListener{
            saveEmployee()
        }
        deleteEmployee.setOnClickListener {
            deleteEmployee()
        }
        employeePhoto.setOnClickListener {
             employeePhoto.setImageResource(R.drawable.blankphoto)
            employeePhoto.tag=""
        }
        cameraButton.setOnClickListener {
            clickPhotoAfterPermission(it)
        }
        galleryButton.setOnClickListener {
            pickPhoto()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_PHOTO_REQUEST -> {
                    val uri = Uri.fromFile(File(selectedPhotoPath))
                    Picasso.get()
                        .load(uri)
                        .into(employeePhoto)
                    employeePhoto.tag = uri.toString()
                }
                GALLERY_PHOTO_REQUEST->{
                    data?.data?.also { uri->
                        val photoFile:File?=try{
                            createFile(requireActivity(),Environment.DIRECTORY_PICTURES,"jpg")
                        }catch (ex:IOException){
                            Toast.makeText(requireActivity(),"Error occured while selecting file {ex.message}",Toast.LENGTH_SHORT).show()
                            null
                        }
                        photoFile?.also {
                            try {


                                val resolver = requireActivity().applicationContext.contentResolver
                                resolver.openInputStream(uri).use { stream ->
                                    val output = FileOutputStream(photoFile)
                                    stream!!.copyTo(output)
                                }
                                val fileUri=Uri.fromFile(photoFile)
                                Picasso.get()
                                    .load(fileUri)
                                    .into(employeePhoto)

                                employeePhoto.tag=fileUri.toString()
                            }catch (e:FileNotFoundException){
                                e.printStackTrace()
                            }catch (e:IOException){
                                e.printStackTrace()
                            }
                        }
                    }
                }

                // Handle other request codes if needed
            }
        }
    }

    private fun clickPhotoAfterPermission(it: View) {
       if(ActivityCompat.checkSelfPermission(requireActivity(),android.Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
           clickPhoto()
       }
        else{
            requestCameraPermission(it)
       }

    }

    private fun requestCameraPermission(it: View) {
         if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.CAMERA)){
             val snack=Snackbar.make(it,"We need your permision to take a photo ,Please grant the permisson when asked",Snackbar.LENGTH_SHORT)
             snack.setAction("OK",{
                 requestPermissions(arrayOf(android.Manifest.permission.CAMERA),PERMISSION_REQUEST_CAMERA)
             })
             snack.show()

         }else{
             requestPermissions(arrayOf(android.Manifest.permission.CAMERA),PERMISSION_REQUEST_CAMERA)
         }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode== PERMISSION_REQUEST_CAMERA){
            if(grantResults.size==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                clickPhoto()
            }
            else{
                Toast.makeText(requireActivity(),"Permission Denied to use camera",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clickPhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takePictureIntent->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile:File?=try{
                    createFile(requireActivity(),Environment.DIRECTORY_PICTURES,"jpg")
            }catch (ex:IOException){
                Toast.makeText(requireActivity(),"error creating file {ex.message}",Toast.LENGTH_SHORT).show()
               null
            }
                photoFile.also {
                    selectedPhotoPath=it!!.absolutePath
                    val photoUri:Uri=FileProvider.getUriForFile(requireActivity(),BuildConfig.APPLICATION_ID +".fileprovider",it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
                    startActivityForResult(takePictureIntent, CAMERA_PHOTO_REQUEST)
                }


        }
//         Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {takepictureIntent->
//             requireActivity().packageManager?.also {
//                 val photoFile: File?=try{
//                     createFile(requireActivity(),Environment.DIRECTORY_PICTURES,"jpg")
//                 }catch (ex:IOException){
//                     Toast.makeText(requireActivity(),"Error occured while creating a file {ex.message}",Toast.LENGTH_SHORT).show()
//                     null
//                 }
//                 photoFile?.also {
//                     selectedPhotoPath=it.absolutePath
//                     val photoURI:Uri=FileProvider.getUriForFile(requireActivity(),BuildConfig.APPLICATION_ID + ".fileprovider",it)
//                 }
//                 takepictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoFile)
//                 startActivityForResult(takepictureIntent, CAMERA_PHOTO_REQUEST )
//             }?.let { takepictureIntent.resolveActivity(it) }

         }
    }

    private fun setData(it: Employee) {
        with(it.photo){
            if(isNotEmpty()){
                employeePhoto.setImageURI(Uri.parse(this))
                employeePhoto.tag=this
            }else{
                employeePhoto.setImageResource(R.drawable.blankphoto)
                employeePhoto.tag=""
            }
        }
        employeename.setText(it.name)
        employeerole.setSelection(it.role.toInt())
        employeeage.setSelection(it.Age-18)
        when(it.gender){
            Gender.Male.ordinal->{
                gendermale.isChecked=true
            }
            Gender.Female.ordinal->{
                genderfemale.isChecked=true
            }
            else->{
                genderother.isChecked=true
            }
        }
        employeeResponisibility.setText(it.responsibility)
        employeeExperience.setText(it.experience)
        employeeEducation.setText(it.education)
        if(it.phone >0){
            employeePhone.setText(it.phone.toString())
        }
        employeeEmail.setText(it.email)
        employeeAddress.setText(it.address)

    }
    private fun saveEmployee(){
        val name=employeename.text.toString()
        val role=employeerole.selectedItemPosition
        val age=employeeage.selectedItemPosition +18

        val SelectedStatusButton = gendergroup.findViewById<RadioButton>(gendergroup.checkedRadioButtonId)
        var gender=Gender.Other.ordinal
        if(SelectedStatusButton.text==Gender.Male.name){
            gender=Gender.Male.ordinal
        }
        else if(SelectedStatusButton.text==Gender.Female.name){
            gender=Gender.Female.ordinal
        }

        val photo=employeePhoto.tag as String
        val responsibilites=employeeResponisibility.text.toString()
        val experience=employeeExperience.text.toString()
        val education=employeeEducation.text.toString()
        val email=employeeEmail.text.toString()
        val address=employeeAddress.text.toString()

        if(name.isBlank()){
            requireActivity().showToast("Please enter the name of the employee")
            return
        }
        if(responsibilites.isBlank()){
            requireActivity().showToast("Please enter the responsibilites of the employee")
            return
        }
        if(experience.isBlank()){
            requireActivity().showToast("Please enter the name of the employee")
            return
        }
        if(education.isBlank()){
            requireActivity().showToast("Please enter the name of the employee")
            return
        }
        var phone:Long=0
        try {
            phone=employeePhone.text.toString().toLong()

        }catch (ex:java.lang.NumberFormatException){
            requireActivity().showToast("Please enter a valid phone number")
            return
        }
        if(email.isBlank()){
            requireActivity().showToast("Please enter the email of the employee")
            return
        }
        if(email.indexOf("@")<0){
            requireActivity().showToast("Please eneter the email of the employee")
            return
        }
        if(address.isBlank()){
            requireActivity().showToast("Please enter the address of the employee")
            return
        }
        val employee=Employee(viewModel.employeeId.value!!,name,role,age,gender,responsibilites,experience,education,phone,email,address,photo)
        viewModel.saveEmployee(employee)
        if(viewModel.employeeId.value==0L){
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            with(sharedPref.edit()){
                putString("LATEST_EMPLOYEE_NAME_KEY",name)
                commit()
            }
        }
        requireActivity().onBackPressed()
    }
    private fun deleteEmployee(){
        viewModel.deleteEmployee()
        requireActivity().onBackPressed()
    }
    private fun showToast(message:String){

    }
    private fun pickPhoto(){
          val pickphotoIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickphotoIntent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(pickphotoIntent, GALLERY_PHOTO_REQUEST)
        }
    }
}