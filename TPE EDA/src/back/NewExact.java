package back;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

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
	
	private EdgeSet minPath = null;

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
				this.minPath =aux;
			}

		}

		System.out.println(min.getTotalWeight());
		System.out.println("Tiempo total  "
				+ ((double) (System.currentTimeMillis() - time) / 1000)
				+ " segundos");
		System.out.println("Cantidad de combinaciones: " + count);
	}

	public HashMap<HashSet<HyperEdge>, EdgeSet> visited = new HashMap<HashSet<HyperEdge>, EdgeSet>();
	public HashMap<Integer, EdgeSet> visited2 = new HashMap<Integer, EdgeSet>();

	long count = 0;

	public void exal(EdgeSet edgesSet)
	{
		System.out.println(edgesSet);

//		if (edgesSet.isTop())
//		{
//			return;
//		}
		
//		if(minPath != null && edgesSet.getTotalWeight() > minPath.getTotalWeight()){
//			return;
//		}

		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();

		HashSet<Node> nodes = new HashSet<Node>();
		
		for (HyperEdge e : edgesSet)
		{
			if (!topEdge(e))
				nodes.addAll(e.tail);

		}
		
		if (nodes.size() == 0)
			return;

		HashSet<HyperEdge> added = new HashSet<HyperEdge>();
		HyperEdge auxEdge;

		for (Node node : nodes)
		{
			auxEdge = node.tail.get(0);
			if (node.tail.size() == 1 && !added.contains(auxEdge))
			{
				added.add(auxEdge);
				auxEdge.setChildrenVisited();
			}
		}

		// boolean toAdd;

		for (Node node : nodes)
		{
			// toAdd = true;
			// for(HyperEdge edge: node.tail){
			// if(added.contains(edge)){
			// toAdd = false;
			// break;
			// }
			// }
			// if(toAdd){
			// parents.add(node.tail);
			// }

			if (!node.visited)
			{
				parents.add(node.tail);
				node.visited = false;
			}

		}

		HyperEdge[] aux = new HyperEdge[parents.size()];

		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations, added);
		// edgesCombinations(nodes, currentPos, combinations);
		count += combinations.size();

		EdgeSet min = null;

		for (EdgeSet combination : combinations)
		{
			Integer hash = combination.hashCode();
			if (!visited2.containsKey(hash))
			{
				visited2.put(hash, combination);

				exal(combination);
			} else
			{
				combination = visited2.get(hash);
			}

			if (min == null
					|| (combination.getTotalWeight() < min.getTotalWeight()))
			{
				min = combination;
			}
		}
		edgesSet.setParent(min);

	}

	public void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HyperEdge[] combination, HashSet<EdgeSet> combinations,
			HashSet<HyperEdge> base)
	{
		if (index == parents.size())
		{

			EdgeSet aux = new EdgeSet(combination);
			aux.addBase(base);

			combinations.add(aux);

			return;
		}

		ArrayList<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{
			combination[index] = edge;
			parentCombinations(parents, index + 1, combination, combinations,
					base);
		}
	}

	public void aprox()
	{
		HyperEdge min = null;
		for (HyperEdge edge : end.tail)
		{
			if (min == null || (edge.weight < min.weight))
			{
				min = edge;
			}

		}

		EdgeSet startingEdge = new EdgeSet(min);
		aprox(startingEdge);

		System.out.println(startingEdge.getTotalWeight());

	}
	
	private boolean topEdge(HyperEdge edge) //Sacar, y poner flag en la clase HyperEdge
	{
		if (edge.tail.size() == 1)
			if (edge.tail.get(0) == start)
				return true;
		
		return false;
	}

	public void aprox(EdgeSet edgesSet)
	{
		{

			if (edgesSet == null || edgesSet.isTop())
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
			minCombination = null;
			minCombination(parents, 0, aux);
			EdgeSet min = minCombination;
			aprox(min);
			edgesSet.setParent(min);

		}
	}

	EdgeSet minCombination;

	public void minCombination(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HyperEdge[] combination)
	{
		if (index == parents.size())
		{
			EdgeSet aux = new EdgeSet(combination);

			if (minCombination == null
					|| (aux.getTotalWeight() < minCombination.getTotalWeight()))
			{
				minCombination = aux;

			}

			return;
		}

		ArrayList<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{
			combination[index] = edge;
			minCombination(parents, index + 1, combination);
		}
	}

	public void edgesCombinations(HashSet<Node> nodes, int[] currentPos,
			HashSet<EdgeSet> combinations)

	{

		int i = 0;
		HyperEdge edge;
		boolean isSatisfied;
		EdgeSet comb;

		comb = new EdgeSet();

		for (Node node : nodes)
		{
			if (currentPos[i] > node.tail.size() - 1) return;
			edge = node.tail.get(currentPos[i]);

			isSatisfied = false;
			for (HyperEdge aux : node.tail)
			{
				if (comb.contains(aux))
				{
					isSatisfied = true;
					break;
				}
			}

			if (!isSatisfied)
			{
				comb.add(edge);

				if (node.tail.size() != 1)
				{

					currentPos[i] = currentPos[i] + 1;
					edgesCombinations(nodes, currentPos, combinations);
					currentPos[i] = currentPos[i] - 1;

				}
			}

			i++;
		}
		combinations.add(comb);

	}

	public void satisfy(HyperEdge edge)
	{
		for (Node node : edge.head)
		{
			node.visited = true;
		}
	}

	public void unSatisfy(HyperEdge edge)
	{
		for (Node node : edge.head)
		{
			node.visited = false;
		}
	}

	public void markPath(EdgeSet edges)
	{

		EdgeSet current = edges;
		EdgeSet next = edges.getParent();
		while (next != null)
		{
			for (HyperEdge edge : current)
			{
				edge.visited = true;
				for (Node node : edge.tail)
				{
					if (!node.visited)
					{
						node.visited = true;
					}
				}
			}
			current = next;
			next = next.getParent();
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException
	{
		HyperGraph g = GraphLoader.loadGraph("Z.hg");

		NewExact a = new NewExact(g);
		// a.aprox();
		a.exal();
		
		//GraphSaver.toDOT(g);

		// Node start = new Node("Start");
		// Node end = new Node("End");
		//
		// Node t = new Node("T");
		// Node s = new Node("S");
		// Node u = new Node("U");
		// Node w = new Node("W");
		// Node test = new Node("Test");
		// Node v = new Node("V");
		//
		//
		// HyperEdge p = new HyperEdge("P", 8.0);
		// HyperEdge q = new HyperEdge("Q", 6.0);
		// HyperEdge r = new HyperEdge("R", 9.0);
		// HyperEdge k = new HyperEdge("K", 10.0);
		// HyperEdge alfa = new HyperEdge("alfa", 10.0);
		//
		//
		// t.tail.add(p);
		// s.tail.add(alfa);
		// u.tail.add(p);
		// test.tail.add(p);
		// w.tail.add(q);
		// w.tail.add(r);
		// w.tail.add(k);
		// v.tail.add(p);
		//
		// NewExact g = new NewExact(start, end);
		//
		// HashSet<Node> set = new HashSet<Node>();
		// set.add(t);
		// set.add(u);
		// set.add(s);
		// set.add(w);
		// set.add(test);
		// set.add(v);
		//
		//
		// for(Node node: set){
		// System.out.println(node);
		// }
		//
		// HashSet<EdgeSet> comb = new HashSet<EdgeSet>();
		// int[] currentPos = new int[set.size()];
		//
		// for (int i = 0; i < currentPos.length; i++)
		// {
		// currentPos[i] = 0;
		// }
		//
		// g.edgesCombinations(set, currentPos, comb);
		//
		// for (EdgeSet e : comb)
		// {
		// System.out.println(e);
		// }

	}
}
