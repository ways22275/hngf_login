package com.example.sideproject.ui.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sideproject.data.model.Chat;
import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import static com.example.sideproject.data.model.ChatKt.toChatConnectEntity;
import static com.example.sideproject.utils.Constant.DOMAIN_SOCKET;

public class ChatViewModel extends ViewModel {

    private WebSocketClient _socketClient;
    private Gson gson = new Gson();

    private MutableLiveData<Chat> _state = new MutableLiveData<>();
    LiveData<Chat> state = _state;

    void initialClient(String userName) throws URISyntaxException {

        if (_socketClient == null) {
            _socketClient = new WebSocketClient(
                    new URI(DOMAIN_SOCKET + userName),
                    new Draft_6455() {},null,100000) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("ws", "onOpen");
                    _state.postValue(toChatConnectEntity(true));
                }

                @Override
                public void onMessage(String message) {
                    Chat chatModel = gson.fromJson(message, Chat.class);
                    chatModel.setConnect(true);
                    _state.postValue(chatModel);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    super.onClosing(code, reason, remote);
                    _state.postValue(toChatConnectEntity(false));
                }

                @Override
                public void onError(Exception ex) {
                    Log.d("ws", "onError : " + ex.getMessage());
                }
            };
        }
        _socketClient.connect();
    }

    public void sendMessage(String msg) {
        if (msg != null && !msg.isEmpty()) {
            _socketClient.send(msg);
        }
    }

    public void disconnect() {
        if (_socketClient != null)
            _socketClient.close();
        _state.setValue(toChatConnectEntity(false));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
