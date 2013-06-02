package back;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class NewExact
{
	public String name;

	private Node start;
	private Node end;

	private double minDistance;

	List<HyperEdge> hEdges;
	List<Node> nodes;

	public NewExact(Node start, Node end)
	{
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}

	public NewExact(HyperGraph graph)
	{
		this.start = graph.getStart();
		this.end = graph.getEnd();
		this.name = graph.name;
		hEdges = graph.hEdges;
		nodes = graph.nodes;
	}

	public Node getStart()
	{
		return start;
	}

	public Node getEnd()
	{
		return end;
	}

	public void exal()
	{
		System.out.println("Comenzando EXAL");
		long time = System.currentTimeMillis();
		EdgeSet min = null;
		for (HyperEdge edge : end.tail)
		{
			EdgeSet aux = new EdgeSet(edge);
			exal(aux);
			if (min == null || (aux.getTotalWeight() < min.getTotalWeight()))
			{
				min = aux;
			}

		}

		System.out.println(min.getTotalWeight());
		System.out.println("Tiempo total  "
				+ (System.currentTimeMillis() - time));
	}

	public HashMap<HashSet<HyperEdge>, EdgeSet> visited = new HashMap<HashSet<HyperEdge>, EdgeSet>();
	
	public void exal(EdgeSet edgesSet)
	{

		if (edgesSet.isTop())
		{
			return;
		}

		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();

		HashSet<Node> nodes = new HashSet<Node>();
		for (HyperEdge e : edgesSet)
		{
			nodes.addAll(e.tail);
		}

		for (Node node : nodes)
		{
			parents.add(node.tail);
		}

		HyperEdge[] aux = new HyperEdge[nodes.size()];
		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations);

		EdgeSet min = null;

		for (EdgeSet combination : combinations)
		{
			if (!visited.containsKey(combination.edges))
			{
				visited.put(combination.edges, combination);
				exal(combination);
			}
			else
				combination = visited.get(combination.edges);
			
			
			if (min == null
					|| (combination.getTotalWeight() < min.getTotalWeight()))
			{
				min = combination;
			}
		}

		edgesSet.setParent(min);

	}

	public void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HyperEdge[] combination, HashSet<EdgeSet> combinations)
	{
		if (index == parents.size())
		{
			EdgeSet aux = new EdgeSet(combination);

			combinations.add(aux);

			return;
		}

		ArrayList<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{
			combination[index] = edge;
			parentCombinations(parents, index + 1, combination, combinations);
		}
	}

	public void edgesCombinations(HashSet<Node> nodes, Iterator<Node> it,
			ArrayList<EdgeSet> combinations, int index)

	{
		
		

		while (it.hasNext())
		{
			Node node = it.next();
			EdgeSet currentComb;
			if(combinations.size() == 0){
				currentComb = new EdgeSet();
				combinations.add(currentComb);
				
			}else{
				currentComb = combinations.get(index);
			}
			


			boolean isSatisfied = false;
			for (HyperEdge e : node.tail)
			{
				if (currentComb.contains(e))
				{
					isSatisfied = true;
				}
			}
			if (!isSatisfied)
			{
				for (HyperEdge edge : node.tail)
				{
					if(node.tail.size() == 1){
						currentComb.add(edge);
					}
					EdgeSet aux = new EdgeSet(currentComb);
					currentComb.add(edge);
					combinations.add(aux);
					edgesCombinations(nodes, it, combinations, index + 1);
				}

			}
		}

	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException
	{
		HyperGraph g = GraphLoader.loadGraph("B.hg");

		NewExact a = new NewExact(g);
		a.exal();
		
//		Node start = new Node("Start");
//		Node end = new Node("End");
//		
//		Node t = new Node("T");
//		Node s = new Node("S");
//		Node u = new Node("U");
//		Node w = new Node("W");
//		
//		HyperEdge p = new HyperEdge("P", 8.0);
//		HyperEdge q = new HyperEdge("Q", 6.0);
//		HyperEdge r = new HyperEdge("R", 9.0);
//		
//		t.tail.add(p);
//		s.tail.add(p);
//		u.tail.add(p);
//		
//		w.tail.add(q);
//		w.tail.add(r);
//		
//		NewExact g = new NewExact(start, end);
//		
//		HashSet<Node> set = new HashSet<Node>();
//		set.add(t);
//		set.add(u);
//		set.add(s);
//		set.add(w);
//		
//		
//		ArrayList<EdgeSet> comb = new ArrayList<EdgeSet>();
//		
//		g.edgesCombinations(set, set.iterator(), comb, 0);
//		
//		for(EdgeSet e: comb){
//			System.out.println(e);
//		}
		

		

		
		
		
	}
}
