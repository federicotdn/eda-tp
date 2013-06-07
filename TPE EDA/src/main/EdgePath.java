package main;

import java.util.HashSet;
import java.util.Iterator;

import main.HyperGraph.HyperEdge;

//Clase EdgePath limpia en paquete Main

public class EdgePath implements Iterable<HyperEdge>
{
	public HashSet<HyperEdge> path;
	public double totalWeight;
	
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
	
	@Override
	public Iterator<HyperEdge> iterator()
	{
		return path.iterator();
	}

}
