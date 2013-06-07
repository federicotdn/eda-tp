package main;

import java.util.HashSet;
import java.util.Iterator;

import main.HyperGraph.HyperEdge;

/**
 * La clase EdgePath modela un camino conformado de ejes de tipo HyperEdge.
 * 
 * La clase cuenta con dos variables: <code>path</code>, un HashSet donde se guardan los ejes que componen el
 * camino, y <code>totalWeight</code>, donde se guarda el peso total del camino sumado.
 * 
 * Ésta clase se utiliza en el algoritmo aproximado, ya que permite guardar en cada eje informacion que representa
 * un camino valido posible que lleva a dicho eje.
 * 
 */
public class EdgePath implements Iterable<HyperEdge>
{
	public HashSet<HyperEdge> path;
	public double totalWeight;
	
	/**
	 * El unico constructor de EdgePath recibe un eje inicial que forma parte del camino.
	 * 
	 * @param initial - Primer eje que forma parte del camino.
	 */
	public EdgePath(HyperEdge initial)
	{
		path = new HashSet<HyperEdge>();
		totalWeight = 0;
		addEdge(initial);
	}
	
	/**
	 * Éste metodo agrega un eje al camino representado por el EdgePath.  Se suma al peso total del camino
	 * el peso del eje agregado.
	 * 
	 * @param edge - Eje a agregar al camino.  Si el eje ya estaba en el camino, se ignora.
	 */
	public void addEdge(HyperEdge edge)
	{
		if (path.add(edge))
			totalWeight += edge.weight;
	}
	
	/**
	 * El metodo <code>mergeWith</code> recibe otro EdgePath, y lo combina con el EdgePath actual.  Para llevar a cabo
	 * esto, simplemente se recorren los ejes HyperEdge del camino <code>other</code>, agregando cada eje al camino actual.
	 * Si se agrega un eje al camino, se suma al peso total el peso del eje, y si el eje ya estaba contenido en el EdgePath
	 * actual entonces se ignora.
	 * 
	 * @param other
	 */
	public void mergeWith(EdgePath other)
	{
		for (HyperEdge edge : other)
		{
			if (path.add(edge))
				totalWeight += edge.weight;
		}
	}
	
	/**
	 * Funcion de utilidad que permite iterar sobre los ejes contenidos dentro del EdgePath.
	 */
	@Override
	public Iterator<HyperEdge> iterator()
	{
		return path.iterator();
	}

}
