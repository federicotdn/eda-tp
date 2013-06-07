package main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

//Clase EdgeSet limpia, solo usa parent

public class EdgeSet implements Iterable<HyperEdge>
{
	public HashSet<HyperEdge> edges;
	public double totalWeight;
	public EdgeSet parent;

	public EdgeSet(HyperEdge[] edges)
	{
		this.edges = new HashSet<HyperEdge>();

		for (HyperEdge e : edges)
			if (this.edges.add(e)) this.totalWeight += e.weight;
	}

	public EdgeSet(HyperEdge edge)
	{
		edges = new HashSet<HyperEdge>();
		edges.add(edge);
		totalWeight = edge.weight;
	}

	public void setParent(EdgeSet parent)
	{
		if(parent != null){
		totalWeight += parent.totalWeight;
		this.parent = parent;
		}
	}

	@Override
	public Iterator<HyperEdge> iterator()
	{
		return edges.iterator();
	}

	@Override
	public int hashCode()
	{
		return edges.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		EdgeSet aux = (EdgeSet) obj;
		return edges.equals(aux.edges);
	}


	public boolean isTop()
	{
		// Horrible, cambiar o sacar

		List<Node> tail = edges.iterator().next().tail;
		if (tail.size() == 1)
		{
			Node aux = tail.get(0);
			if (aux.tail.isEmpty())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString()
	{
		return edges.toString() + " " + totalWeight;
	}

	public void addBase(HashSet<HyperEdge> base)
	{
		for (HyperEdge edge : base)
			if (this.edges.add(edge)) this.totalWeight += edge.weight;
	}

}
