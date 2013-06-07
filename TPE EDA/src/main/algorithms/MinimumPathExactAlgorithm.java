package main.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import main.EdgeSet;
import main.HyperGraph;
import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

public class MinimumPathExactAlgorithm
{
	private static HyperGraph graph;
	
	private static HashMap<Integer, EdgeSet> visited = new HashMap<Integer, EdgeSet>();

	
	public static double execute(HyperGraph hyperGraph)
	{
		graph = hyperGraph;
		
		EdgeSet min = null;

		for (HyperEdge edge : graph.end.tail)
		{
			EdgeSet aux = new EdgeSet(edge);

			minimumPathExact(aux);

			if (min == null || aux.getTotalWeight() < min.getTotalWeight())
				min = aux;

		}

		markPath(min);
		
		return min.getTotalWeight();
		
	}


	private static void minimumPathExact(EdgeSet current)
	{

		HashSet<Node> nodes = getParentNodes(current);

		if (nodes.size() == 0) // Caso base
			return;

		HashSet<EdgeSet> combinations = generateCombinations(nodes);

		EdgeSet min = null;

		// aux = null; //Dejar esto?
		// nodes = null;
		// parents = null;
		// base = null;

		for (EdgeSet sample : combinations)
		{
			Integer hash = sample.hashCode();

			if (!visited.containsKey(hash))
			{
				visited.put(hash, sample);

				minimumPathExact(sample);
			} else
			{
				sample = visited.get(hash);
			}

			if (min == null || (sample.getTotalWeight() < min.getTotalWeight()))
			{
				min = sample;
			}
		}

		current.setParent(min);
	}

	private static ArrayList<ArrayList<HyperEdge>> generateParents(
			HashSet<Node> nodes, HashSet<HyperEdge> base)
	{
		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();

		for (Node node : nodes)
		{
			if (node.tail.size() == 1)
			{
				HyperEdge auxEdge = node.tail.get(0);

				if (!base.contains(auxEdge))
				{
					base.add(auxEdge);
					auxEdge.setChildrenVisited();
				}
			}
		}

		int i = 0;
		for (Node node : nodes)
			if (!node.visited)
			{
				parents.add(node.tail);
				node.visited = false;

			}

		return parents;
	}

	private static HashSet<Node> getParentNodes(EdgeSet set)
	{
		HashSet<Node> nodes = new HashSet<Node>();

		for (HyperEdge edge : set)
		{
			if (!edge.isTop)
			{
				nodes.addAll(edge.tail);
			}
		}
		return nodes;
	}

	private static HashSet<EdgeSet> generateCombinations(HashSet<Node> nodes)
	{

		HashSet<HyperEdge> base = new HashSet<HyperEdge>();

		Node[] nodesArray = new Node[nodes.size()];

		ArrayList<ArrayList<HyperEdge>> parents = generateParents(nodes, base);

		HyperEdge[] aux = new HyperEdge[parents.size()];

		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations, base);

		return combinations;
	}

	private static void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
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

	private static void markPath(EdgeSet set)
	{
		if (set == null)
		{
			return;
		}
		
		for (HyperEdge edge : set)
		{
			edge.visited = true;
			for(Node node: edge.tail){
				node.visited = true;
			}
		}
		
		markPath(set.getParent());

	}
}
