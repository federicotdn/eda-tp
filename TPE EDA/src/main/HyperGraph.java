package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//Clase HyperGraph limpia, con implementacion de algoritmo exacto:
//Usa la version con HashMap<Integer, EdgeSet>, llega recursivamente hasta arriba
//primero y despues va bajando, sumando pesos

public class HyperGraph
{
	private String name;

	private Node start;
	private Node end;

	private List<HyperEdge> hEdges;
	private List<Node> nodes;

	public HyperGraph(String name, Node start, Node end)
	{
		this.name = name;
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}

	public Node start()
	{
		return start;
	}

	public Node end()
	{
		return end;
	}

	public List<HyperEdge> edges()
	{
		return hEdges;
	}

	public List<Node> nodes()
	{
		return nodes;
	}

	// ----------------Algoritmo Exacto---------------------------------------

	public void minimumPathExact()
	{
		System.out.println("Comenzando b��squeda de camino minimo (exacto) en "
				+ name + " ...");
		long lastTime = System.currentTimeMillis();

		EdgeSet min = null;

		for (HyperEdge edge : end.tail)
		{
			EdgeSet aux = new EdgeSet(edge);

			minimumPathExact(aux);

			if (min == null || aux.getTotalWeight() < min.getTotalWeight())
				min = aux;

		}

		System.out.println("Camino minimo pesa: " + min.getTotalWeight());

		System.out.println("Tard��: "
				+ ((double) System.currentTimeMillis() - lastTime) / 1000
				+ " segundos.");
	}

	public HashMap<Integer, EdgeSet> visited = new HashMap<Integer, EdgeSet>();

	private void minimumPathExact(EdgeSet current)
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

	private HashSet<EdgeSet> generateCombinations(HashSet<Node> nodes)
	{
		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();

		HashSet<HyperEdge> base = new HashSet<HyperEdge>();


		for (Node node : nodes)
		{
			if (node.tail.size() == 1)
			{
				HyperEdge auxEdge = node.tail.get(0);

				if (!base.contains(auxEdge))
				;
				{
					base.add(auxEdge);
					auxEdge.setChildrenVisited();
				}
			}
		}

		for (Node node : nodes)
			if (!node.visited)
			{
				parents.add(node.tail);
				node.visited = false;
			}

		HyperEdge[] aux = new HyperEdge[parents.size()];

		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations, base);

		return combinations;
	}

	private HashSet<Node> getParentNodes(EdgeSet set)
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

	private void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
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

	// ----------------Algoritmo Exacto End-----------------------------------

	protected static class Node
	{
		private ArrayList<HyperEdge> head; // Las dos listas son necesarias?
		private ArrayList<HyperEdge> tail;

		private String name;

		private boolean visited;

		public ArrayList<HyperEdge> tail()
		{
			return tail;
		}

		public ArrayList<HyperEdge> head()
		{
			return head;
		}

		public String getName()
		{
			return name;
		}

		public Node(String name)
		{
			head = new ArrayList<HyperEdge>();
			tail = new ArrayList<HyperEdge>();
			this.name = name;
			visited = false;
		}

		@Override
		public String toString()
		{
			return "(" + name + ")";
		}
	}

	protected static class HyperEdge
	{
		private ArrayList<Node> head; // Las dos listas son necesarias?
		private ArrayList<Node> tail;

		private String name;

		private boolean visited;
		private boolean isTop;

		private final double weight;

		public HyperEdge(String name, double weight)
		{
			head = new ArrayList<Node>();
			tail = new ArrayList<Node>();

			this.name = name;
			this.weight = weight;
			visited = false;
			isTop = false;
		}

		public ArrayList<Node> tail()
		{
			return tail;
		}

		public ArrayList<Node> head()
		{
			return head;
		}

		public double weight()
		{
			return weight;
		}

		public void setAsTop()
		{
			isTop = true;
		}

		public void setChildrenVisited()
		{
			for (Node node : head)
				node.visited = true;
		}

		@Override
		public int hashCode()
		{
			return super.hashCode(); // Dejamos el hashcode de Object, por ahora
										// Quizas darle un ID unico a cada eje y
										// usar eso
		}

		@Override
		public String toString()
		{
			return "[" + name + ", " + weight + "]";
		}
	}
}
