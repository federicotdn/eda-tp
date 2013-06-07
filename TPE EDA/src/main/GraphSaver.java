package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

public class GraphSaver
{
	public static void toDOT(HyperGraph hGraph) throws IOException
	{

		FileWriter fileOutput = new FileWriter(hGraph.name + ".dot");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		writer.write("digraph  {");
		writer.newLine();

		for (Node node : hGraph.nodes())
		{
			writer.write("\"" + node.name + "\"[label=\"" + node.name + "\"];");
			writer.newLine();

		}

		for (HyperEdge hEdge : hGraph.edges())
		{
			writer.write("\"" + hEdge.name
					+ "\"[shape=box, height=0.18, fontsize=12, label=\""
					+ hEdge.name + " ( " + hEdge.weight() + " )" + "\"];");
			writer.newLine();

			for (Node node : hEdge.head())
			{
				writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\";");
				writer.newLine();
			}
			for (Node node : hEdge.tail())
			{
				writer.write("\"" + node.name + "\"->\"" + hEdge.name + "\";");
				writer.newLine();
			}
		}

		writer.write("}");
		writer.close();
		System.out.println("Archivo creado: " + hGraph.name + ".dot");
	}
	
	public static void toDOTwithPath(HyperGraph hGraph) throws IOException
	{

		FileWriter fileOutput = new FileWriter(hGraph.name + ".dot");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		
		
		
		writer.write("digraph  {");
		writer.newLine();
		
		HashSet<Node> pathNodes = new HashSet<Node>();
		
		Node start = hGraph.start();
		Node end = hGraph.end();
		
		pathNodes.add(end);
		
		writer.write("\"" + start.name + "\"[color=red label=\"" + start.name + "\"];");
		writer.newLine();
		writer.write("\"" + end.name + "\"[color=red label=\"" + end.name + "\"];");
		writer.newLine();
		
		for (HyperEdge edge : hGraph.edges())
		{
			if (edge.getVisited())
				pathNodes.addAll(edge.tail());
		}
		
		for (Node node : hGraph.nodes())
		{
			if (!pathNodes.contains(node))
				writer.write("\"" + node.name + "\"[label=\"" + node.name + "\"];");
			else
				writer.write("\"" + node.name + "\"[color=red label=\"" + node.name + "\"];");
			writer.newLine();

		}

		for (HyperEdge hEdge : hGraph.edges())
		{
			if (!hEdge.getVisited())
			{
				writer.write("\"" + hEdge.name
						+ "\"[shape=box, height=0.18, fontsize=12, label=\""
						+ hEdge.name + " ( " + hEdge.weight() + " )" + "\"];");
				writer.newLine();
	
				for (Node node : hEdge.head())
				{
					writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\";");
					writer.newLine();
				}
				for (Node node : hEdge.tail())
				{
					writer.write("\"" + node.name + "\"->\"" + hEdge.name + "\";");
					writer.newLine();
				}
			}
			else
			{
				writer.write("\"" + hEdge.name
						+ "\"[color=red shape=box, height=0.18, fontsize=12, label=\""
						+ hEdge.name + " ( " + hEdge.weight() + " )" + "\"];");
				writer.newLine();
				
				for (Node node : hEdge.tail())
				{
					writer.write("\"" + node.name + "\"->\"" + hEdge.name + "\"[style=bold, color=red];");
					writer.newLine();
				}
				
				for (Node node : hEdge.head())
				{
					if (!pathNodes.contains(node))					
						writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\";");
					else
						writer.write("\"" + hEdge.name + "\"->\"" + node.name + "\"[style=bold, color=red];");
					writer.newLine();
				}
			}
		}

		writer.write("}");
		writer.close();
		System.out.println("Archivo creado: " + hGraph.name + ".dot");
	}
}
