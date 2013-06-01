package back;

import java.util.HashSet;
import java.util.Iterator;

import back.HyperGraph.HyperEdge;

public class EdgePath implements Iterable<HyperEdge>
{
	private HashSet<HyperEdge> path;
	private double totalWeight;
	
	public EdgePath(HyperEdge initial)
	{
		path = new HashSet<HyperEdge>();
		totalWeight = 0;
		addEdge(initial);
	}
	
	public EdgePath()
	{
		path = new HashSet<HyperEdge>();
		totalWeight = 0;
	}
	
	public void addEdge(HyperEdge edge)
	{
		if (path.add(edge))
			totalWeight += edge.weight;
	}
	
	public void mergeWith(EdgePath other)
	{
		for (HyperEdge edge : other)
		{
			if (path.add(edge))
				totalWeight += edge.weight;
		}
	}
	
	public double distance()
	{
		return totalWeight;
	}
	
	public void markPath()
	{
		for (HyperEdge edge : path)
			edge.visited = true;
	}

	@Override
	public Iterator<HyperEdge> iterator()
	{
		return path.iterator();
	}
}