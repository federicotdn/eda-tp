package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

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

	private double minDistance;

	private HashSet<HyperEdge> minPath;

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

	private void clearNodeMarks()
	{
		for (Node node : nodes)
			node.visited = false;
	}

	// ----------------Algoritmo Exacto---------------------------------------

	public void minimumPathExact()
	{
		System.out.println("Comenzando busqueda de camino minimo (exacto) en "
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

		System.out.println("Tardo: "
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

	private ArrayList<ArrayList<HyperEdge>> generateParents(
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

		for (Node node : nodes)
			if (!node.visited)
			{
				parents.add(node.tail);
				node.visited = false;
			}

		return parents;
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

	private HashSet<EdgeSet> generateCombinations(HashSet<Node> nodes)
	{

		HashSet<HyperEdge> base = new HashSet<HyperEdge>();

		ArrayList<ArrayList<HyperEdge>> parents = generateParents(nodes, base);

		HyperEdge[] aux = new HyperEdge[parents.size()];

		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations, base);

		return combinations;
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

	// ----------------Algoritmo Aproximado-----------------------------------

	private HyperEdge bestFirstSearch(Node begin, Node finish)
			throws IOException
	{

		HyperEdge hEdge = null;
		boolean hasEnded = false;

		PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>(10,
				new Comparator<HyperEdge>() {
					@Override
					public int compare(HyperEdge edge1, HyperEdge edge2)
					{
						return Double.compare(edge1.path.distance(),
								edge2.path.distance());
					}
				});

		for (HyperEdge edge : begin.head)
		{
			edge.calculatePathDistance();
			if (!edge.isTaboo)
			{
				hq.offer(edge);
			}
		}

		while (!hq.isEmpty() && !hasEnded)
		{
			hEdge = hq.poll();

			for (Node node : hEdge.head)
			{
				if (node == finish)
				{
					hasEnded = true;
					break;
				}
			}
			procesHEdge(hEdge, hq);

		}

		if (!hasEnded && hq.isEmpty())
		{
			return null;
		}

		// me queda hEdge con el camino para arriba
		// marcar ejes del camino

		return hEdge;
	}

	public void minimumPathApprox(long maxTime) throws IOException
	{
		long time = System.currentTimeMillis();
		
		HyperEdge edge = bestFirstSearch(start, end);
		HyperEdge result;
		LinkedList<HyperEdge> taboos;
		
		int numberOfTaboos = 1;
		int i;

		this.minDistance = edge.path.distance();
		this.minPath = edge.path.getPath();

		resetGraph();
		
		boolean flag = false;

		Iterator<HyperEdge> it = minPath.iterator();
		

		while (!flag)
		{
			taboos = new LinkedList<HyperEdge>();
			if(!it.hasNext()){
				it = minPath.iterator();
				numberOfTaboos++;
			}
			else{
				i = 0;
				while(it.hasNext() && i < numberOfTaboos){
					edge = it.next();
					edge.isTaboo = true;
					i++;
					taboos.add(edge);
				}
			}
			
			
			result = bestFirstSearch(start, end);
			if (result != null && (result.path.distance() < minDistance))
			{
				minDistance = result.path.distance();
				minPath = result.path.getPath();
				it = minPath.iterator();
				flag = true;
			} else
			{
				
				for(HyperEdge e: taboos){
					e.isTaboo = false;
				}
			}
			resetGraph();


		}

		System.out.println(minDistance);

	}

	private void resetGraph()
	{
		clearNodeMarks();
		clearEdges();
	}

	private void clearEdges()
	{
		for (HyperEdge edge : hEdges)
		{
			edge.path = null;
			edge.parents = new ArrayList<HyperEdge>();
		}
	}

	private void improvePath(HyperEdge last)
	{

	}

	private void procesHEdge(HyperEdge hEdge, PriorityQueue<HyperEdge> hq)
	{
		for (Node node : hEdge.head)
		{
			if (!node.visited)
			{
				node.visited = true;

				for (HyperEdge edge : node.head)
				{
					if (!edge.isTaboo)
					{
						edge.addVisitor();
						edge.parents.add(hEdge);

						if (edge.currentEntriesCount == edge.tail.size())
						{
							removeUnnecesaryParents(edge);
							edge.calculatePathDistance();
							hq.offer(edge);
						}
					}
				}

			}
		}

	}

	private void removeUnnecesaryParents(HyperEdge hEdge)
	{

		for (Node node : hEdge.tail)
		{
			node.tempParentCount = 0;
		}

		for (HyperEdge edge : hEdge.parents)
		{
			addParentToChildren(edge);
		}

		Iterator<HyperEdge> it = hEdge.parents.iterator();

		while (it.hasNext())
		{

			HyperEdge current = it.next();
			substractParentToChildren(current);

			if (!hasMissingParents(current))
			{
				it.remove();

			} else
			{

				addParentToChildren(current);
			}
		}

		it = hEdge.parents.iterator();

	}

	// ----------------Algoritmo Aproximado-End--------------------------------

	protected static class Node
	{
		private ArrayList<HyperEdge> head; // Las dos listas son necesarias?
		private ArrayList<HyperEdge> tail;

		private String name;

		public int tempParentCount;

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

		private Node()
		{
			this("");
		}

		@Override
		public String toString()
		{
			return "(" + name + ")";
		}
	}

	// Metodos de utilidad para HyperEdge

	public static void addParentToChildren(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			node.tempParentCount++;
		}
	}

	public static void substractParentToChildren(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			node.tempParentCount--;
		}
	}

	public static boolean hasMissingParents(HyperEdge edge)
	{

		for (Node node : edge.head)
		{
			if (node.tempParentCount == 0)
			{
				return true;
			}
		}

		return false;
	}

	protected static class HyperEdge
	{
		private ArrayList<Node> head; // Las dos listas son necesarias?
		private ArrayList<Node> tail;
		private ArrayList<HyperEdge> parents;

		private String name;
		private final double weight;

		private EdgePath path;
		private boolean visited;
		public int currentEntriesCount;
		private boolean isTaboo;

		private boolean isTop;

		public HyperEdge(String name, double weight)
		{
			head = new ArrayList<Node>();
			tail = new ArrayList<Node>();
			parents = new ArrayList<HyperEdge>();

			this.name = name;
			this.weight = weight;
			visited = false;
			isTop = false;

			isTaboo = false;

			currentEntriesCount = 0;
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

		public void calculatePathDistance()
		{
			if (this.path == null)
			{
				this.path = new EdgePath(this);
			}

			for (HyperEdge parent : this.parents)
			{
				this.path.mergeWith(parent.path);
			}
		}

		public void addVisitor()
		{
			currentEntriesCount++;
		}

		public void setAsTop()
		{
			isTop = true;
		}

		public ArrayList<HyperEdge> parents()
		{
			return parents;
		}

		public void setChildrenVisited()
		{
			for (Node node : head)
				node.visited = true;
		}

		public void setAsVisited()
		{
			visited = true;
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
