package back;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class EdgeSet implements Iterable<HyperEdge>
{
	private HashSet<HyperEdge> edges;
	private double totalWeight;
	private EdgeSet parent;

	public EdgeSet(HashSet<HyperEdge> edges)
	{
		this.edges = edges;
	}

	public EdgeSet(HyperEdge[] edges)
	{
		this.edges = new HashSet<HyperEdge>();
		for (HyperEdge e : edges)
		{
			if (this.edges.add(e))
			{
				this.totalWeight += e.weight;
			}

		}
	}

	public EdgeSet(HyperEdge edge)
	{
		HashSet<HyperEdge> aux = new HashSet<HyperEdge>();
		aux.add(edge);
		this.edges = aux;
		totalWeight = edge.weight;
	}

	public void setParent(EdgeSet parent)
	{
		this.parent = parent;
		totalWeight += parent.totalWeight;
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

	public double getTotalWeight()
	{
		return totalWeight;
	}

	public boolean isTop()
	{
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

}
