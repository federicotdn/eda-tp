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

		markPath(min);
	}

	private HashMap<Integer, EdgeSet> visited = new HashMap<Integer, EdgeSet>();

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

		int i = 0;
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

		Node[] nodesArray = new Node[nodes.size()];

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

	private void markPath(EdgeSet set)
	{
		if (set == null)
		{
			return;
		}

		for (HyperEdge edge : set)
		{
			edge.visited = true;
		}

		markPath(set.getParent());

	}

	// ----------------Algoritmo Exacto End-----------------------------------

	// ----------------Algoritmo Aproximado-----------------------------------

	private HyperEdge bestFirstSearch(Node begin, Node finish,
			long remainingTime) throws IOException
	{

		HyperEdge hEdge = null;
		boolean hasEnded = false;

		PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>(
				hEdges.size(), new Comparator<HyperEdge>() {
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
			// if (remainingTime <= 0)
			// {
			// return null;
			// }

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

	public void minimumPathApprox(int maxTimeSeg) throws IOException
	{
		long maxTimeMillis = maxTimeSeg * 1000;
		long startingTime = System.currentTimeMillis();

		HyperEdge edge = bestFirstSearch(start, end,
				maxTimeMillis - (System.currentTimeMillis() - startingTime));
		this.minDistance = edge.path.distance();
		this.minPath = edge.path.getPath();

		improvePath(edge, startingTime, maxTimeMillis);

		clearNodeMarks();

	}

	private HyperEdge pickRandomEdge(HashSet<HyperEdge> set)
	{
		int rand = (int) (Math.random() * set.size());
		Iterator<HyperEdge> it = set.iterator();
		HyperEdge edge = null;

		for (int i = 0; i <= rand; i++)
		{
			edge = it.next();
		}

		return edge;
	}

	public void resetGraph()
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
			edge.currentEntriesCount = 0;
		}
	}

	private void improvePath(HyperEdge last, long startingTime,
			long maxTimeMillis) throws IOException
	{
		HyperEdge edge;
		HyperEdge result = null;
		HashSet<HyperEdge> current = last.path.getPath();
		HashSet<HyperEdge> previous = null;
		HashSet<HyperEdge> taboo = new HashSet<HyperEdge>();

		int i = 0;

		while ((System.currentTimeMillis() - startingTime) < maxTimeMillis)
		{

			if (i == 1000)
			{
				taboo = new HashSet<HyperEdge>();
			}

			if (current != null)
			{
				previous = current;
				edge = pickRandomEdge(current);
				while (taboo.contains(edge))
				{
					edge = pickRandomEdge(current);
				}
			} else
			{
				edge = pickRandomEdge(previous);
				while (taboo.contains(edge))
				{
					edge = pickRandomEdge(previous);
				}
			}

			edge.isTaboo = true;
			taboo.add(edge);
			resetGraph();

			result = bestFirstSearch(start, end,
					maxTimeMillis - (System.currentTimeMillis() - startingTime));
			if (result != null && (result.path.distance() < minDistance))
			{
				minDistance = result.path.distance();
				minPath = result.path.getPath();
				System.out.println(result.path.distance());
				current = minPath;
			}

			edge.isTaboo = false;

			if (result == null)
			{
				current = null;
			} else
			{
				current = result.path.getPath();
			}

		}

		System.out.println(minDistance);
		System.out.println((double) (System.currentTimeMillis() - startingTime)
				/ 1000 + " segundos ");
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

	public void minimumPathApproxAlt(int maxTimeSeg) throws IOException
	{
		long maxTimeMillis = maxTimeSeg * 1000;
		long startingTime = System.currentTimeMillis();
		long lastTime = 0;

		HyperEdge edge = bestFirstSearch(start, end,
				maxTimeMillis - (System.currentTimeMillis() - startingTime));
		HyperEdge result;
		LinkedList<HyperEdge> taboos;

		HashSet<HyperEdge> recentTaboos = new HashSet<HyperEdge>();
		int numberOfTaboos = 1;
		int i;
		int size = 0;
		int count = 0;

		this.minDistance = edge.path.distance();
		this.minPath = edge.path.getPath();

		resetGraph();

		Iterator<HyperEdge> it = minPath.iterator();

		while ((System.currentTimeMillis() - startingTime) < maxTimeMillis)
		{
			taboos = new LinkedList<HyperEdge>();
			if (!it.hasNext())
			{
				it = minPath.iterator();
				numberOfTaboos++;
				size = minPath.size();
				//System.out.println(numberOfTaboos);
			}

			i = 0;
			while (it.hasNext() && i < numberOfTaboos)
			{
				edge = it.next();
//				if (!recentTaboos.contains(edge))
//				{
					i++;
					taboos.add(edge);
//					recentTaboos.add(edge);
					edge.isTaboo = true;
//				}
			}

			result = bestFirstSearch(start, end,
					maxTimeMillis - (System.currentTimeMillis() - startingTime));

			if (result != null && (result.path.distance() < minDistance))
			{
				minDistance = result.path.distance();
				System.out.println("Encontre resultado menor: " + minDistance);
				minPath = result.path.getPath();
				it = minPath.iterator();
				size = minPath.size();
			}else{

			for (HyperEdge e : taboos)
			{
				e.isTaboo = false;
			}

			}
			
//			if(numberOfTaboos >= size && result != null){
//				size = result.path.getPath().size();
//				it = result.path.getPath().iterator();
//				numberOfTaboos= 0;
//				
//			}

			long thisTime = (System.currentTimeMillis() - startingTime) / 1000;

			if (thisTime != lastTime && thisTime % 5 == 0)
			{
				System.out.println("Tiempo: " + thisTime + " s");
				lastTime = thisTime;
			}
			
			resetGraph();
			
		}

		System.out.println(minDistance);

	}

	private void markPath(HashSet<HyperEdge> set)
	{
		for (HyperEdge edge : set)
		{
			edge.visited = true;
		}

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
			return super.hashCode();
		}

		@Override
		public String toString()
		{
			return "[" + name + ", " + weight + "]";
		}
	}
}
