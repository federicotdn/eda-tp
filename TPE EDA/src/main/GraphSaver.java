package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

/**
 * La clase de GraphSaver esta compuesta por metodos estaticos, que permiten guardar grafos en distintos formatos.
 *
 */
public class GraphSaver
{
	/**
	 * El unico metodo publico de la clase recibe como parametro un objeto HyperGraph, y llama a las tres funciones
	 * restantes de la clase.  De esta forma se crean tres archivos: subgrafo en .hg, grafo entero en .dot, y grafo con camino
	 * en .dot.
	 * 
	 * 
	 * @param graph - Grafo a guardar.
	 * @throws IOException Si hay un error al crear el archivo.
	 */
	public static void createGraphFiles(HyperGraph graph) throws IOException
	{
		toDOT(graph);
		toDOTwithPath(graph);
		toHGpathOnly(graph);
	}
	
	/**
	 * Éste metodo recibe un objeto HyperGraph como parametro, y guarda el mismo en formato .dot.
	 * 
	 * @param hGraph - Grafo a guardar.
	 * @throws IOException IOException Si hay un error al crear el archivo.
	 */
	private static void toDOT(HyperGraph hGraph) throws IOException
	{

		FileWriter fileOutput = new FileWriter(hGraph.name + ".dot");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		writer.write("digraph  {");
		writer.newLine();

		for (Node node : hGraph.nodes)
		{
			writer.write("\"" + node.name + "\"[label=\"" + node.name + "\"];");
			writer.newLine();

		}

		for (HyperEdge hEdge : hGraph.hyperEdges)
		{
			writer.write("\"" + hEdge.name
					+ "\"[shape=box, height=0.18, fontsize=12, label=\""
					+ hEdge.name + " ( " + hEdge.weight + " )" + "\"];");
			writer.newLine();

			for (Node node : hEdge.head)
			{
				writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\";");
				writer.newLine();
			}
			for (Node node : hEdge.tail)
			{
				writer.write("\"" + node.name + "\"->\"" + hEdge.name + "\";");
				writer.newLine();
			}
		}

		writer.write("}");
		writer.close();
		System.out.println("Archivo creado: " + hGraph.name + ".dot");
	}
	
	/**
	 * 
	 * Éste metodo recibe un objeto HyperGraph, y guarda el camino recorrido como un subgrafo separado en formato .hg.
	 * 
	 * @param graph - Grafo a extraer el subgrafo.
	 * @throws IOException IOException Si hay un error al crear el archivo.
	 */
	private static void toHGpathOnly(HyperGraph graph) throws IOException
	{
		FileWriter fileOutput = new FileWriter(graph.name + ".min.hg");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		
		writer.write("# Subgrafo de: " + graph.name);
		writer.newLine();
		
		writer.write(graph.start.name);
		writer.newLine();
		
		writer.write(graph.end.name);
		writer.newLine();
		
		boolean first = true;
	
		for (HyperEdge edge : graph.hyperEdges)
		{		
			if (edge.visited)
			{
				if (first)
					first = false;
				else
					writer.newLine();
				
				StringBuffer line = new StringBuffer();
				
				line.append(edge.name + " " + edge.weight + " ");
				
				LinkedList<Node> entryNodes = new LinkedList<Node>();
				LinkedList<Node> exitNodes = new LinkedList<Node>();
				
				for (Node node : edge.head)
					if (node.visited)
						exitNodes.add(node);
				
				for (Node node : edge.tail)
					if (node.visited)
						entryNodes.add(node);
				
				line.append(exitNodes.size());
				
				for (Node node : exitNodes)
					line.append(" " + node.name);
				
				line.append(" " + entryNodes.size());
				
				for (Node node : entryNodes)
					line.append(" " + node.name);
				
				writer.write(line.toString());
			}
		}
		
		System.out.println("Archivo: " + graph.name + ".min.hg creado.");
		writer.close();
	}
	
	/**
	 * Éste metodo recibe un objeto de tipo HyperGraph, y guarda el mismo en formato .dot, pero con el camino recorrido
	 * marcado en rojo (ejes y nodos).
	 * 
	 * @param hGraph - Grafo a guardar.
	 * @throws IOException Si hay un error al crear el archivo.
	 */
	private static void toDOTwithPath(HyperGraph hGraph) throws IOException
	{

		FileWriter fileOutput = new FileWriter(hGraph.name + ".min.dot");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		
		writer.write("digraph  {");
		writer.newLine();
		
		Node start = hGraph.start;
		Node end = hGraph.end;
		
		writer.write("\"" + start.name + "\"[color=red label=\"" + start.name + "\"];");
		writer.newLine();
		writer.write("\"" + end.name + "\"[color=red label=\"" + end.name + "\"];");
		writer.newLine();
		
		for (Node node : hGraph.nodes)
		{
			if (!node.visited)
				writer.write("\"" + node.name + "\"[label=\"" + node.name + "\"];");
			else
				writer.write("\"" + node.name + "\"[color=red label=\"" + node.name + "\"];");
			writer.newLine();

		}


		for (HyperEdge hEdge : hGraph.hyperEdges)
		{
			if (!hEdge.visited)
			{
				writer.write("\"" + hEdge.name
						+ "\"[shape=box, height=0.18, fontsize=12, label=\""
						+ hEdge.name + " ( " + hEdge.weight + " )" + "\"];");
				writer.newLine();
	
				for (Node node : hEdge.head)
				{
					writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\";");
					writer.newLine();
				}
				for (Node node : hEdge.tail)
				{
					writer.write("\"" + node.name + "\"->\"" + hEdge.name + "\";");
					writer.newLine();
				}
			}
			else
			{
				writer.write("\"" + hEdge.name
						+ "\"[color=red shape=box, height=0.18, fontsize=12, label=\""
						+ hEdge.name + " ( " + hEdge.weight + " )" + "\"];");
				writer.newLine();
				
				for (Node node : hEdge.tail)
				{
					writer.write("\"" + node.name + "\"->\"" + hEdge.name + "\"[style=bold, color=red];");
					writer.newLine();
				}
				
				for (Node node : hEdge.head)
				{
					if (!node.visited)					
						writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\";");
					else
						writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\"[style=bold, color=red];");
					writer.newLine();
				}
			}
		}

		writer.write("}");
		writer.close();
		System.out.println("Archivo creado: " + hGraph.name + ".min.dot");
	}
}
