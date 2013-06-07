package main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

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
	
	public static void toHGpathOnly(HyperGraph graph) throws IOException
	{
		FileWriter fileOutput = new FileWriter(graph.name + ".hg");
		BufferedWriter writer = new BufferedWriter(fileOutput);
		
		writer.write("# Subgrafo de: " + graph.name);
		writer.newLine();
		
		writer.write(graph.start().name);
		writer.newLine();
		
		writer.write(graph.end().name);
		writer.newLine();

		
		for (HyperEdge edge : graph.edges())
		{		
			if (edge.visited)
			{
				StringBuffer line = new StringBuffer();
				
				line.append(edge.name + " " + edge.weight() + " ");
				line.append(edge.head().size());
				
				for (Node node : edge.head())
					if (node.visited)
					line.append(node.name + " ");
				
				line.append(" " + edge.tail().size());
				
			}
		}
		
		for (HyperEdge edge : graph.edges())
		{
			StringBuffer line = new StringBuffer();
			
			line.append(edge.name + " " + edge.weight() + " ");
			line.append(edge.head().size() + " ");
			
			for (Node node : edge.head())
				line.append(node.name + " ");
			
			int entryCount = edge.tail().size();
			line.append(entryCount + " ");
			
			int nodeNumber = 1;
			
			for (Node node : edge.tail())
			{
				line.append(node.name);
				
				if (nodeNumber++ < entryCount)
					line.append(" ");
			}
			
			writer.write(line.toString());
			
		}
		
		System.out.println("Archivo: " + graph.name + ".hg creado.");
		writer.close();
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
