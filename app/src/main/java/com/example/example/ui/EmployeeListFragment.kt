package com.example.example.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.appsearch.GlobalSearchSession
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.example.BuildConfig
import com.example.example.R
import com.example.example.data.Employee
import com.example.example.databinding.FragmentEmployeeListBinding
import com.example.example.ui.EmployeeListFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.lang.System.out


const val READ_FILE_REQUEST=1
const val CREATE_FILE_REQUEST=2

class EmployeeListFragment : Fragment() {
    private lateinit var toolbar: Toolbar
    private lateinit var employeeList: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentEmployeeListBinding
    private lateinit var viewModel:EmployeeListViewModel
    private lateinit var add_employee:FloatingActionButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentEmployeeListBinding.inflate(layoutInflater)
        add_employee=binding.addemployee
        recyclerView=binding.employeeRecylcerview
        employeeList=binding.noEmployeeRecord
        toolbar=binding.toolbar
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setHasOptionsMenu(true)
        viewModel=ViewModelProviders.of(this).get(EmployeeListViewModel::class.java)

    }

//    @Deprecated("Deprecated in Java")
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu,menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//         return when(item.itemId){
//             R.id.menu_import_data->{
//                 importEmployee()
//                 true
//             }
//             R.id.menu_export_data->{
//                 GlobalScope.launch {
//                     exportEmployees()
//                 }
//                 true
//             }
//             R.id.menu_latest_employee->{
//                 val sharedPref=requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return true
//                 val name=sharedPref.getString("LATEST_EMPLOYEE_NAME_KEY","")
//                 if(name.isNullOrEmpty()){
//                     Toast.makeText(requireActivity(),"The name of the employee is ${name}",Toast.LENGTH_SHORT).show()
//
//                 }else{
//                     Toast.makeText(requireActivity(),"No employee added yet",Toast.LENGTH_SHORT).show()
//
//                 }
//                 true
//             }
//             else->super.onOptionsItemSelected(item)
//         }
//    }

    private fun importEmployee() {
       val intent= Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type="text/csv"
        }
        intent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(intent, READ_FILE_REQUEST)
        }
//           Intent(Intent.ACTION_GET_CONTENT).also {readFileIntent->
//               readFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
//               readFileIntent.type="text/*"
//               readFileIntent.resolveActivity(requireActivity().packageManager).also {
//                   startActivityForResult(readFileIntent, READ_FILE_REQUEST)
//               }
//           }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode== Activity.RESULT_OK){
            when(resultCode){
                READ_FILE_REQUEST->{
                    GlobalScope.launch {
                        data?.data?.also {uri->
                              readFromFile(uri)
                        }
                    }
                }
                CREATE_FILE_REQUEST->{
                    data?.data?.also { uri ->
                        GlobalScope.launch {
                            if(writetoFile(uri)){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(requireActivity(),"file exported Successfully",Toast.LENGTH_SHORT).show()

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupNavigationDrawer() {
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = requireActivity().findViewById<NavigationView>(R.id.navigation_view)

        NavigationUI.setupWithNavController(toolbar, navController,
        AppBarConfiguration.Builder(R.id.navigation,R.id.employeeListFragment).setDrawerLayout(drawerLayout).build()
        )
        navigationView.setupWithNavController(navController)

        navigationView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()

            when (it.itemId) {
                R.id.add_new ->  findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(
                        0
                    )
                )
                R.id.contactus ->  findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToContactUsFragment()
                )
                R.id.about ->  findNavController().navigate(
                    EmployeeListFragmentDirections.actionEmployeeListFragmentToAboutUsFragment()
                )
                R.id.menu_export_data -> {
                    GlobalScope.launch {
                        exportEmployees()
                    }
                }
                R.id.menu_import_data -> {
                    Intent(Intent.ACTION_GET_CONTENT).also { readFileIntent ->
                        readFileIntent.addCategory(Intent.CATEGORY_OPENABLE)
                        readFileIntent.type = "text/*"
                        readFileIntent.resolveActivity(requireActivity().packageManager)?.also {
                            startActivityForResult(readFileIntent,
                                READ_FILE_REQUEST
                            )
                        }
                    }
                }
                R.id.menu_alarm->{
                    val alarmmanger= requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                     val alarmIntent=Intent(context,AlarmReceiver::class.java).let {
                         PendingIntent.getBroadcast(context,0,it,0)
                     }
                    when{
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M->{
                            alarmmanger.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+60*1000
                            ,alarmIntent)
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT->{
                            alarmmanger.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+60*1000,alarmIntent)

                        }else->{
                            alarmmanger.set(
                                AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+60*100,alarmIntent)

                        }
                    }
                   requireActivity().showToast("Alarm set successfully",Toast.LENGTH_SHORT)

                }
                R.id.menu_latest_employee_name -> {
                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
                    val name = sharedPref?.getString("LATEST_EMPLOYEE_NAME_KEY", "")
                    if(!name.isNullOrEmpty()){
                    } else if(name != null){
                        requireActivity().showToast("No Employee Added")
                    }
                }
                R.id.signout->{
                    val auth=FirebaseAuth.getInstance()
                    auth.signOut()
                    auth.addAuthStateListener {
                        if(auth.currentUser==null)
                        {
                            val currId=findNavController().currentDestination!!.id
                            if(currId==R.id.employeeListFragment){
                                findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToLoginFragment2())
                            }
                        }
                    }
                }
                R.id.chat->{
                    findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToChatFragment())
                }
            }

            true

         }
    }
    private suspend fun writetoFile(uri: Uri): Boolean {
         try{
             requireActivity().contentResolver.openFileDescriptor(uri,"w")?.use{pfd->
                 FileOutputStream(pfd.fileDescriptor).use{outStream->
                     val employes =viewModel.getEmployeeList()
                     if(employes.isNotEmpty()){
                         employes.forEach{
                             outStream.write((it.name +","+it.role +","+it.Age+","+it.gender+"\n").toByteArray())
                         }
                     }
                 }
             }
         }catch (e:FileNotFoundException){
             e.printStackTrace()
             return false
         }catch (e:IOException){
             e.printStackTrace()
             return false
         }
        return true
    }

    private suspend fun parseCSVFile(it: FileInputStream) {
             val employees= mutableListOf<Employee>()
        BufferedReader(InputStreamReader(it)).forEachLine {
            val tokens= it.split(",")
            employees.add(Employee(id=0,name=tokens[0],role=tokens[1].toInt(),
            Age=tokens[2].toInt(),gender=tokens[3].toInt(),photo="", responsibility = "", experience = "", education = "", phone = 0, email = "", address = ""))
        }
        if(employees.isNotEmpty()){
            viewModel.insertEmployees(employees)
        }

    }

    private suspend fun readFromFile(uri: Uri) {
        try{
            requireActivity().applicationContext.contentResolver.openFileDescriptor(uri,"r")?.use {
                withContext(Dispatchers.IO){
                    FileInputStream(it.fileDescriptor).use{
                        parseCSVFile(it)
                    }
                }
            }
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
    private suspend fun exportEmployees(){
        val intent=Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type="text/csv"
            putExtra(Intent.EXTRA_TITLE,"employee_list.csv")
        }
        startActivityForResult(intent, CREATE_FILE_REQUEST)
    }
//    private suspend fun exportEmployee() {
//            var csvFile: File?=null
//            withContext(Dispatchers.IO){
//                csvFile=try {
//                    createFile(requireActivity(),"Documents","csv")
//
//                }catch (ex:IOException){
//                    Toast.makeText(requireActivity(),"error occured while creating file {ex.message}",Toast.LENGTH_SHORT).show()
//                    null
//                }
//                csvFile?.printWriter()?.use {
//                    val employes =viewModel.getEmployeeList()
//                    if(employes.isNotEmpty()){
//                        employes.forEach{
//                            out.println(it.name +","+it.role +","+it.Age+","+it.gender)
//                        }
//                    }
//                }
//            }
//        withContext(Dispatchers.Main){
//            csvFile?.let {
//                val uri=FileProvider.getUriForFile(requireActivity(),BuildConfig.APPLICATION_ID +".fileprovider",it)
//                launchFile(uri,".csv")
//            }
//        }
//
//    }

    private fun launchFile(uri: Uri, s: String) {
          val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s)
        val intent=Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(uri,mimeType)
        if(intent.resolveActivity(requireActivity().packageManager)!=null){
            startActivity(intent)
        }
        else{
            Toast.makeText(requireActivity(),"No app to read csv file",Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
         setupNavigationDrawer()
        with(recyclerView){
            layoutManager=LinearLayoutManager(requireActivity())
            adapter = EmployeeAdapter {show, id ->
                if(show){
                    findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(id))
                } else{
                    findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeShowFragment(id))
                }
            }

        }
        add_employee.setOnClickListener {
            findNavController().navigate(EmployeeListFragmentDirections.actionEmployeeListFragmentToEmployeeDetailFragment(0))
        }
        viewModel.employees.observe(viewLifecycleOwner, Observer {

            (recyclerView.adapter as EmployeeAdapter).submitList(it)
            if(it.isNotEmpty()){
                employeeList.visibility=View.INVISIBLE
            }
            else{
                employeeList.visibility=View.VISIBLE
            }
        })
    }
}

//    private fun handleMenuItem(it: MenuItem):Boolean {
//        return when(it.itemId){
//            R.id.menu_import_data->{
//                importEmployee()
//                true
//            }
//            R.id.menu_export_data->{
//                GlobalScope.launch {
//                    exportEmployees()
//                }
//                true
//            }
//            R.id.menu_latest_employee->{
//                val sharedPref=requireActivity().getPreferences(Context.MODE_PRIVATE) ?: return true
//                val name=sharedPref.getString("LATEST_EMPLOYEE_NAME_KEY","")
//                if(name.isNullOrEmpty()){
//                    Toast.makeText(requireActivity(),"The name of the employee is ${name}",Toast.LENGTH_SHORT).show()
//
//                }else{
//                    Toast.makeText(requireActivity(),"No employee added yet",Toast.LENGTH_SHORT).show()
//
//                }
//                true
//            }
//            else->super.onOptionsItemSelected(it)
//        }
//
//    }
//
//}

