package back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HyperGraph
{

	public String name;

	private Node start;
	private Node end;

	private double minDistance;
	
	List<HyperEdge> hEdges;
	List<Node> nodes;

	public HyperGraph(Node start, Node end)
	{
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}

	protected static class Node
	{

		public String name;

		public List<HyperEdge> head;
		public List<HyperEdge> tail;

		public boolean visited;

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
	}

	protected static class HyperEdge
	{

		public String name;

		public List<Node> tail = new ArrayList<Node>();
		public List<Node> head = new ArrayList<Node>();

		public boolean visited;

		public final double weight;

		public double distance;
		
		public int numberOfEntries;

		public HyperEdge(String name, double weight)
		{
			this.name = name;
			this.weight = weight;
			this.numberOfEntries = numberOfEntries;
		}
		

	public void exactAlgorithmAlt()
	{
		Map<String, HyperEdge> calculated = new HashMap<String, HyperEdge>();
		
		HyperEdge imag = new HyperEdge("Imaginary", 0);
		imag.tail.add(end);
		
		exactAlgorithmAlt(imag, calculated);
	}
	
	private void exactAlgorithmAlt(HyperEdge hEdge, Map<String,HyperEdge >calculated){
		
		for(Node node: hEdge.tail ){
			for(HyperEdge edge: node.tail){
				edge.distance = edge.weight + hEdge.distance;
			}
		}
		
		
	}


}
