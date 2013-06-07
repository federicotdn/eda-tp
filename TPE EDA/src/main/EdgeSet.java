package main;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

/**
 * La clase EdgeSet modela un conjunto de ejes de un hipergrafo.  Se utiliza en el algoritmo exacto para manipular
 * grupos de ejes.
 * 
 * <p>EdgeSet posee tres variables: <code>edges</code>, un HashSet donde se almacenan los ejes del conjunto,
 *  <code>totalWeight</code>, que representa el peso sumado de los ejes del conjunto mas el peso de <code>parent</code>, la 
 *  tercera variable, que representa al conjunto de ejes superior.
 *
 */
public class EdgeSet implements Iterable<HyperEdge>
{
	public HashSet<HyperEdge> edges;
	public double totalWeight;
	public EdgeSet parent;

	
	/**
	 * Éste constructor de EdgeSet recibe un array de HyperEdge, y simplemente agrega los contenidos a su
	 * variable <code>edges</code>, calculando el peso restante a la vez.
	 * 
	 * @param edges - Conjunto de HyperEdges a agregar al conjunto.
	 */
	public EdgeSet(HyperEdge[] edges)
	{
		this.edges = new HashSet<HyperEdge>();

		for (HyperEdge e : edges)
			if (this.edges.add(e)) this.totalWeight += e.weight;
	}

	/**
	 * 
	 * Éste constructor de EdgeSet es similar al anterior, pero recibe un solo HyperEdge en vez de un conjunto.
	 * 
	 * @param edge - Eje unico a agregar.
	 */
	public EdgeSet(HyperEdge edge)
	{
		edges = new HashSet<HyperEdge>();
		edges.add(edge);
		totalWeight = edge.weight;
	}
	
	/**
	 * El metodo <code>setParent</code> establece cuál es el conjunto de ejes pariente de el conjunto actual.  Al establecer
	 * el pariente, el EdgeSet actual suma a su propio peso total el peso del pariente.  De esta forma, si se forma una cadena
	 * de EdgeSet, el conjunto inferior tendra el peso acumulado de toda la cadena.
	 * 
	 * @param parent
	 */
	public void setParent(EdgeSet parent)
	{
		if(parent != null)
		{
			totalWeight += parent.totalWeight;
			this.parent = parent;
		}
	}
	

	/**
	 * Funcion de utilidad que permite iterar sobre el EdgeSet.
	 */
	@Override
	public Iterator<HyperEdge> iterator()
	{
		return edges.iterator();
	}
	
	/**
	 * El hashCode de EdgeSet es simplemente el hashCode del HashSet interno.
	 */
	@Override
	public int hashCode()
	{
		return edges.hashCode();
	}

	/**
	 * El metodo <code>equals</code> delega la responsabilidad de comparacion al HashSet interno.
	 */
	@Override
	public boolean equals(Object obj)
	{
		EdgeSet aux = (EdgeSet) obj;
		return edges.equals(aux.edges);
	}

	/**
	 * Metodo de utilidad para debug.
	 */
	@Override
	public String toString()
	{
		return edges.toString() + " " + totalWeight;
	}

	/**
	 * El metodo <code>addBase</code> permite agregar mas ejes de tipo HyperEdge a el conjunto actual.  Se comprueba
	 * que los ejes agregados no estén ya en el conjunto, y se aumenta el peso total adecuadamente.
	 * 
	 * @param base - Conjunto de ejes a agregar.
	 */
	public void addBase(HashSet<HyperEdge> base)
	{
		for (HyperEdge edge : base)
			if (this.edges.add(edge)) 
				this.totalWeight += edge.weight;
	}

}
