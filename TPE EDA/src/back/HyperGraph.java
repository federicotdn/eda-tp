package back;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

		public ArrayList<HyperEdge> head;
		public ArrayList<HyperEdge> tail;

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
		public List<HyperEdge> edgeParents = new ArrayList<HyperEdge>();

		public boolean visited;

		public final double weight;

		public double distance;

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
		
		@Override
		public int hashCode()
		{
			return name.hashCode();
		}
	}

	public double exactAlgorithm()
	{
		System.out.println("Comenzando EXAL.");
		
		long time = System.currentTimeMillis();
		
		HyperEdge aux = new HyperEdge("no uses este nombre", 0);
		aux.tail.add(end);

		exactAlgorithm(aux);
		
		//Marcar el camino (quizas este mal hecho), y agregar los nodos a un set (mal)
		HashSet<HyperEdge> path = new HashSet<HyperEdge>();
		markPath(aux, path);
		
		//Medir el peso total (mal)
		double total = 0;
		for (HyperEdge e : path)
			total += e.weight;
		
		System.out.println("EXAL termino.");
		System.out.println("Tardó: " + (System.currentTimeMillis() - time) + " milisegundos.");
		
		System.out.println("Se visitaron: " + totalejes + " ejes");
		System.out.println("Se calcularon: " + totalcombinaciones + " combinaciones");
		
		System.out.println("Peso real del camino es: " + total); //imprimir peso real
		
		return aux.distance;
	}

	
	private void markPath(HyperEdge edge, HashSet<HyperEdge> path)
	{
		edge.visited = true;
		path.add(edge);
		
		for (HyperEdge parent : edge.edgeParents)
			markPath(parent, path);
	}
	
	//Variables globales de EXACT ALGORITHM despues las saco
	
	public HashSet<HyperEdge> visited = new HashSet<HyperEdge>();
	
	int combinaciones = 0;
	
	int totalcombinaciones = 0;
	int totalejes = 0;
	
	//end variables globales

	public void exactAlgorithm(HyperEdge edge)
	{
		//DEBUG start
		//System.out.println("Parseando edge: " + edge.name);
		
		visited.add(edge);
		totalejes++;
		
		//DEBUG end
		
		if (edge.tail.size() == 1)
		{
			Node aux = edge.tail.get(0);
			if (aux.tail.isEmpty())
			{
				//System.out.println("Llegue al nodo inicio.");
				edge.distance = edge.weight;
				return;
			}

		}

		ArrayList<ArrayList<HyperEdge>> parents = new ArrayList<ArrayList<HyperEdge>>();
		float total = 0;

		HashSet<HyperEdge> calculated = new HashSet<HyperEdge>(); //puede ser mucho mas general

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
		
		int cantidadComb = 1; //debuggear parentCombinations, sacar despues

		for (Node node : edge.tail)
		{
			parents.add(node.tail);
			cantidadComb *= node.tail.size(); //debuggear parentCombinations, sacar despues
		}

		parentCombinations(parents, 0, combination, result);
		
//		System.out.print("eleji: "); //sacar
//		for (HyperEdge e : result) //debugging, sacar
//		{
//			System.out.print(e + " ");
//		}
//		System.out.print("\n"); //sacar todo!!!
		
		if (cantidadComb != combinaciones) //debuggear parentCombinations, sacar despues
		{
			System.out.println("No se hicieron la cantidad de combinaciones necesarias.");
			System.out.println("Deberian haber " + cantidadComb + " pero hubieron " + combinaciones);
		}
		
		combinaciones = 0; //debuggear parentCombinations, sacar despues

		edge.distance = edge.weight + combinationWeight(result);
		
		//Si se hace un hashset global no es necesario el clear
		edge.edgeParents.clear();
		edge.edgeParents.addAll(result);
		
		//System.out.println("Distancia acumulada es: " + edge.distance);
	}

	public void parentCombinations(ArrayList<ArrayList<HyperEdge>> parents,
			int index, HyperEdge[] combination,
			HashSet<HyperEdge> result)
	{
		if (index == parents.size())
		{
			HashSet<HyperEdge> tempResult = new HashSet<HyperEdge>();
			
//			System.out.print("combinacion: "); //sacar
//			for (HyperEdge e : combination) //debugging, sacar
//			{
//				System.out.print(e + " ");
//			}
//			System.out.print("\n"); //sacar

			for (HyperEdge edge : combination)
				tempResult.add(edge);

			if (combinationWeight(tempResult) < combinationWeight(result) || result.isEmpty())
			{
				result.clear();
				result.addAll(tempResult);
			}
			
			totalcombinaciones++; //sacar
			combinaciones++; //debugging, sacar
			
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

	private float combinationWeight(HashSet<HyperEdge> comb)
	{
		float sum = 0;
		for (HyperEdge e : comb)
			sum += e.distance;
		return sum;
	}

	// Alt algorithm abajo

	public void exactAlgorithmAlt()
	{
		Map<String, HyperEdge> calculated = new HashMap<String, HyperEdge>();

		HyperEdge imag = new HyperEdge("Imaginary", 0);
		imag.tail.add(end);

		exactAlgorithmAlt(imag, calculated);
	}

	private void exactAlgorithmAlt(HyperEdge hEdge,
			Map<String, HyperEdge> calculated)
	{

		for (Node node : hEdge.tail)
		{
			for (HyperEdge edge : node.tail)
			{
				edge.distance = edge.weight + hEdge.distance;
			}
		}

	}
}
