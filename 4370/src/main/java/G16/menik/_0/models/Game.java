package G16.menik._0.models;

import java.sql.Timestamp;

/**\n * Represents an NBA game with comprehensive statistics for both home and away teams.\n * Contains game identification, timing information, and detailed performance metrics\n * including field goals, free throws, rebounds, assists, and scoring data.\n */\npublic class Game {
    private String seasonId;
    private String teamIdHome;
    private String teamAbbreviationHome;
    private String teamNameHome;
    private String gameId;
    private Timestamp gameDate;
    private String matchupHome;
    private String wlHome;
    private int min;
    private double fgmHome;
    private double fgaHome;
    private double fgPctHome;
    private double fg3mHome;
    private double fg3aHome;
    private double fg3PctHome;
    private double ftmHome;
    private double ftaHome;
    private double ftPctHome;
    private double orebHome;
    private double drebHome;
    private double rebHome;
    private double astHome;
    private double stlHome;
    private double blkHome;
    private double tovHome;
    private double pfHome;
    private double ptsHome;
    private int plusMinusHome;
    private int videoAvailableHome;
    private String teamIdAway;
    private String teamAbbreviationAway;
    private String teamNameAway;
    private String matchupAway;
    private String wlAway;
    private double fgmAway;
    private double fgaAway;
    private double fgPctAway;
    private double fg3mAway;
    private double fg3aAway;
    private double fg3PctAway;
    private double ftmAway;
    private double ftaAway;
    private double ftPctAway;
    private double orebAway;
    private double drebAway;
    private double rebAway;
    private double astAway;
    private double stlAway;
    private double blkAway;
    private double tovAway;
    private double pfAway;
    private double ptsAway;
    private int plusMinusAway;
    private int videoAvailableAway;
    private String seasonType;

    // Getters and Setters for getting and setting the values of the fields
    public String getSeasonId() { return seasonId; }
    public void setSeasonId(String seasonId) { this.seasonId = seasonId; }
    public String getTeamIdHome() { return teamIdHome; }
    public void setTeamIdHome(String teamIdHome) { this.teamIdHome = teamIdHome; }
    public String getTeamAbbreviationHome() { return teamAbbreviationHome; }
    public void setTeamAbbreviationHome(String teamAbbreviationHome) { this.teamAbbreviationHome = teamAbbreviationHome; }
    public String getTeamNameHome() { return teamNameHome; }
    public void setTeamNameHome(String teamNameHome) { this.teamNameHome = teamNameHome; }
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public Timestamp getGameDate() { return gameDate; }
    public void setGameDate(Timestamp gameDate) { this.gameDate = gameDate; }
    public String getMatchupHome() { return matchupHome; }
    public void setMatchupHome(String matchupHome) { this.matchupHome = matchupHome; }
    public String getWlHome() { return wlHome; }
    public void setWlHome(String wlHome) { this.wlHome = wlHome; }
    public int getMin() { return min; }
    public void setMin(int min) { this.min = min; }
    public double getFgmHome() { return fgmHome; }
    public void setFgmHome(double fgmHome) { this.fgmHome = fgmHome; }
    public double getFgaHome() { return fgaHome; }
    public void setFgaHome(double fgaHome) { this.fgaHome = fgaHome; }
    public double getFgPctHome() { return fgPctHome; }
    public void setFgPctHome(double fgPctHome) { this.fgPctHome = fgPctHome; }
    public double getFg3mHome() { return fg3mHome; }
    public void setFg3mHome(double fg3mHome) { this.fg3mHome = fg3mHome; }
    public double getFg3aHome() { return fg3aHome; }
    public void setFg3aHome(double fg3aHome) { this.fg3aHome = fg3aHome; }
    public double getFg3PctHome() { return fg3PctHome; }
    public void setFg3PctHome(double fg3PctHome) { this.fg3PctHome = fg3PctHome; }
    public double getFtmHome() { return ftmHome; }
    public void setFtmHome(double ftmHome) { this.ftmHome = ftmHome; }
    public double getFtaHome() { return ftaHome; }
    public void setFtaHome(double ftaHome) { this.ftaHome = ftaHome; }
    public double getFtPctHome() { return ftPctHome; }
    public void setFtPctHome(double ftPctHome) { this.ftPctHome = ftPctHome; }
    public double getOrebHome() { return orebHome; }
    public void setOrebHome(double orebHome) { this.orebHome = orebHome; }
    public double getDrebHome() { return drebHome; }
    public void setDrebHome(double drebHome) { this.drebHome = drebHome; }
    public double getRebHome() { return rebHome; }
    public void setRebHome(double rebHome) { this.rebHome = rebHome; }
    public double getAstHome() { return astHome; }
    public void setAstHome(double astHome) { this.astHome = astHome; }
    public double getStlHome() { return stlHome; }
    public void setStlHome(double stlHome) { this.stlHome = stlHome; }
    public double getBlkHome() { return blkHome; }
    public void setBlkHome(double blkHome) { this.blkHome = blkHome; }
    public double getTovHome() { return tovHome; }
    public void setTovHome(double tovHome) { this.tovHome = tovHome; }
    public double getPfHome() { return pfHome; }
    public void setPfHome(double pfHome) { this.pfHome = pfHome; }
    public double getPtsHome() { return ptsHome; }
    public void setPtsHome(double ptsHome) { this.ptsHome = ptsHome; }
    public int getPlusMinusHome() { return plusMinusHome; }
    public void setPlusMinusHome(int plusMinusHome) { this.plusMinusHome = plusMinusHome; }
    public int getVideoAvailableHome() { return videoAvailableHome; }
    public void setVideoAvailableHome(int videoAvailableHome) { this.videoAvailableHome = videoAvailableHome; }
    public String getTeamIdAway() { return teamIdAway; }
    public void setTeamIdAway(String teamIdAway) { this.teamIdAway = teamIdAway; }
    public String getTeamAbbreviationAway() { return teamAbbreviationAway; }
    public void setTeamAbbreviationAway(String teamAbbreviationAway) { this.teamAbbreviationAway = teamAbbreviationAway; }
    public String getTeamNameAway() { return teamNameAway; }
    public void setTeamNameAway(String teamNameAway) { this.teamNameAway = teamNameAway; }
    public String getMatchupAway() { return matchupAway; }
    public void setMatchupAway(String matchupAway) { this.matchupAway = matchupAway; }
    public String getWlAway() { return wlAway; }
    public void setWlAway(String wlAway) { this.wlAway = wlAway; }
    public double getFgmAway() { return fgmAway; }
    public void setFgmAway(double fgmAway) { this.fgmAway = fgmAway; }
    public double getFgaAway() { return fgaAway; }
    public void setFgaAway(double fgaAway) { this.fgaAway = fgaAway; }
    public double getFgPctAway() { return fgPctAway; }
    public void setFgPctAway(double fgPctAway) { this.fgPctAway = fgPctAway; }
    public double getFg3mAway() { return fg3mAway; }
    public void setFg3mAway(double fg3mAway) { this.fg3mAway = fg3mAway; }
    public double getFg3aAway() { return fg3aAway; }
    public void setFg3aAway(double fg3aAway) { this.fg3aAway = fg3aAway; }
    public double getFg3PctAway() { return fg3PctAway; }
    public void setFg3PctAway(double fg3PctAway) { this.fg3PctAway = fg3PctAway; }
    public double getFtmAway() { return ftmAway; }
    public void setFtmAway(double ftmAway) { this.ftmAway = ftmAway; }
    public double getFtaAway() { return ftaAway; }
    public void setFtaAway(double ftaAway) { this.ftaAway = ftaAway; }
    public double getFtPctAway() { return ftPctAway; }
    public void setFtPctAway(double ftPctAway) { this.ftPctAway = ftPctAway; }
    public double getOrebAway() { return orebAway; }
    public void setOrebAway(double orebAway) { this.orebAway = orebAway; }
    public double getDrebAway() { return drebAway; }
    public void setDrebAway(double drebAway) { this.drebAway = drebAway; }
    public double getRebAway() { return rebAway; }
    public void setRebAway(double rebAway) { this.rebAway = rebAway; }
    public double getAstAway() { return astAway; }
    public void setAstAway(double astAway) { this.astAway = astAway; }
    public double getStlAway() { return stlAway; }
    public void setStlAway(double stlAway) { this.stlAway = stlAway; }
    public double getBlkAway() { return blkAway; }
    public void setBlkAway(double blkAway) { this.blkAway = blkAway; }
    public double getTovAway() { return tovAway; }
    public void setTovAway(double tovAway) { this.tovAway = tovAway; }
    public double getPfAway() { return pfAway; }
    public void setPfAway(double pfAway) { this.pfAway = pfAway; }
    public double getPtsAway() { return ptsAway; }
    public void setPtsAway(double ptsAway) { this.ptsAway = ptsAway; }
    public int getPlusMinusAway() { return plusMinusAway; }
    public void setPlusMinusAway(int plusMinusAway) { this.plusMinusAway = plusMinusAway; }
    public int getVideoAvailableAway() { return videoAvailableAway; }
    public void setVideoAvailableAway(int videoAvailableAway) { this.videoAvailableAway = videoAvailableAway; }
    public String getSeasonType() { return seasonType; }
    public void setSeasonType(String seasonType) { this.seasonType = seasonType; }
}