package com.alles.puzzle;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.logging.Logger;

public class websocket extends WebSocketClient {

    public CompletableFuture<String> message = new CompletableFuture<String>();
    public String getlastMessage() throws ExecutionException, InterruptedException {
        String msg = this.message.get();
        this.message = new CompletableFuture<String>();
        return msg;
    }

    public websocket(String s) {
        super(URI.create(s));
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        System.out.println(s);
        this.message.complete(s);
    }



    @Override
    public void onClose(int i, String s, boolean b) {

    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

}
