package back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class HyperGraph
{

	public String name;

	private Node start;
	private Node end;

	private double minDistance;

	List<HyperEdge> hEdges;
	List<Node> nodes;

	public HyperGraph(Node start, Node end)
	{
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}

	protected static class Node
	{

		public String name;

		public ArrayList<HyperEdge> head;
		public ArrayList<HyperEdge> tail;

		public boolean visited;

		public Node(String name)
		{

			this.name = name;
			tail = new ArrayList<HyperEdge>();
			head = new ArrayList<HyperEdge>();

		}

		@Override
		public String toString()
		{

			// TODO Auto-generated method stub
			return name;
		}
	}

	protected static class HyperEdge
	{

		public String name;

		public List<Node> tail = new ArrayList<Node>();
		public List<Node> head = new ArrayList<Node>();

		public boolean visited;

		public final double weight;

		public double distance;

		public int numberOfEntries;

		public HyperEdge(String name, double weight)
		{
			this.name = name;
			this.weight = weight;
			this.numberOfEntries = numberOfEntries;
		}
	}

	public void exactAlgorithm()
	{

		HyperEdge aux = new HyperEdge(null, 0);
		aux.tail.add(end);

		exactAlgorithm(aux);

	}

	public void exactAlgorithm(HyperEdge edge)
	{
		if (edge.tail.size() == 1)
		{
			Node aux = edge.tail.get(0);
			if (aux.tail.isEmpty())
			{
				edge.distance = edge.weight;
				return;
			}

		}

		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();
		float total = 0;

		HashSet<HyperEdge> calculated = new HashSet<HyperEdge>();

		for (Node node : edge.tail)
		{
			for (HyperEdge parentEdge : node.tail)
			{
				if (!calculated.contains(parentEdge))
				{
					exactAlgorithm(parentEdge);
					calculated.add(parentEdge);
				}
			}
		}

		HyperEdge[] combination = new HyperEdge[edge.tail.size()];
		HashSet<HyperEdge> result = new HashSet<HyperEdge>();

		for (Node node : edge.tail)
		{
			parents.add(node.tail);
		}

		parentCombinations(parents, 0, calculated, combination, result);

		edge.distance = combinationWeight(result);
	}

	// "To understand recursion, you must first understand recursion" -
	// Morpheus, Matrix: the Prelude
	// "Im in." - Neo, Matrix 4 balls

	private void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HashSet<HyperEdge> calculated, HyperEdge[] combination,
			HashSet<HyperEdge> result)
	{
		if (index == parents.size())
		{
			HashSet<HyperEdge> tempResult = new HashSet<HyperEdge>();

			for (HyperEdge edge : combination)
				tempResult.add(edge);

			if (combinationWeight(tempResult) < combinationWeight(result))
			{
				result.clear();
				result.addAll(tempResult);
			}
		}

		ArrayList<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{
			combination[index] = edge;
			parentCombinations(parents, index + 1, calculated, combination,
					result);
		}
	}

	private float combinationWeight(HashSet<HyperEdge> comb)
	{
		float weight = 0;
		for (HyperEdge e : comb)
			weight += e.distance;
		return weight;
	}

	// Alt algorithm abajo

	public void exactAlgorithmAlt()
	{
		Map<String, HyperEdge> calculated = new HashMap<String, HyperEdge>();

		HyperEdge imag = new HyperEdge("Imaginary", 0);
		imag.tail.add(end);

		exactAlgorithmAlt(imag, calculated);
	}

	private void exactAlgorithmAlt(HyperEdge hEdge,
			Map<String, HyperEdge> calculated)
	{

		for (Node node : hEdge.tail)
		{
			for (HyperEdge edge : node.tail)
			{
				edge.distance = edge.weight + hEdge.distance;
			}
		}

	}
}
