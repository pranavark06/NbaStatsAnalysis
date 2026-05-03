package G16.menik._0.models;

public class PlayerGameStats {
    private String gameId;
    private String teamId;
    private String teamAbbreviation;
    private String teamCity;
    private String playerId;
    private String playerName;
    private String nickname;
    private String startPosition;
    private String comment;
    private String min;
    private String fgm;
    private String fga;
    private String fgPct;
    private String fg3m;
    private String fg3a;
    private String fg3Pct;
    private String ftm;
    private String fta;
    private String ftPct;
    private String oreb;
    private String dreb;
    private String reb;
    private String ast;
    private String stl;
    private String blk;
    private String tto;
    private String pf;
    private String pts;
    private String plusMinus;

    // Additional player info
    private String jersey;
    private String position;
    private String height;
    private String weight;
    private String draftYear;
    private String draftRound;
    private String draftNumber;
    private String gameDate;
    private String seasonAvgPts;
    private String seasonAvgReb;
    private String seasonAvgAst;
    private int gamesPlayed;
    private double gameScore;
    private int teamPtsRank;

    // Getters and Setters
    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public String getTeamAbbreviation() { return teamAbbreviation; }
    public void setTeamAbbreviation(String teamAbbreviation) { this.teamAbbreviation = teamAbbreviation; }
    public String getTeamCity() { return teamCity; }
    public void setTeamCity(String teamCity) { this.teamCity = teamCity; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getStartPosition() { return startPosition; }
    public void setStartPosition(String startPosition) { this.startPosition = startPosition; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getMin() { return min; }
    public void setMin(String min) { this.min = min; }
    public String getFgm() { return fgm; }
    public void setFgm(String fgm) { this.fgm = fgm; }
    public String getFga() { return fga; }
    public void setFga(String fga) { this.fga = fga; }
    public String getFgPct() { return fgPct; }
    public void setFgPct(String fgPct) { this.fgPct = fgPct; }
    public String getFg3m() { return fg3m; }
    public void setFg3m(String fg3m) { this.fg3m = fg3m; }
    public String getFg3a() { return fg3a; }
    public void setFg3a(String fg3a) { this.fg3a = fg3a; }
    public String getFg3Pct() { return fg3Pct; }
    public void setFg3Pct(String fg3Pct) { this.fg3Pct = fg3Pct; }
    public String getFtm() { return ftm; }
    public void setFtm(String ftm) { this.ftm = ftm; }
    public String getFta() { return fta; }
    public void setFta(String fta) { this.fta = fta; }
    public String getFtPct() { return ftPct; }
    public void setFtPct(String ftPct) { this.ftPct = ftPct; }
    public String getOreb() { return oreb; }
    public void setOreb(String oreb) { this.oreb = oreb; }
    public String getDreb() { return dreb; }
    public void setDreb(String dreb) { this.dreb = dreb; }
    public String getReb() { return reb; }
    public void setReb(String reb) { this.reb = reb; }
    public String getAst() { return ast; }
    public void setAst(String ast) { this.ast = ast; }
    public String getStl() { return stl; }
    public void setStl(String stl) { this.stl = stl; }
    public String getBlk() { return blk; }
    public void setBlk(String blk) { this.blk = blk; }
    public String getTto() { return tto; }
    public void setTto(String tto) { this.tto = tto; }
    public String getPf() { return pf; }
    public void setPf(String pf) { this.pf = pf; }
    public String getPts() { return pts; }
    public void setPts(String pts) { this.pts = pts; }
    public String getPlusMinus() { return plusMinus; }
    public void setPlusMinus(String plusMinus) { this.plusMinus = plusMinus; }

    public String getJersey() { return jersey; }
    public void setJersey(String jersey) { this.jersey = jersey; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }
    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }
    public String getDraftYear() { return draftYear; }
    public void setDraftYear(String draftYear) { this.draftYear = draftYear; }
    public String getDraftRound() { return draftRound; }
    public void setDraftRound(String draftRound) { this.draftRound = draftRound; }
    public String getDraftNumber() { return draftNumber; }
    public void setDraftNumber(String draftNumber) { this.draftNumber = draftNumber; }
    public String getGameDate() { return gameDate; }
    public void setGameDate(String gameDate) { this.gameDate = gameDate; }
    public String getSeasonAvgPts() { return seasonAvgPts; }
    public void setSeasonAvgPts(String seasonAvgPts) { this.seasonAvgPts = seasonAvgPts; }
    public String getSeasonAvgReb() { return seasonAvgReb; }
    public void setSeasonAvgReb(String seasonAvgReb) { this.seasonAvgReb = seasonAvgReb; }
    public String getSeasonAvgAst() { return seasonAvgAst; }
    public void setSeasonAvgAst(String seasonAvgAst) { this.seasonAvgAst = seasonAvgAst; }
    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public double getGameScore() { return gameScore; }
    public void setGameScore(double gameScore) { this.gameScore = gameScore; }
    public int getTeamPtsRank() { return teamPtsRank; }
    public void setTeamPtsRank(int teamPtsRank) { this.teamPtsRank = teamPtsRank; }
}
