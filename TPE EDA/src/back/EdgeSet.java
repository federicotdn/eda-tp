package back;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class EdgeSet implements Iterable<HyperEdge>
{
	public HashSet<HyperEdge> edges;
	private double totalWeight;
	private EdgeSet parent;
	private EdgeSet child;
	

	public EdgeSet(HashSet<HyperEdge> edges)
	{
		this.edges = edges;
	}
	
	public EdgeSet()
	{
		this.edges = new HashSet<HyperEdge>();
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
		if(parent != null){
			totalWeight += parent.totalWeight;

		}
		this.parent = parent;
	}
	
	public void setChild(EdgeSet child)
	{
		if(child != null){
			this.totalWeight += child.totalWeight;

		}
		this.child = child;
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
	
	public boolean contains(HyperEdge edge){
		return edges.contains(edge);
	}
	
	public boolean add(HyperEdge edge){
		boolean aux = edges.add(edge);
		if(aux){
			totalWeight += edge.weight;
		}
		return aux;
	}
	
	public  EdgeSet(EdgeSet set){
			this.edges = new HashSet<HyperEdge>();
			this.edges.addAll(set.edges);
			this.totalWeight = set.totalWeight;
	}
	
	public int size(){
		return edges.size();
	}
	
	public void setEdges(EdgeSet set){
		this.edges = set.edges;
		this.totalWeight = set.totalWeight;
	}
	
	public EdgeSet getParent(){
		return parent;
	}
	
	public boolean isEmpty(){
		return edges.isEmpty();
	}
	
	public void addBase(HashSet<HyperEdge> base){
		for(HyperEdge edge: base){
			if(this.edges.add(edge)){
				this.totalWeight += edge.weight;
			}
		}
	}
	
	public HashSet<HyperEdge> getEdges(){
		return edges;
	}
	
	public void removeChild(){
		this.totalWeight = this.totalWeight - child.totalWeight;
		this.child = null;
	}
	
	
	

}
