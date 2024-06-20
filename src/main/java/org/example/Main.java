package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws MalformedURLException {
        String api_key = "RGAPI-950f3a0c-6fd4-47bd-aa16-c83b54af4757";
        // https://europe.api.riotgames.com/lol/match/v5/matches/EUN1_3603751907?api_key=RGAPI-950f3a0c-6fd4-47bd-aa16-c83b54af4757
        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/EUN1_3603751907?api_key="
                + api_key;
        LoLMatch game = null;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                ObjectMapper objectMapper = new ObjectMapper();
                game=objectMapper.readValue(response.toString(), LoLMatch.class);
            } else {
                throw new Exception("Error: " + responseCode);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        System.out.println(game.toString());
    }
}