package back;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
		for(HyperEdge edge: end.tail){
			EdgeSet aux = new EdgeSet(edge);
			exal(aux);
			if (min == null
					|| (aux.getTotalWeight() < min.getTotalWeight()))
			{
				min = aux;
			}
			
		}
		
		System.out.println(min.getTotalWeight());
		System.out.println("Tiempo total  " + (System.currentTimeMillis() - time));
	}


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
			exal(combination);
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

	public void edgesCombinations(ArrayList<Node> nodes, int index,
			ArrayList<HashSet<HyperEdge>> combinations, Iterator<Node> it,
			HashSet<HyperEdge> aux)
	{

		if (!it.hasNext())
		{
			return;
		}

		while (it.hasNext())
		{
			Node node = it.next();
			boolean isSatisfied = false;
			for (HyperEdge parent : node.tail)
			{
				if (aux.contains(parent))
				{
					isSatisfied = true;
				}
			}
			if (!isSatisfied)
			{
				for (HyperEdge e : node.tail)
				{
					aux.add(e);
					combinations.add(aux);
					edgesCombinations(nodes, index, combinations, it, aux);
					HashSet<HyperEdge> set = new HashSet<HyperEdge>(aux);

				}
			}

		}

	}

	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("B.hg");
		
		NewExact a = new NewExact(g);
		a.exal();
	}
}
