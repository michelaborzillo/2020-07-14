package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	PremierLeagueDAO dao;
	Graph<Team,  DefaultWeightedEdge> grafo;
	Map<Integer, Team> idMap;
	
	public Model() {
		dao= new PremierLeagueDAO();
		idMap= new HashMap<Integer, Team>();
		
	}
	
	public void creaGrafo() {
		this.grafo= new SimpleWeightedGraph<> (DefaultWeightedEdge.class);
		List<Team> team= dao.getTeams(idMap);
		Graphs.addAllVertices(this.grafo, idMap.values());
		for (CoppiaSquadre cs: dao.getCoppie(idMap)) {
			  if (this.totPuntiSquadra(cs.getT1(), cs.getT2())!=0)
			Graphs.addEdgeWithVertices(this.grafo, cs.getT1(), cs.getT2(), this.totPuntiSquadra(cs.getT1(),cs.getT2()));
			
		}
		
	}

	public int nVertici() {
		return this.grafo.vertexSet().size();
		
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
		
	}
	
	public int tot(Team t) {
		return this.dao.vittorie(t)+this.dao.pareggi(t);
	}
	public int totPuntiSquadra(Team t1, Team t2) {
		int tot1=dao.pareggi(t1)+dao.vittorie(t1)+dao.vittorieFuoriCasa(t1);
		int tot2= dao.pareggi(t2)+dao.vittorie(t2)+dao.vittorieFuoriCasa(t2);
		int tot= tot1-tot2;
		
		return Math.abs(tot);
	}
	
	
	
	public List<Team> getSquadre() {
		return this.dao.getTeams(idMap);
	}
	
	
	public List<Team> getPeggiori(Team t) {
		List<Team> riempi= new ArrayList<> ();
		for (Team i: this.getSquadre()) {
			if (this.tot(t)>this.tot(i))
				riempi.add(i);
		}
		Collections.sort(riempi);
		return riempi;
	}
	
	
	public List<Team> getMigliori(Team t) {
		List<Team> riempi= new ArrayList<> ();
		for (Team i: this.getSquadre()) {
			if (this.tot(t)<this.tot(i))
				riempi.add(i);
		}
		Collections.sort(riempi);
		return riempi;	
	}
}
