package back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class AltCombGraph
{

	public String name;

	private Node start;
	private Node end;

	private double minDistance;

	List<HyperEdge> hEdges;
	List<Node> nodes;

	public AltCombGraph(Node start, Node end)
	{
		this.start = start;
		this.end = end;
		hEdges = new ArrayList<HyperEdge>();
		nodes = new ArrayList<Node>();
	}
	
	public AltCombGraph(HyperGraph graph)
	{
		this.start = graph.getStart();
		this.end = graph.getEnd();
		this.name = graph.name;
		hEdges = graph.hEdges;
		nodes = graph.nodes;
	}

	public Node getStart()
	{
		return start;
	}


	public Node getEnd()
	{
		return end;
	}

	public double exactAlgorithm()
	{
		System.out.println("Comenzando EXAL.");
		
		long time = System.currentTimeMillis();
		
		HyperEdge aux = new HyperEdge("no uses este nombre", 0);
		aux.tail.add(end);

		exactAlgorithm(aux);
		aux.path.markPath();
		
		System.out.println("EXAL termino.");
		System.out.println("Tardó: " + (System.currentTimeMillis() - time) + " milisegundos.");
		
		System.out.println("Peso real del camino es: " + aux.path.distance()); //imprimir peso real
		
		return aux.distance;
	}
	
	private double calculatePath(HyperEdge edge) //auxiliar, sacar
	{
		HashSet<HyperEdge> path = new HashSet<HyperEdge>();
		
		fillPath(edge, path);
		
		double total = 0;
		for (HyperEdge e : path)
			total += e.weight;
		return total;
	}
	
	private void fillPath(HyperEdge edge, HashSet<HyperEdge> path) //auxiliar, sacar
	{
		path.add(edge);
		
		for (HyperEdge parent : edge.edgeParents)
			fillPath(parent, path);
	}
	
	
	HashSet<HyperEdge> calculated = new HashSet<HyperEdge>(); 


	public void exactAlgorithm(HyperEdge edge)
	{		
		edge.path = new EdgePath(edge);
		
		if (edge.tail.size() == 1)
		{
			Node aux = edge.tail.get(0);
			if (aux.tail.isEmpty())
			{
				return;
			}

		}

		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();

		for (Node node : edge.tail)
		{
			for (HyperEdge parentEdge : node.tail)
			{
				if (!calculated.contains(parentEdge))
				{
					exactAlgorithm(parentEdge);
					calculated.add(parentEdge);
				}
			}
		}

		HyperEdge[] combination = new HyperEdge[edge.tail.size()];
		HashSet<HyperEdge> result = new HashSet<HyperEdge>();

		for (Node node : edge.tail)
		{
			parents.add(node.tail);
		}
		
		lastDistance = 0;
		
		parentCombinations(parents, 0, combination, result);

		edge.edgeParents.addAll(result);
		
		for (HyperEdge parent : result)
			edge.path.mergeWith(parent.path);
	}
	
	private float resultWeight = 0;
	HashSet<HyperEdge> tempResult = new HashSet<HyperEdge>();
	double lastDistance = 0;

	public void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HyperEdge[] combination,
			HashSet<HyperEdge> result)
	{
		if (index == parents.size())
		{			
			tempResult.clear();

			for (HyperEdge edge : combination)
				tempResult.add(edge);
			
			EdgePath temp = new EdgePath();
			
			for (HyperEdge e : tempResult)
				temp.mergeWith(e.path);
			
			if (temp.distance() < lastDistance || lastDistance == 0)
			{
				result.clear();
				result.addAll(tempResult);
				lastDistance = temp.distance();
			}
			
			return;
		}

		ArrayList<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{
			combination[index] = edge;
			parentCombinations(parents, index + 1, combination,
					result);
		}
	}
}
