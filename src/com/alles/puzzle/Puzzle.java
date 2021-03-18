package com.alles.puzzle;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Puzzle {

    public String getID(){
        String[] UUID = java.util.UUID.randomUUID().toString().split("-");
        return UUID[0]+"-"+UUID[4].substring(1);
    }
    public String cleanStr(Invocable inv, String s) throws ScriptException, NoSuchMethodException {
        String res = (String) inv.invokeFunction("a", s);
        System.out.println(res);
        return res;
    }

    String piecesArray;
    String nop;
    public void getPiecesArray(int pieces){
        List<Integer> range = IntStream.rangeClosed(1, pieces)
                .boxed().collect(Collectors.toList());
        Random random = new Random();

        Collections.shuffle(range);
        this.nop = String.valueOf(range.size());
        this.piecesArray = range.toString();
    }

    private int colrow;
    public void findColRow(int pieces){
        while (true){
            double sqr = Math.sqrt(pieces);
            if (sqr % 1 == 0){
                this.colrow = (int) sqr;
                return;
            }
            ++pieces;
        }

    }
    // TODO: Separate websockets and clients
    public String web(String image, int pieces) throws Exception {
        findColRow(pieces);
        getPiecesArray(this.colrow*this.colrow);

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        engine.eval("\n" +
                "\n" +
                "stringy = function(e) {\n" +
                "    return \"[object Object]\" === Object.prototype.toString.call(e) ? \"~j~\" + JSON.stringify(e) : String(e)\n" +
                "}\n" +
                "\n" +
                "a = function(e) {\n" +
                "\n" +
                "    e = JSON.parse(e.replaceAll(\"'\",'\"'));\n" +
                "    for (var t, o = \"\", r = 0, n = (e = Array.isArray(e) ? e : [e]).length; r < n; r++)\n" +
                "        t = null === e[r] || void 0 === e[r] ? \"\" : stringy(e[r]),\n" +
                "            o += \"~m~\" + t.length+ \"~m~\" + t;\n" +
                "    return o\n" +
                "}");
        Invocable inv = (Invocable) engine;

        // WebSocket 1
        String id = getID();
        websocket client = new websocket("wss://ns.exitgames.com:19093/a0a94d7b-d90a-4161-ab65-91e8e3752c8c?libversion=4.1.0.0");
        client.connect();
        client.getlastMessage();
        client.send(cleanStr(inv,"{\"req\":230,\"vals\":[224,\"a0a94d7b-d90a-4161-ab65-91e8e3752c8c\",220,\"1.0\",225,\""+id+"-PuzzleBot\",210,\"us\"]}"));
        String message = client.getlastMessage().split("~j~")[1];
        JsonElement root = JsonParser.parseString(message);
        String link2 = root.getAsJsonObject().getAsJsonArray("vals").get(3).toString().replace("\"","");
        String key2 = root.getAsJsonObject().getAsJsonArray("vals").get(5).toString().replace("\"","");
        System.out.println(link2+" "+key2);
        client.closeBlocking();

        // Websocket 2
        websocket client2 = new websocket(link2+"/a0a94d7b-d90a-4161-ab65-91e8e3752c8c?libversion=4.1.0.0");
        client2.connect();
        client2.getlastMessage();
        client2.send(cleanStr(inv,"{\"req\":230,\"vals\":[221,\""+key2+"\"]}"));
        System.out.println(client2.getlastMessage());
        String message2 = client2.getlastMessage();
        System.out.println(message2);
        client2.send(cleanStr(inv,"{\"req\": 229, \"vals\": []}"));
        System.out.println(client2.getlastMessage());
        client2.send(cleanStr(inv,"{\"req\":227,\"vals\":[255,\""+id+"\",248,{\"254\":false,\"255\":20,\"pid\":null,\"nop\":"+this.nop+",\"rows\":"+this.colrow+",\"cols\":"+this.colrow+",\"lay\":"+piecesArray+",\"tabr\":4,\"tabb\":4,\"shp\":4,\"rot\":false,\"ang\":null,\"edo\":false,\"elpsd\":0,\"strt\":"+ Instant.now().getEpochSecond()+",\"last\":"+Instant.now().getEpochSecond()+",\"sesn\":0,\"dev\":true,\"indie\":false,\"url\":\""+image+"\"},241,true,250,true,236,0,235,1,232,true]}"));
        String message3 = client2.getlastMessage().split("~j~")[1];
        System.out.println(message3);
        JsonElement root3 = JsonParser.parseString(message);
        String link3 = root3.getAsJsonObject().getAsJsonArray("vals").get(1).toString().replace("\"","");
        String key3 = root3.getAsJsonObject().getAsJsonArray("vals").get(5).toString().replace("\"","");
        System.out.println(link3+key3);
        client2.close();

        // websocket 3
        websocket client3 = new websocket("wss://gcash053.exitgames.com:19091"+"/a0a94d7b-d90a-4161-ab65-91e8e3752c8c?libversion=4.1.0.0");
        client3.connectBlocking();
        client3.send(cleanStr(inv,"{\"req\":230,\"vals\":[224,\"a0a94d7b-d90a-4161-ab65-91e8e3752c8c\",220,\"1.0\",221,\""+key3+"\",225,\""+id+"-PuzzleBot\"]}"));
        System.out.println(client3.getlastMessage());
        client3.send(cleanStr(inv,"{\"req\":227,\"vals\":[255,\""+id+"\",248,{\"254\":false,\"255\":20,\"pid\":null,\"nop\":"+this.nop+",\"rows\":"+this.colrow+",\"cols\":"+this.colrow+",\"lay\":"+piecesArray+",\"tabr\":26,\"tabb\":20,\"shp\":4,\"rot\":false,\"ang\":null,\"edo\":false,\"elpsd\":0,\"strt\":"+Instant.now().getEpochSecond()+",\"last\":"+Instant.now().getEpochSecond()+",\"sesn\":0,\"dev\":true,\"indie\":false,\"url\":\""+image+"\"},241,true,250,true,236,0,235,1,232,true,249,{\"255\":\"PuzzleBot\"}]}"));
        System.out.println(client3.getlastMessage());
        client3.send(cleanStr(inv,"{\"req\":252,\"vals\":[251,{\"sesn\":1},250,true]}"));
        client3.send(cleanStr(inv,"{\"req\":252,\"vals\":[251,{\"strtd\":false},250,true]}"));
        client3.getlastMessage();
        client3.getlastMessage();
        client3.getlastMessage();
        OkHttpClient ok = new OkHttpClient();

        String mpLink = get("https://www.jigsawexplorer.com/scripts/get-game-link.php?game_id="+id+"&region=us&custom=true");
        client3.send(cleanStr(inv,"{\"req\":252,\"vals\":[251,{\"lnk\":\""+mpLink+"\"},250,true]}"));
        System.out.println(client3.getlastMessage());
        System.out.println(mpLink);
        client3.closeBlocking();
        return mpLink;
    }

    public String get(String uri) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}