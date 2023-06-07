package com.example.example.ui

import android.os.Bundle
import android.os.Message
import android.os.UserManager.UserOperationException
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LAYOUT_DIRECTION_INHERIT
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.example.R
import com.example.example.databinding.FragmentChatBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.ktx.Firebase

const val MESSAGE_BASE_PATH="messages"
class ChatFragment : Fragment() {
    private lateinit var binding:FragmentChatBinding
    private lateinit var sendmessage:ImageButton
    private lateinit var editText: EditText
    private lateinit var auth:FirebaseAuth
    private lateinit var dbRef: FirebaseDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter:FirebaseRecyclerAdapter<UserMessage, ViewHolder?>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding=FragmentChatBinding.inflate(layoutInflater)
        editText=binding.editTextTextPersonName
        sendmessage=binding.imageButton
        recyclerView=binding.recyclerView
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        dbRef=FirebaseDatabase.getInstance()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendmessage.setOnClickListener {
            val messageText=editText.text.toString()
            if(messageText.isBlank()){
                Toast.makeText(requireContext(),"Please enter a message to send",Toast.LENGTH_SHORT).show()

            }
            else{
                val ref=dbRef.getReference(MESSAGE_BASE_PATH).push()
                val userMessage=UserMessage(auth.currentUser?.email ?: "unknown",messageText)
                ref.setValue(userMessage).addOnSuccessListener {
                    editText.setText("")
                }
                    .addOnFailureListener { ex:java.lang.Exception->
                        Toast.makeText(requireContext(),"Failed to send the message ${ex.toString()}",Toast.LENGTH_SHORT).show()
                    }
            }
        }
        recyclerView.layoutManager=LinearLayoutManager(requireActivity())
        setupMessageList()
    }

    private fun setupMessageList() {
        val query: Query = dbRef.reference.child(MESSAGE_BASE_PATH)
        val options: FirebaseRecyclerOptions<UserMessage> = FirebaseRecyclerOptions.Builder<UserMessage>()
            .setQuery(query) { snapshot ->
                UserMessage(
                    snapshot.child("email").value.toString(),
                    snapshot.child("message").value.toString()
                )
            }
            .build()

        adapter = object: FirebaseRecyclerAdapter<UserMessage, ViewHolder?>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
                return ViewHolder(view)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserMessage) {
                holder.bind(model)
            }
        }

        recyclerView.adapter = adapter
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val userMessage: TextView = view.findViewById(R.id.user_message)
        private val userEmail: TextView = view.findViewById(R.id.user_email)

        fun bind(message: UserMessage) {
            with(message) {
                userEmail.text = email
                userMessage.text = this.message
            }
        }
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}