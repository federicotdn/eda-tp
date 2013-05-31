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
		path.add(initial);
		totalWeight = initial.weight;
	}
	
	public void mergeWith(EdgePath other)
	{
		for (HyperEdge edge : other)
		{
			if (path.add(edge))
				totalWeight += edge.weight;
		}
	}

	@Override
	public Iterator<HyperEdge> iterator()
	{
		return path.iterator();
	}
}
