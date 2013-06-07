package main.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.structure.EdgeSet;
import main.structure.HyperGraph;
import main.structure.HyperGraph.HyperEdge;
import main.structure.HyperGraph.Node;

/**
 * La clase estatica <code>MinimumPathExactAlgorithm</code> contiene todos los metodos necesarios para buscar el camino minimo
 * en un hipergrafo dado.
 */
public class MinimumPathExactAlgorithm
{
	private static HyperGraph graph;

	private static HashMap<Integer, EdgeSet> visited = new HashMap<Integer, EdgeSet>();

	
	/**
	 * <code>execute</code> es el unico metodo publico de la clase.  Recibe una instancia de HyperGraph, y llama a la
	 * funcion <code>minimumPathExact</code> con cada eje padre del nodo destino (es decir, los ejes inferiores del grafo).
	 * Guarda una referencia al eje (encapsulado en una instancia de <code>EdgeSet</code>) cuyo camino calculado dio minimo, y 
	 * devuelve el peso del camino.
	 * 
	 * @param hyperGraph
	 * @return
	 */
	public static double execute(HyperGraph hyperGraph)
	{
		graph = hyperGraph;

		EdgeSet min = null;

		for (HyperEdge edge : graph.end.tail)
		{
			EdgeSet aux = new EdgeSet(edge);

			minimumPathExact(aux);

			if (min == null || aux.totalWeight < min.totalWeight)
				min = aux;

		}
		
		//Marcar el camino para poder grabarlo
		markPath(min);
		graph.end.visited = true;

		return min.totalWeight;

	}
	
	/**
	 * 
	 * Funcion recursiva central del algoritmo exacto.  Su comportamiento esta descrito en el informe entregado.
	 * 
	 * <p> Al finalizar el algoritmo, la variable EdgeSet current tiene como padre <code>parent</code> a otro conjunto de 
	 * ejes (esto se repite), formando asi el camino que se calculo.
	 * 
	 */
	private static void minimumPathExact(EdgeSet current)
	{

		HashSet<Node> nodes = getParentNodes(current);

		if (nodes.size() == 0) // Caso base
			return;

		HashSet<EdgeSet> combinations = generateCombinations(nodes);

		EdgeSet min = null;

		// aux = null; //Dejar esto?
		// nodes = null;
		// parents = null;
		// base = null;

		for (EdgeSet sample : combinations)
		{
			Integer hash = sample.hashCode();

			if (!visited.containsKey(hash))
			{
				visited.put(hash, sample);

				minimumPathExact(sample);
			} else
			{
				sample = visited.get(hash);
			}

			if (min == null || (sample.totalWeight < min.totalWeight))
			{
				min = sample;
			}
		}

		current.setParent(min);
	}

	/**
	 * El metodo <code>generateParents</code> se encarga de tomar un conjunto de nodos, y devolver una lista donde cada elemento
	 * de la lista es una lista de ejes padre, por cada nodo.  Con esta lista luego se pueden generar combinaciones de ejes
	 * padre distintias.
	 * 
	 * @param nodes - conjunto de nodos a analizar los padres
	 * @param base - conjunto de nodos con un solo eje padre (optimizacion explicada en informe)
	 * @return Lista de lista de ejes, representando los padres de cada nodo por separado
	 */
	private static List<List<HyperEdge>> generateParents(
			HashSet<Node> nodes, HashSet<HyperEdge> base)
	{
		List<List<HyperEdge>> parents = new ArrayList<List<HyperEdge>>();

		for (Node node : nodes)
		{
			if (node.tail.size() == 1)
			{
				HyperEdge auxEdge = node.tail.get(0);

				if (!base.contains(auxEdge))
				{
					base.add(auxEdge);
					auxEdge.setChildrenVisited(true);
				}
			}
		}

		for (Node node : nodes)
			if (!node.visited)
			{
				parents.add(node.tail);
				node.visited = false;

			}

		for (HyperEdge edge : base)
		{
			edge.setChildrenVisited(false);
		}

		return parents;
	}
	
	/**
	 * El metodo <code>getParentNodes</code> devuelve el conjunto de nodos padre para un dado conjunto de ejes.
	 * No se agregan los nodos padres de los ejes hijos del nodo inicio (ejes superiores).
	 *
	 * @param set - conjunto de ejes
	 * @return conjunto de nodos padre
	 */
	private static HashSet<Node> getParentNodes(EdgeSet set)
	{
		HashSet<Node> nodes = new HashSet<Node>();

		for (HyperEdge edge : set)
		{
			if (!edge.isTop)
			{
				nodes.addAll(edge.tail);
			}
		}
		return nodes;
	}

	/**
	 * La funcion <code>generateCombinations</code>, dado un conjunto de nodos, devuelve todas las posibles combinaciones
	 * de ejes padre que habilitan a dichos nodos.  Ésto se logra llamando a otras funciones, 
	 * como <code>parentCombinations</code>.
	 * 
	 * @param nodes - Conjunto de nodos
	 * @return Conjunto de Conjuntos de ejes padre
	 */
	private static HashSet<EdgeSet> generateCombinations(HashSet<Node> nodes)
	{

		HashSet<HyperEdge> base = new HashSet<HyperEdge>();

		List<List<HyperEdge>> parents = generateParents(nodes, base);

		HyperEdge[] aux = new HyperEdge[parents.size()];

		HashSet<EdgeSet> combinations = new HashSet<EdgeSet>();

		parentCombinations(parents, 0, aux, combinations, base);

		return combinations;
	}

	/**
	 * El metodo recursivo <code>parentCombinations</code> se encarga de generar combinaciones distintas de ejes para un
	 * conjunto de nodos dado.  En vez de recibir un conjunto de nodos, la funcion recibe directamente una lista de lista de 
	 * ejes (proveniente de <code>generateParents</code>), en donde cada elemento representa la lista de ejes padres de cada nodo.
	 * 
	 * <p>Las combinaciones se generan recursivamente, eligiendo un solo eje padre por cada nodo.  Una vez que se llega al caso base
	 * , se tiene en la variable <code>combination</code> un eje padre por cada nodo, lo cual termina siendo una combinacion de 
	 * ejes padre que habilita a todos los nodos analizados previamente.  Las combinaciones se guardan en <code>combinations.</code>
	 * 
	 * @param parents - Lista de lista de ejes (contiene ejes padre de cada nodo)
	 * @param index - indice de posicion en array <code>combination</code>
	 * @param combination - array donde se arman las combinaciones temporalmente
	 * @param combinations - HashSet donde se guardan las combinaciones generadas
	 * @param base - conjunto de hiperejes que aparacen en todas las combinaciones necesariamente
	 */
	private static void parentCombinations(
			List<List<HyperEdge>> parents, int index,
			HyperEdge[] combination, HashSet<EdgeSet> combinations,
			HashSet<HyperEdge> base)
	{
		if (index == parents.size())
		{
			EdgeSet aux = new EdgeSet(combination);
			aux.addBase(base);

			combinations.add(aux);
			return;
		}

		List<HyperEdge> edges = parents.get(index);

		for (HyperEdge edge : edges)
		{

			combination[index] = edge;

			parentCombinations(parents, index + 1, combination, combinations,
					base);
		}
	}

	private static void markPath(EdgeSet set)
	{
		if (set == null)
		{
			return;
		}

		for (HyperEdge edge : set)
		{
			edge.visited = true;
			for (Node node : edge.tail)
			{
				node.visited = true;
			}
		}

		markPath(set.parent);

	}
}
