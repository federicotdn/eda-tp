package main;

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
	public String name;

	public Node start;
	public Node end;

	public List<HyperEdge> hyperEdges;
	public List<Node> nodes;

	public double minDistance;

	public HashSet<HyperEdge> minPath;

	private long startingTime;

	private long maxTime;

	public HyperGraph(String name, Node start, Node end)
	{
		this.name = name;
		this.start = start;
		this.end = end;
		hyperEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}


	public void clearNodeMarks()
	{
		for (Node node : nodes)
			node.visited = false;
	}
	
	public  void clearEdges()
	{
		for (HyperEdge edge : hyperEdges)
		{
			edge.edgePath = null;
			edge.parents = new ArrayList<HyperEdge>();
			edge.currentEntriesCount = 0;
		}
	}

	// ----------------Algoritmo Exacto---------------------------------------

	public double minimumPathExact()
	{
		EdgeSet min = null;
		
		long time = System.currentTimeMillis();

		for (HyperEdge edge : end.tail)
		{
			EdgeSet aux = new EdgeSet(edge);

			minimumPathExact(aux);

			if (min == null || aux.totalWeight < min.totalWeight)
				min = aux;

		}

		markEdgeSetPath(min);
		end.visited = true;
		
		System.out.println("Tardó: " + (double) (System.currentTimeMillis() - time)/1000 + "  segundos");
		
		return min.totalWeight;
		
	}

	private HashMap<Integer, EdgeSet> visited = new HashMap<Integer, EdgeSet>();
	private HashMap<HashSet, EdgeSet> visited2 = new HashMap<HashSet, EdgeSet>();
	

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

			if (!visited2.containsKey(sample.edges))
			{
				visited2.put(sample.edges, sample);

				minimumPathExact(sample);
			} else
			{
				sample = visited2.get(sample.edges);
			}

			if (min == null || (sample.totalWeight < min.totalWeight))
			{
				min = sample;
			}
		}

		current.setParent(min);
	}

	private List<List<HyperEdge>> generateParents(
			HashSet<Node> nodes, HashSet<HyperEdge> base)
	{
		List<List<HyperEdge>> parents = new ArrayList<List<HyperEdge>>();

		for (Node node : nodes)
		{
			if (node.tail.size() == 1)
			{
				HyperEdge auxEdge = node.tail.get(0);

				if (!base.contains(auxEdge))
				{
					base.add(auxEdge);
					auxEdge.setChildrenVisited(true);
				}
			}
		}

		for (Node node : nodes)
			if (!node.visited)
			{
				parents.add(node.tail);

			}
			else{
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

		List<List<HyperEdge>> parents = generateParents(nodes, base);

		HyperEdge[] aux = new HyperEdge[parents.size()];

		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations, base);

		return combinations;
	}

	private void parentCombinations(List<List<HyperEdge>> parents,
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

		List<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{

			combination[index] = edge;

			parentCombinations(parents, index + 1, combination, combinations,
					base);
		}
	}

	private void markEdgeSetPath(EdgeSet set)
	{
		if (set == null)
		{
			return;
		}

		markHashPath(set.edges);
		markEdgeSetPath(set.parent);

	}

	// ----------------Algoritmo Exacto End-----------------------------------

	// ----------------Algoritmo Aproximado-----------------------------------

	private HyperEdge bestFirstSearch(Node begin, Node finish)
	{

		HyperEdge hEdge = null;
		boolean hasEnded = false;

		PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>(
				hyperEdges.size(), new Comparator<HyperEdge>() {
					@Override
					public int compare(HyperEdge edge1, HyperEdge edge2)
					{
						return Double.compare(edge1.edgePath.totalWeight,
								edge2.edgePath.totalWeight);
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
			if ((System.currentTimeMillis() - startingTime) > maxTime)
			{
				return null;
			}

			hEdge = hq.poll();

			if(hEdge.isBottom){
				return hEdge;
			}
			procesHEdge(hEdge, hq);

		}

		
			return null;
		

		// me queda hEdge con el camino para arriba
		// marcar ejes del camino

	}

	public double minimumPathApproxAlt2(int maxTimeSeg)
	{
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge edge = bestFirstSearch(start, end);
		this.minDistance = edge.edgePath.totalWeight;
		this.minPath = edge.edgePath.path;
		
		

		improvePath(edge);

		clearNodeMarks();
		
		return minDistance;

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

	

	private void improvePath(HyperEdge last)
	{
		HyperEdge edge;
		HyperEdge result = null;
		HashSet<HyperEdge> current = last.edgePath.path;
		HashSet<HyperEdge> previous = null;
		HashSet<HyperEdge> taboo = new HashSet<HyperEdge>();
		
		HashSet<HyperEdge> added = new HashSet<HyperEdge>();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{

			if (current != null)
			{
				previous = current;
				
				edge = pickRandomEdge(current);

			} else
			{
				edge = pickRandomEdge(previous);

			}

			edge.isTaboo = true;
			resetGraph();

			result = bestFirstSearch(start, end);
			if (result != null && (result.edgePath.totalWeight < minDistance))
			{
				minDistance = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				current = minPath;
			}

			edge.isTaboo = false;

			if (result == null)
			{
				current = null;
			} else
			{
				current = result.edgePath.path;
			}

		}

		
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

	public double minimumPathApproxAlt(int maxTimeSeg)
	{
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge edge = bestFirstSearch(start, end);
		HyperEdge result = null;

		HashSet<HyperEdge> recentTaboos = new HashSet<HyperEdge>();
		HashSet<HyperEdge> current;

		int count = 0;

		this.minDistance = edge.edgePath.totalWeight;
		this.minPath = edge.edgePath.path;
		current = minPath;

		// resetGraph();

		Iterator<HyperEdge> it = minPath.iterator();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{
			if (!it.hasNext())
			{
				for (HyperEdge taboo : recentTaboos)
				{
					taboo.isTaboo = false;
				}
				recentTaboos = new HashSet<HyperEdge>();
				current = pickRandomNeighbour(current);
				it = current.iterator();

			}

			edge = it.next();
			edge.isTaboo = true;
			recentTaboos.add(edge);
			resetGraph();
			result = bestFirstSearch(start, end);

			if (result != null && (result.edgePath.totalWeight < minDistance))
			{
				minDistance = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				it = minPath.iterator();
				current = minPath;

			} else
			{
				edge.isTaboo = false;
			}

			long thisTime = (System.currentTimeMillis() - startingTime) / 1000;

			
			if (count >= current.size())
			{
				for (HyperEdge taboo : recentTaboos)
				{
					taboo.isTaboo = false;
				}
				recentTaboos = new HashSet<HyperEdge>();
				count = 0;

			}

		}

		
		resetGraph();
		
		return minDistance;

	}

	private HashSet<HyperEdge> pickRandomNeighbour(HashSet<HyperEdge> current)
	{

		HashSet<HyperEdge> neighbour = current;
		HyperEdge result = null;
		HyperEdge edge;

		resetGraph();
		int i = 0;
		while (result == null
				&& (System.currentTimeMillis() - startingTime) < maxTime)
		{
			edge = pickRandomEdge(current);
			edge.isTaboo = true;
			result = bestFirstSearch(start, end);
			edge.isTaboo = false;

			if (result != null)
			{
				neighbour = result.edgePath.path;
			}

			resetGraph();

		}
		return neighbour;
	}

	private void markHashPath(HashSet<HyperEdge> set)
	{
		
		for (HyperEdge edge : set)
		{
			edge.visited = true;
			for(Node node: edge.tail){
				node.visited = true;
			}
		}

	}


	public double minimumPathApprox(int maxTimeSeg)
	{
		maxTime = maxTimeSeg * 1000;
		startingTime = System.currentTimeMillis();

		HyperEdge edge = bestFirstSearch(start, end);
		HyperEdge result = null;

		HashSet<HyperEdge> recentTaboos = new HashSet<HyperEdge>();
		HashSet<HyperEdge> current;

		ArrayList<HyperEdge> taboos = new ArrayList<HyperEdge>();
		int numberOfTaboos = 1;
		int i = 0;

		int count = 0;
		int maxC = 0;

		this.minDistance = edge.edgePath.totalWeight;
		this.minPath = edge.edgePath.path;
		current = minPath;

		for (HyperEdge e : hyperEdges)
		{
			maxC += e.head.size();
		}
		for(Node node: nodes){
			maxC += node.head.size();
		}

		Iterator<HyperEdge> it = minPath.iterator();

		while ((System.currentTimeMillis() - startingTime) < maxTime)
		{
			taboos = new ArrayList<HyperEdge>();
			if (!it.hasNext())
			{
				if (numberOfTaboos >= current.size() )
				{
					current = pickRandomNeighbour(current);
					it = current.iterator();
					numberOfTaboos = 1;

				} else
				{
					numberOfTaboos++;
					it = current.iterator();
				}

			}
			i = 0;

			while (it.hasNext() && i < numberOfTaboos)
			{
				edge = it.next();
				edge.isTaboo = true;
				recentTaboos.add(edge);
				taboos.add(edge);
				i++;
			}

			resetGraph();
			result = bestFirstSearch(start, end);

			if (result != null && (result.edgePath.totalWeight < minDistance))
			{
				minDistance = result.edgePath.totalWeight;
				minPath = result.edgePath.path;
				it = minPath.iterator();
				current = minPath;
				numberOfTaboos = 1;

			} else
			{
				for (HyperEdge taboo : taboos)
				{
					taboo.isTaboo = false;
				}
			}


			if (count >= maxC)
			{
				for (HyperEdge taboo : recentTaboos)
				{
					taboo.isTaboo = false;
				}
				recentTaboos = new HashSet<HyperEdge>();
				count = 0;

			}
			count++;

		}

		resetGraph();
		end.visited = true;
		markHashPath(minPath);
//		System.out.println(minDistance);
//		System.out.println((double) (System.currentTimeMillis() - startingTime)
//				/ 1000 + " segundos ");
		
		return minDistance;

	}

	// ----------------Algoritmo Aproximado-End--------------------------------

	public static class Node
	{
		public List<HyperEdge> head; // Las dos listas son necesarias?
		public List<HyperEdge> tail;

		public String name;

		public int tempParentCount;

		public boolean visited;


		public String getName()
		{
			return name;
		}

		public Node(String name)
		{
			head = new LinkedList();
			tail = new LinkedList();
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

	public static class HyperEdge
	{
		public List<Node> head; // Las dos listas son necesarias?
		public List<Node> tail;
		public List<HyperEdge> parents;

		public String name;
		public final double weight;

		public EdgePath edgePath;
		public boolean visited;
		public int currentEntriesCount;
		public boolean isTaboo;

		public boolean isTop;
		public boolean isBottom;

		public HyperEdge(String name, double weight)
		{
			head = new LinkedList<Node>();
			tail = new LinkedList<Node>();
			parents = new LinkedList<HyperEdge>();

			this.name = name;
			this.weight = weight;
			visited = false;
			isTop = false;
			isBottom = false;

			isTaboo = false;

			currentEntriesCount = 0;
		}


		public void calculatePathDistance()
		{
			if (this.edgePath == null)
			{
				this.edgePath = new EdgePath(this);
			}

			for (HyperEdge parent : this.parents)
			{
				this.edgePath.mergeWith(parent.edgePath);
			}
		}

		public void addVisitor()
		{
			currentEntriesCount++;
		}

		public void setChildrenVisited(boolean value)
		{
			for (Node node : head)
				node.visited = value;
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
