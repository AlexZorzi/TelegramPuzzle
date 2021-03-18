package com.alles.puzzle;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static void main(String[] args) throws Exception {


        if(args.length != 2 && false){
            System.out.println("Missing Arguments! (botUsername, botToken");
            return;
        }else {
            try {
                TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
                telegramBotsApi.registerBot(new Bot(args[0],args[1]));
                //telegramBotsApi.registerBot(new Bot("botusername" ,"botToken"));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }
}
