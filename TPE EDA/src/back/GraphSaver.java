package back;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class GraphSaver
{

	public static void toDOT(HyperGraph hGraph) throws IOException
	{

		FileWriter fileOutput = new FileWriter(hGraph.name + ".dot");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		int i = 0;
		writer.write("digraph  {".toCharArray());
		writer.newLine();

		for (Node node : hGraph.nodes.values())
		{
			writer.write("\"" + node.name + "\"[label=\"" + node.name + "\"];");
			writer.newLine();

		}

		for (HyperEdge hEdge : hGraph.hEdges.values())
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

	public static void toDOT(HyperGraph hGraph, HyperGraph subgraph)
			throws IOException
	{

		FileWriter fileOutput = new FileWriter(hGraph.name + ".min.dot");
		BufferedWriter writer = new BufferedWriter(fileOutput);

		writer.write("digraph  {");
		writer.newLine();

		for (Node node : hGraph.nodes.values())
		{
			if (subgraph.nodes.containsKey(node.name))
			{
				writer.write("\"" + node.name + "\"[color = red,label=\""
						+ node.name + "\"];");
			} else
			{
				writer.write(("\"" + node.name + "\"" + "[label=\"" + node.name + "\"];"));
			}
			writer.newLine();

		}

		for (HyperEdge hEdge : hGraph.hEdges.values())
		{
			if (subgraph.hEdges.containsKey(hEdge.name))
			{
				writer.write("\""
						+ hEdge.name
						+ "\"[color= red, shape=box, height=0.18, fontsize=12, label=\""
						+ hEdge.name + " ( " + hEdge.weight + " )   "
						+ hEdge.distance + "\"];");
			} else
			{
				writer.write("\"" + hEdge.name
						+ "\"[shape=box, height=0.18, fontsize=12, label=\""
						+ hEdge.name + " ( " + hEdge.weight + " )   "
						+ hEdge.distance + "\"];");
			}
			writer.newLine();

			for (Node node : hEdge.head)
			{
				if (subgraph.nodes.containsKey(node.name)
						&& subgraph.hEdges.containsKey(hEdge.name))
				{
					writer.write("\"" + hEdge.name + "\"->\"" + node.name
							+ "\"" + "[style=bold, color=red];");
				} else
				{
					writer.write("\"" + hEdge.name + "\"->\"" + node.name
							+ "\";");
				}
				writer.newLine();
			}

			for (Node node : hEdge.tail)
			{
				if (subgraph.hEdges.containsKey(hEdge.name))
				{
					writer.write("\"" + node.name + "\"->\"" + hEdge.name
							+ "\"" + "[style=bold, color=red];");
				} else
				{
					writer.write("\"" + node.name + "\"->\"" + hEdge.name
							+ "\";");
				}
				writer.newLine();
			}
		}

		writer.write("}");
		writer.close();
		System.out.println("Archivo creado: " + hGraph.name + ".min.dot");
	}

	public static void toTXT(HyperGraph hGraph) throws IOException
	{
		FileWriter fileOutput = new FileWriter(hGraph.name + ".txt");
		BufferedWriter writer = new BufferedWriter(fileOutput);

		for (Node node : hGraph.nodes.values())
		{
			writer.write("nodo:     " + node.name);
			writer.newLine();

			for (HyperEdge edge : node.destinationEdges)
			{
				writer.write("\t" + edge.name);
				writer.newLine();
			}
		}

		for (HyperEdge edge : hGraph.hEdges.values())
		{
			writer.write("Hiper eje: " + edge.name);
			writer.newLine();
			writer.write("Colas: ");
			writer.newLine();

			for (Node node : edge.tail)
			{
				writer.write("\t" + node.name);
				writer.newLine();
			}

			writer.write("Cabezas: ");
			writer.newLine();

			for (Node node : edge.head)
			{
				writer.write("\t" + node.name);
				writer.newLine();
			}
		}

		System.out.println("txt creado");

		writer.close();
	}
}
