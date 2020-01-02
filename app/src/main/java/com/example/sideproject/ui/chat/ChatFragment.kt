package com.example.sideproject.ui.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sideproject.App.Companion.roomDatabase
import com.example.sideproject.R
import com.example.sideproject.data.model.Chat
import com.example.sideproject.data.remote.ServiceClient
import com.example.sideproject.data.remote.user.UserRepository
import com.example.sideproject.utils.RxTransFormers
import kotlinx.android.synthetic.main.fragment_chat.*
import java.net.URISyntaxException

class ChatFragment : Fragment() {

    private lateinit var mViewModel: ChatViewModel
    private lateinit var userName: String

    private var mAdapter: ChatListAdapter = ChatListAdapter()
    private var mHandler: Handler = Handler(Handler.Callback { msg ->
        mAdapter.appendMsg(msg.obj as Chat)
        recyclerView.scrollToPosition((mAdapter.itemCount - 1))
        true
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
        mViewModel.state.observe(this, Observer {
            val chat = it ?: return@Observer
            if (chat.isConnect) {
                val handlerMessage = Message.obtain()
                handlerMessage.obj = chat
                mHandler.sendMessage(handlerMessage)
            } else {
                Toast.makeText(context, "connect false", Toast.LENGTH_SHORT).show()
            }
        })

        recyclerView.adapter = mAdapter
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        button_submit.setOnClickListener {
            if (editText_msg.text.toString().isEmpty())
                return@setOnClickListener
            mViewModel.sendMessage(editText_msg.text.toString())
        }
    }

    @SuppressLint("CheckResult")
    override fun onStart() {
        super.onStart()
        val repository = UserRepository(
            service = ServiceClient.getService(), userModel = roomDatabase.userModel())
        try {
            repository.getUserInfoFromLocal()
                .compose(RxTransFormers.applySchedulerSingle())
                .subscribe(
                    {
                        if (it != null)
                            mViewModel.initialClient(it.name)
                    },
                    { e -> e.printStackTrace() })
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        mViewModel.disconnect()
    }
}
