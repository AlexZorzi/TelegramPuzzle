package com.alles.puzzle;

import okhttp3.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.MalformedURLException;
import java.net.URL;
import org.java_websocket.client.WebSocketClient;
public class Bot extends org.telegram.telegrambots.bots.TelegramLongPollingBot{

    String username;
    String token;
    Puzzle puzzle;

    public Bot(String username, String token){
        System.out.println("username: "+username+" Token: "+token);
        this.username = username;
        this.token = token;
        this.puzzle = new Puzzle();
    }

    @Override
    public String getBotUsername() {
        return this.username;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());
        String[] args = update.getMessage().getText().split(" ");
        if (update.getMessage().isCommand() && args[0].equals("/puzzle")) {
            if (args.length < 2) {
                replyToChat(update, "usage: /puzzle <url> (<pieces>)");
                return;
            }
            URL url = null;
            try {
                url = new URL(args[1]);
            } catch (MalformedURLException e) {
                replyToChat(update, "url is not valid");
                e.printStackTrace();
                return;
            }
            int pieces = 100;
            try {
                if (args.length > 2){
                    pieces = Integer.parseInt(args[2]);
                }
            }catch (Exception e){
                replyToChat(update, "Invalid value for puzzle pieces");
                return;
            }
            if (pieces < 6 || pieces > 1000) {
                replyToChat(update, "The initial number of pieces must be a numeric value from 6 to 1000.");
                return;
            }
            try {
                String result = puzzle.web(url.toString());
                System.out.println(result);
                String MpLink = result; // TODO
                replyToChat(update, "Multiplayer Link: "+MpLink+" Pieces: "+String.valueOf(pieces));
            } catch (Exception e) {
                replyToChat(update, "post request error! dont bully me D:");
                e.printStackTrace();
                return;
            }

        }

    }

    private void replyToChat(Update update, String msg){
        SendMessage reply = new SendMessage();
        reply.setChatId(update.getMessage().getChatId().toString());
        reply.setText(msg);
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    public static final MediaType FORM = MediaType.parse("multipart/form-data");
    private final OkHttpClient client = new OkHttpClient();

}
