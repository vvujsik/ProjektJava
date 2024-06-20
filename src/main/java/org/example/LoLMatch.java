package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoLMatch {
    private Metadata metadata = new Metadata();
    private Info info = new Info();

    public List<Participants> getParticipants(boolean isTeam1) {
        int teamID = isTeam1 ? 100 : 200;
        List<Participants> team = new ArrayList<>();
        for (Participants participant : info.getParticipants()) {
            if (teamID == participant.getTeamId()) {
                team.add(participant);
            }
        }
        return team;
    }
    // Gettery i settery
    public Metadata getMetadata() {
        return metadata;
    }
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    public Info getInfo() {
        return info;
    }
    public void setInfo(Info info) {
        this.info = info;
    }

    @Override
    public String toString() {
        //ArrayList<String> Participants = metadata.getParticipants();
        ArrayList<Participants> participantsInfo = info.getParticipants();
        String wynik = metadata.getMatchId() + "\n";
        for (Participants participant : participantsInfo) {
            wynik += participant.getRiotIdGameName() + " --- " + participant.getChampionName() + "\n";
        }
        return wynik;
    }
    public String toString(String userPuuid) {
        ArrayList<String> Participants = metadata.getParticipants();
        ArrayList<Participants> participantsInfo = info.getParticipants();
        String wynik = "";
        Participants user = participantsInfo.stream().filter(participants -> participants.getPuuid().equals(userPuuid)).findFirst().orElse(null);
        if (user != null){
            wynik +="K: " + user.getKills() + " / D: " + user.getDeaths() + " / A: " + user.getAssists();
        }
        return wynik;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Metadata {
    private String matchId;
    private ArrayList<String> participants = new ArrayList<String>();

    // Gettery i settery
    public String getMatchId() {
        return matchId;
    }
    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
    public ArrayList<String> getParticipants() {
        return participants;
    }
    @Override
    public String toString() {
        return matchId;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Info {
    private ArrayList<Participants> participants = new ArrayList<Participants>();

    public ArrayList<Participants> getParticipants() {
        return participants;
    }
    public Participants getCurrentParticipant(String puuid) {
        return participants.stream().filter(participants -> participants.getPuuid().equals(puuid)).findFirst().orElse(null);
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Participants {
    private String puuid;
    private String championName;
    private int kills;
    private int assists;
    private int deaths;
    private boolean win;
    private int teamId;
    private boolean gameEndedInEarlySurrender;
    private String riotIdGameName;
    private String riotIdTagline;
    private int item0;
    private int item1;
    private int item2;
    private int item3;
    private int item4;
    private int item5;
    private int item6; //trinket

    public int getItem(int i){
        if (i == 0) return item0;
        else if (i == 1) return item1;
        else if (i == 2) return item2;
        else if (i == 3) return item3;
        else if (i == 4) return item4;
        else if (i == 5) return item5;
        else if (i == 6) return item6;
        else return -1;
    }
    public void setItem0(int item0){
        this.item0=item0;
    }
    public void setItem1(int item1){
        this.item1=item1;
    }
    public void setItem2(int item2){
        this.item2=item2;
    }
    public void setItem3(int item3){
        this.item3=item3;
    }
    public void setItem4(int item4){
        this.item4=item4;
    }
    public void setItem5(int item5){
        this.item5=item5;
    }
    public void setItem6(int item6){
        this.item6=item6;
    }

    public File ItemToFile(int i){
        int item = getItem(i);
        //System.out.println(item);
        if (item == -1) return null;
        else if (item == 0) return new File("C:\\Users\\Łukasz\\Downloads\\dragontail-14.11.1\\14.11.1\\img\\item\\placeholder.png");
        return new File("C:\\Users\\Łukasz\\Downloads\\dragontail-14.11.1\\14.11.1\\img\\item\\"+item+".png");
        //return new File("C:\\Users\\Łukasz\\Downloads\\dragontail-14.11.1\\14.11.1\\img\\item\\placeholder.png");
    }

    public int getTeamId() {
        return teamId;
    }

    // Gettery i settery dla puuid
    public String getPuuid() {
        return puuid;
    }

    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }

    // Gettery i settery dla championName
    public String getChampionName() {
        return championName;
    }

    public void setChampionName(String championName) {
        this.championName = championName;
    }

    // Gettery i settery dla kills
    public int getKills() {
        return kills;
    }
    public void setKills ( int kills){
        this.kills = kills;
    }

        // Gettery i settery dla assists
    public int getAssists () {
        return assists;
    }

    public void setAssists ( int assists){
        this.assists = assists;
    }

        // Gettery i settery dla deaths
    public int getDeaths () {
        return deaths;
    }

    public void setDeaths ( int deaths){
        this.deaths = deaths;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public void setGameEndedInEarlySurrender(boolean gameEndedInEarlySurrender) {
        this.gameEndedInEarlySurrender = gameEndedInEarlySurrender;
    }

    public boolean isGameEndedInEarlySurrender() {
        return gameEndedInEarlySurrender;
    }

    public int isWin() {
        if (gameEndedInEarlySurrender == true) {
            return -1;
        }
        return win ? 1 : 0;
    }

    public String getRiotIdGameName() {
        return riotIdGameName;
    }

    public String getRiotIdTagline() {
        return riotIdTagline;
    }

    public File championToFile(){
        String path = "C:\\Users\\Łukasz\\Downloads\\dragontail-14.11.1\\14.11.1\\img\\champion\\"+championName+".png";
        return new File(path);
    }

}
