package it.polito.tdp.PremierLeague.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.PremierLeague.model.Action;
import it.polito.tdp.PremierLeague.model.CoppiaSquadre;
import it.polito.tdp.PremierLeague.model.Match;
import it.polito.tdp.PremierLeague.model.Player;
import it.polito.tdp.PremierLeague.model.Team;


public class PremierLeagueDAO {
	
	public List<Player> listAllPlayers(){
		String sql = "SELECT * FROM Players";
		List<Player> result = new ArrayList<Player>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Player player = new Player(res.getInt("PlayerID"), res.getString("Name"));
				
				result.add(player);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Team> listAllTeams(){
		String sql = "SELECT * FROM Teams";
		List<Team> result = new ArrayList<Team>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Team team = new Team(res.getInt("TeamID"), res.getString("Name"));
				result.add(team);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Action> listAllActions(){
		String sql = "SELECT * FROM Actions";
		List<Action> result = new ArrayList<Action>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Action action = new Action(res.getInt("PlayerID"),res.getInt("MatchID"),res.getInt("TeamID"),res.getInt("Starts"),res.getInt("Goals"),
						res.getInt("TimePlayed"),res.getInt("RedCards"),res.getInt("YellowCards"),res.getInt("TotalSuccessfulPassesAll"),res.getInt("totalUnsuccessfulPassesAll"),
						res.getInt("Assists"),res.getInt("TotalFoulsConceded"),res.getInt("Offsides"));
				
				result.add(action);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Match> listAllMatches(){
		String sql = "SELECT m.MatchID, m.TeamHomeID, m.TeamAwayID, m.teamHomeFormation, m.teamAwayFormation, m.resultOfTeamHome, m.date, t1.Name, t2.Name   "
				+ "FROM Matches m, Teams t1, Teams t2 "
				+ "WHERE m.TeamHomeID = t1.TeamID AND m.TeamAwayID = t2.TeamID";
		List<Match> result = new ArrayList<Match>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				
				Match match = new Match(res.getInt("m.MatchID"), res.getInt("m.TeamHomeID"), res.getInt("m.TeamAwayID"), res.getInt("m.teamHomeFormation"), 
							res.getInt("m.teamAwayFormation"),res.getInt("m.resultOfTeamHome"), res.getTimestamp("m.date").toLocalDateTime(), res.getString("t1.Name"),res.getString("t2.Name"));
				
				
				result.add(match);

			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Team> getTeams(Map<Integer, Team>idMap) {
		String sql="SELECT * FROM Teams";
		List<Team> result= new ArrayList<Team>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				Team team= new Team(res.getInt("TeamID"),res.getString("name"));
				idMap.put(team.getTeamID(), team);
				result.add(team);
			}
			conn.close();
			return result;
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
		/*public List<CoppiaSquadre> getCoppie (Map<Integer, Team>idMap) {
		String sql ="SELECT distinct t1.TeamID AS t1, t2.TeamID AS t2 " 
				+ "FROM teams t1, teams t2, actions a1, actions a2 "
				+ "WHERE t1.TeamID=a1.TeamID AND t2.TeamID=a2.TeamID AND a1.PositionID-a2.PositionID!=0";
Connection conn= DBConnect.getConnection();
		List<CoppiaSquadre> result= new ArrayList<CoppiaSquadre>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			
			ResultSet res=st.executeQuery();
			while (res.next()) {
				Team te1= idMap.get(res.getInt("t1"));
				Team te2= idMap.get(res.getInt("t2"));
				idMap.put(te1.getTeamID(), te1);
				idMap.put(te2.getTeamID(), te2);
				int peso= this.peso(te1, te2);
				result.add(new CoppiaSquadre(te1, te2, peso));
			}
			conn.close();
			return result;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}*/
	
	public List<CoppiaSquadre> getCoppie(Map<Integer, Team>idMap) {
		String sql="SELECT m.TeamHomeID AS t1, m.TeamAwayID AS t2 "
				+ "FROM matches m";
		Connection conn= DBConnect.getConnection();
		List<CoppiaSquadre> result= new ArrayList<CoppiaSquadre>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			   
			ResultSet res=st.executeQuery();
			while (res.next()) {
				Team t1= idMap.get(res.getInt("t1"));
				Team t2= idMap.get(res.getInt("t2"));
				idMap.put(t1.getTeamID(), t1);
				idMap.put(t2.getTeamID(), t2);
				if (t1!=null && t2!=null) {
				result.add(new CoppiaSquadre(t1, t2));
		}
			
			}
			conn.close();
			return result;
			
		}catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
		
	
	public int vittorie (Team team) {
		String sql ="SELECT Count(m1.ResultOfTeamHome)*3 AS punti "
				+ "FROM matches m1 " 
				+ "WHERE m1.TeamHomeID=? AND m1.ResultOfTeamHome='1'";
		int vitt=0;
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, team.getTeamID());
			ResultSet res=st.executeQuery();
			if (res.next()) {
				vitt=res.getInt("punti");
			}
			conn.close();
			return vitt;
		}catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int pareggi (Team team) {
		String sql ="SELECT Count(m1.ResultOfTeamHome)*1 AS punti "
				+ "FROM matches m1 " 
				+ "WHERE (m1.TeamHomeID=? OR m1.TeamAwayID=?) AND m1.ResultOfTeamHome='0'";
		int vitt=0;
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, team.getTeamID());
			st.setInt(2, team.getTeamID());
			ResultSet res=st.executeQuery();
			if (res.next()) {
				vitt=res.getInt("punti");
			}
			conn.close();
			return vitt;
		}catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public int vittorieFuoriCasa(Team team) {
		String sql="SELECT COUNT(m1.ResultOfTeamHome)*3 AS punti "
				+ "FROM matches m1 "
				+ "WHERE m1.TeamAwayID=? AND m1.ResultOfTeamHome='-1'";
		int vitt=0;
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, team.getTeamID());
			ResultSet res=st.executeQuery();
			if (res.next()) {
				vitt=res.getInt("punti");
			}
			conn.close();
			return vitt;
		}catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
 	
	
	
	
}
