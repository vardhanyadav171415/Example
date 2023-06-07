package com.example.example.data

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.example.R
import com.example.example.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase


class LoginFragment : Fragment() {
    private lateinit var binding:FragmentLoginBinding
    private lateinit var auth:FirebaseAuth
    private lateinit var register:TextView
    private lateinit var login: Button
    private lateinit var userEmail:TextView
    private lateinit var userPass:TextView
    private lateinit var progress:ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            findNavController().navigate(LoginFragmentDirections.actionLoginFragment2ToEmployeeListFragment())
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=  FragmentLoginBinding.inflate(layoutInflater)
        register=binding.registerTv
        login=binding.registerButton
        userEmail=binding.registerEmail
        userPass=binding.registerPassword
        progress=binding.progressBarRegistered
        return  binding.root
    }

    private fun validateInput(email: String, pass: String): Boolean {
        var valid=true
        if(email.isBlank()){
            userEmail.error="Please enter an email address"
            valid=false
        }
        if(pass.isBlank()){
            userPass.error="Please enter Password"
            valid=false
        }
        else if(pass.length <=0){
            userPass.error="Password should be 8 characters or more"
            valid=false
        }
        return valid
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragment2ToRegisterFragment())
        }
        login.setOnClickListener {
            userEmail.error=null
            userPass.error=null
            val email=userEmail.text.toString()
            val pass=userEmail.text.toString()

            if(validateInput(email,pass)){
                progress.visibility=View.VISIBLE

                auth.signInWithEmailAndPassword(email,pass)
                    .addOnCompleteListener(requireActivity()){task->
                        progress.visibility=View.INVISIBLE
                        if(task.isSuccessful){
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragment2ToEmployeeListFragment())
                        }
                        else{
                            val toast= Toast.makeText(requireActivity(),"Authentication failed: ${task.exception.toString()}",Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER_VERTICAL,0,0)
                            toast.show()
                        }
                    }
            }
        }
    }

}