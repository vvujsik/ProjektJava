package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class APIClient {
    private String api_key;

    public APIClient(String apiKey) {
        this.api_key = apiKey;
    }

    public RiotAccount getUserData(String gameName, String tag) throws Exception {
        String url = "https://europe.api.riotgames.com/riot/account/v1/accounts/by-riot-id/"
                + gameName + "/" + tag + "?api_key=" + api_key;

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
            RiotAccount account = objectMapper.readValue(response.toString(), RiotAccount.class);
            GetMoreUserData(account);
            GetRanksData(account);
            System.out.println("ID: "+account.getAccdata().getId() + " ||| Rank:" + account.getRankData().getLast().getQueueType()+ " LP: "+account.getRankData().getLast().getLeaguePoints());
            return account;
        } else {
            throw new Exception("Error: " + responseCode);
        }
    }

    public void GetMoreUserData(RiotAccount acc) throws Exception {
        String url = "https://eun1.api.riotgames.com/lol/summoner/v4/summoners/by-puuid/"+acc.getPuuid()+"?api_key="+api_key;
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
            AccountData data = objectMapper.readValue(response.toString(), AccountData.class);
            acc.setAccdata(data);
        } else {
            throw new Exception("Error: " + responseCode);
        }
    }

    public void GetRanksData(RiotAccount acc) throws Exception {
        String url = "https://eun1.api.riotgames.com/lol/league/v4/entries/by-summoner/"+acc.getAccdata().getId()+"?api_key="+api_key;
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

            System.out.println(response);

            ObjectMapper objectMapper = new ObjectMapper();
            ArrayList<RanksData> ranksDataList = objectMapper.readValue(response.toString(), new TypeReference<ArrayList<RanksData>>(){});
            for (RanksData data : ranksDataList){
                System.out.println(data.getQueueType()+ data.getLeaguePoints());
            }
            acc.setRanks(ranksDataList);
        } else {
            throw new Exception("Error: " + responseCode);
        }
    }

    public void getLastMatches (RiotAccount user,int start, int count) throws Exception {
        ArrayList<String> matches = new ArrayList<String>();
        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/by-puuid/" + user.getPuuid() + "/ids?start="+ start +"&count="+count+"&api_key=" + this.api_key;

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

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String[] matchIdsArray = objectMapper.readValue(response.toString(), String[].class);
                ArrayList<String> matchIds = new ArrayList<>(Arrays.asList(matchIdsArray));
                //matchIds.forEach(item -> System.out.println(item));
                user.addLastMatches(matchIds);
            } catch (Exception ex) {
                throw new Exception("RETURN");
            }
        }
        else {
            throw new Exception("Error: " + responseCode);
        }
    }

    public LoLMatch getMatchData(String gameID) throws Exception {
        String url = "https://europe.api.riotgames.com/lol/match/v5/matches/"+ gameID +"?api_key=" + api_key;

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
            //System.out.println(response.toString());
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            try {
                LoLMatch game = objectMapper.readValue(response.toString(), LoLMatch.class);
                return game;
            } catch (Exception ex) {
                throw new Exception("RETURN");
            }
        } else {
            throw new Exception("Error SSSS: " + responseCode);
        }
    }
}
