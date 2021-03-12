package com.alles.puzzle;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class Bot extends org.telegram.telegrambots.bots.TelegramLongPollingBot{

    String username;
    String token;
    String PathChromeDrive;
    public Bot(String username, String token, String PathChromeDrive){
        System.out.println("username: "+username+" Token: "+token+" Path: "+PathChromeDrive);
        this.username = username;
        this.token = token;
        this.PathChromeDrive = PathChromeDrive;
    }

    @Override
    public String getBotUsername() {

        return this.username;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }

    public String endPoint = "https://www.jigsawexplorer.com/jigsaw-puzzle-result/";
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());
        String[] args = update.getMessage().getText().split(" ");
        if (update.getMessage().isCommand() && args[0].equals("/puzzle")) {
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
            String result = null;
            try {
                result = this.post(url, String.valueOf(pieces));
            } catch (Exception e) {
                replyToChat(update, "post request error! dont bully me D:");
                e.printStackTrace();
                return;
            }
            result = result.substring(result.indexOf("id=\"short-link\" class=\"result-url\" style=\"margin-top: 10px; margin-bottom: 15px\" type=\"text\" readonly=\"readonly\" value=\"") + 1);
                result = result.substring(0, result.indexOf("\" />")).split("value=\"")[1];
                System.out.println(result);
                String MpLink = getMPlink(result);
                replyToChat(update, "Multiplayer Link: "+MpLink+" Pieces: "+String.valueOf(pieces));


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

    public String getMPlink(String SPlink) {
        System.setProperty("webdriver.chrome.driver", this.PathChromeDrive);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--headless");

        WebDriver driver = new ChromeDriver(chromeOptions);

        driver.get(SPlink);
        driver.findElement(By.id("jigex-multiplayer-btn")).click();
        driver.findElement(By.id("jigex-player-name")).sendKeys("PuzzleBot");
        driver.findElement(By.id("jigex-invite-btn")).click();
        while (driver.findElement(By.id("jigex-game-link")).getAttribute("value").equals("")){

        }
        String MP_LINK = driver.findElement(By.id("jigex-game-link")).getAttribute("value");
        System.out.println("MP_LINK:"+MP_LINK);
        driver.quit();
        return MP_LINK;



    }
    public static final MediaType FORM = MediaType.parse("multipart/form-data");
    private final OkHttpClient client = new OkHttpClient();

    public String post(URL image_url, String pieces) throws Exception {

        RequestBody formBody = new FormBody.Builder()
                .add("image-url", image_url.toString())
                .add("puzzle-nop", pieces)
                .add("credit-line", "")
                .add("credit-url", "")
                .add("color", "blue")
                .build();
        Request request = new Request.Builder().url(this.endPoint).post(formBody).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
