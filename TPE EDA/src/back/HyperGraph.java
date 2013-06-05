package back;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class HyperGraph
{

	public String name;

	private Node start;
	private Node end;

	private double minDistance;

	private EdgeSet minPath;

	List<HyperEdge> hEdges;
	List<Node> nodes;

	public HyperGraph(Node start, Node end)
	{
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}

	public Node getStart()
	{
		return start;
	}

	public Node getEnd()
	{
		return end;
	}

	protected static class Node
	{

		public String name;

		public ArrayList<HyperEdge> head;
		public ArrayList<HyperEdge> tail;

		public boolean visited;

		public int tempoParentCount;

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

		@Override
		public int hashCode()
		{
			return name.hashCode();
		}
	}

	protected static class HyperEdge
	{

		public String name;

		public List<Node> tail = new ArrayList<Node>();
		public List<Node> head = new ArrayList<Node>();
		public List<HyperEdge> edgeParents = new ArrayList<HyperEdge>();

		public boolean visited;

		public boolean isTop;

		public final double weight;

		public EdgePath path; // si anda entonces no es necesario tener distance

		public double distance;

		public Set<HyperEdge> parents = new HashSet<HyperEdge>();
		public Set<HyperEdge> children = new HashSet<HyperEdge>();

		public int numberOfEntries;// hacer final
		public int currentEntriesCount;

		public HyperEdge(String name, double weight)
		{
			this.name = name;
			this.weight = weight;
			visited = false;
		}

		@Override
		public String toString()
		{
			return "[" + name + ", " + weight + "]";
		}

		public void setChildrenVisited()
		{
			for (Node node : head)
				node.visited = true;
		}

		public void addVisitor()
		{

			currentEntriesCount++;
		}

		public void addParentToChildren()
		{

			for (Node node : this.head)
			{
				node.tempoParentCount++;
			}
		}

		public void substractParentToChildren()
		{

			for (Node node : this.head)
			{
				node.tempoParentCount--;
			}
		}

		public boolean hasMissingParents()
		{

			for (Node node : this.head)
			{
				if (node.tempoParentCount == 0)
				{
					return true;
				}
			}

			return false;
		}

		public void calculateDitance()
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

		public void calculateDitanceAlt()
		{
			if (this.path == null)
			{
				this.path = new EdgePath(this);
			}
			for (HyperEdge child : this.children)
			{
				this.path.mergeWith(child.path);
			}
		}
	}

	public double exactAlgorithm() throws IOException
	{

		HyperEdge hEdge = null;
		boolean hasEnded = false;

		PriorityQueue<HyperEdge> hq = new PriorityQueue<HyperEdge>(10,
				new Comparator<HyperEdge>() {
					@Override
					public int compare(HyperEdge edge1, HyperEdge edge2)
					{
						Double aux1 = (Double) edge1.path.distance();
						Double aux2 = (Double) edge2.path.distance();
						return aux1.compareTo(aux2);
					}
				});

		for (HyperEdge edge : start.head)
		{
			edge.calculateDitance();
			hq.offer(edge);
		}

		while (!hq.isEmpty() && !hasEnded)
		{
			hEdge = hq.poll();

			for (Node node : hEdge.head)
			{
				if (node == end)
				{
					hasEnded = true;
					break;
				}
			}
			procesHEdge(hEdge, hq);

		}

		EdgeSet aux = new EdgeSet(hEdge);

		toSet(hEdge);

		double w = 0;

		for (HyperEdge edge : this.aux)
		{
			w += edge.weight;
		}

		System.out.println(w);

		System.out.println(hEdge.path.distance());

		generateSet(aux);

		minPath = aux;

		// improvePath();

		return aux.getTotalWeight();

	}

	HashSet<HyperEdge> aux = new HashSet<HyperEdge>();

	public void toSet(HyperEdge edge)
	{
		aux.add(edge);
		for (HyperEdge e : edge.parents)
		{
			toSet(e);
		}
	}

	private void procesHEdge(HyperEdge hEdge, PriorityQueue<HyperEdge> hq)
	{

		hEdge.visited = true;

		for (Node node : hEdge.head)
		{

			if (!node.visited)
			{
				node.visited = true;

				for (HyperEdge edge : node.head)
				{
					edge.addVisitor();
					edge.parents.add(hEdge);

					if (edge.currentEntriesCount == edge.tail.size())
					{
						removeUnnecesaryParents(edge);
						edge.calculateDitance();
						hq.offer(edge);

					}
				}

			}
		}

	}

	private void removeUnnecesaryParents(HyperEdge hEdge)
	{

		for (Node node : hEdge.tail)
		{
			node.tempoParentCount = 0;

		}

		for (HyperEdge edge : hEdge.parents)
		{
			edge.addParentToChildren();
		}

		Iterator<HyperEdge> it = hEdge.parents.iterator();

		while (it.hasNext())
		{

			HyperEdge current = it.next();
			current.substractParentToChildren();

			if (!current.hasMissingParents())
			{
				it.remove();

			} else
			{

				current.addParentToChildren();
			}
		}

		it = hEdge.parents.iterator();

	}

	private void generateSetAlt(EdgeSet set)
	{
		EdgeSet current = set;

		while (!set.isEmpty())
		{

			EdgeSet newSet = new EdgeSet();

			for (HyperEdge edge : set)
			{
				for (HyperEdge parent : edge.parents)
				{
					newSet.add(parent);
				}
			}
			
		}
		
	}
	
	HashSet<HyperEdge> added = new HashSet<HyperEdge>();

	private void generateSet(EdgeSet set)
	{

		System.out.println(set);
		EdgeSet newSet = new EdgeSet();
		for (HyperEdge edge : set)
		{
			for (HyperEdge parent : edge.parents)
			{
				if(!added.contains(parent)){
					newSet.add(parent);
				}
				
			}
		}
		if (newSet.isEmpty())
		{
			return;
		}
		// newSet.setChild(set);
		generateSet(newSet);
		set.setParent(newSet);

	}

	private void improvePath()
	{

		HashSet<Node> nodes;
		HashSet<HyperEdge> base;

		EdgeSet current = minPath;
		EdgeSet previous = null;
		EdgeSet alternativePath = null;
		double total = 0;

		int i = 0;
		while (current != null)
		{
			nodes = getParentNodes(current);
			base = new HashSet<HyperEdge>();
			ArrayList<ArrayList<HyperEdge>> parents = generateParents(nodes,
					base);
			HyperEdge[] aux = new HyperEdge[parents.size()];

			EdgeSet temp = new EdgeSet(current);
			EdgeSet tempPrev = null;
			if (previous != null)
			{
				tempPrev = new EdgeSet(previous);

			}

			minComb = null;

			minCombination(parents, 0, aux, base);

			if (minComb.getTotalWeight() < current.getSelfWeight())
			{

				EdgeSet tempParent = null;
				if (current.getParent() != null)
				{
					tempParent = new EdgeSet(current.getParent());
					minComb.setParent(tempParent);
				}

				if (previous != null)
				{
					tempPrev.setParent(minComb);
				}
				temp = minComb;
			}
			if (i == 0)
			{
				alternativePath = temp;
			}
			total += temp.getSelfWeight();
			previous = current;
			current = current.getParent();
			i++;

		}

		System.out.println(alternativePath.getTotalWeight());

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
				nodes.addAll(edge.head);
			}
		}
		return nodes;
	}

	EdgeSet minComb;

	private void minCombination(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HyperEdge[] combination, HashSet<HyperEdge> base)
	{
		if (index == parents.size())
		{
			EdgeSet aux = new EdgeSet(combination);
			aux.addBase(base);
			if (minComb == null
					|| (aux != null && aux.getTotalWeight() < minComb
							.getTotalWeight()))
			{
				minComb = aux;
			}

			return;
		}

		ArrayList<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{
			combination[index] = edge;
			minCombination(parents, index + 1, combination, base);
		}
	}

}
