package org.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
import java.util.ArrayList;

public class RiotAccount {
    private String puuid;
    private String gameName;
    private String tagLine;
    private ArrayList<String> LastMatches = new ArrayList<>();
    private AccountData accdata;
    private ArrayList<RanksData> ranks = new ArrayList<>();

    public File IconToFile(){
        System.out.println(this.getAccdata().getProfileIconId());
        return new File("C:\\Users\\Łukasz\\Downloads\\dragontail-14.11.1\\14.11.1\\img\\profileicon\\"+this.getAccdata().getProfileIconId()+".png");
    }
    public File RankToFile(String ranga){
        String path = "C:\\Users\\Łukasz\\Downloads\\InFinity54 LoL_DDragon master extras-tier\\"+ranga.toLowerCase()+".png";
        return new File(path);
    }
    //Gettery i Settery
    public void setAccdata(AccountData data) {
        this.accdata = data;
    }
    public void setRanks(ArrayList<RanksData> data){
        this.ranks = data;
    }
    public AccountData getAccdata() {
        return this.accdata;
    }
    public ArrayList<RanksData> getRankData() {
        return this.ranks;
    }
    public void setPuuid(String puuid) {
        this.puuid = puuid;
    }
    public String getPuuid() {
        return puuid;
    }
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    public String getGameName() {
        return gameName;
    }
    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }
    public String getTagLine() {
        return tagLine;
    }
    public void setLastMatches(ArrayList<String> lastMatches) {
        LastMatches = lastMatches;
    }

    public void addLastMatches(ArrayList<String> lastMatches){
        LastMatches.addAll(lastMatches);
    }
    public ArrayList<String> getLastMatches() {
        return LastMatches;
    }

    @Override
    public String toString() {
        return "RiotAccount: \n" +
                "puuid='" + puuid + '\n' +
                ", gameName='" + gameName + '\n' +
                ", tagLine='" + tagLine + '\n';
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
class AccountData {
    private String id;
    private String accountId;
    private int profileIconId;
    private int summonerLevel;

    // Gettery i Settery
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAccountId() {
        return accountId;
    }
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    public int getProfileIconId() {
        return profileIconId;
    }
    public void setProfileIconId(int profileIconId) {
        this.profileIconId = profileIconId;
    }
    public int getSummonerLevel() {
        return summonerLevel;
    }
    public void setSummonerLevel(int summonerLevel) {
        this.summonerLevel = summonerLevel;
    }
}
@JsonIgnoreProperties(ignoreUnknown = true)
class RanksData {
    private String queueType;
    private String tier;
    private String rank;
    private int leaguePoints;
    private int wins;
    private int losses;

    public String GetqueueName(){
        if (queueType.equals("RANKED_SOLO_5x5")) {
            return "soloQ";
        }
        else if (queueType.equals("RANKED_FLEX_SR")) {
            return "Flex";
        }
        else
        return "Rank";
    }

    public void setQueueType(String queueType){
        this.queueType = queueType;
    }
    public String getQueueType(){
        return queueType;
    }
    public String getRank(){
        return rank;
    }
    public void setRank(String rank){
        this.rank = rank;
    }
    public String getTier(){
        return tier;
    }
    public void setTier(String tier){
        this.tier = tier;
    }
    public int getLeaguePoints(){
        return leaguePoints;
    }
    public void setLeaguePoints(int leaguePoints){
        this.leaguePoints = leaguePoints;
    }
    public int getWins() {
        return wins;
    }
    public void setWins(int wins) {
        this.wins = wins;
    }
    public int getLosses() {
        return losses;
    }
    public void setLosses(int losses) {
        this.losses = losses;
    }
}
