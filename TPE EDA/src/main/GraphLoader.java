package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import main.HyperGraph.HyperEdge;
import main.HyperGraph.Node;

public class GraphLoader
{
	static String errorMessageLength = "Nombres deben tener longitud 1-10.";
	static String errorMessagePattern = "Nombres deben ser alfanumericos.";
	static String errorMessageFormat = "Archivo mal formado.";
	static String errorMessageNumbers = "La cantidad de tags no concuerda con el numero especificado.";
	static String errorMessageFile = "Archivo debe ser formato .hg";

	static String alphaNumericPattern = "^[a-zA-Z0-9._]*$";

	final static int minTagLength = 1, maxTagLength = 10;
	final static int minTagsPerLine = 6;

	static public HyperGraph loadGraph(String filename) throws IOException,
			FileNotFoundException
	{
		if (!filename.endsWith(".hg"))
			throw new FileNotFoundException(errorMessageFile);

		FileReader fileinput = new FileReader(filename);
		BufferedReader reader = new BufferedReader(fileinput);

		String graphName = filename.substring(0, filename.length() - 3);

		LinkedList<String> lineTags;

		HashMap<String, Node> nodes = new HashMap<String, Node>();

		Node start = null, end = null;

		int linesRead = 0;

		while (reader.ready() && linesRead != 2)
		{
			String line = reader.readLine();

			if (line.startsWith("#")) continue;

			linesRead++;
			
			if (linesRead == 1)
				start = new Node(parseSingleTag(line));
			else if (linesRead == 2)
				end = new Node(parseSingleTag(line));
		}

		HyperGraph graph = new HyperGraph(graphName, start, end);

		nodes.put(start.getName(), start);
		nodes.put(end.getName(), end);

		while (reader.ready())
		{
			String line = reader.readLine();
			
			if (line.isEmpty()) 
				throw new IOException(errorMessageFormat);

			if (line.startsWith("#")) continue;

			lineTags = parseMultipleTags(line);
			Iterator<String> iterator = lineTags.iterator();

			String edgeName = iterator.next();
			double edgeWeight = Double.valueOf(iterator.next());

			HyperEdge edge = new HyperEdge(edgeName, edgeWeight);

			Node aux;
			
			String next = iterator.next();
			
			if (!next.matches("[0-9]+"))
				throw new IOException("Error: " + next + " no es un numero.");

			int exitCount = Integer.valueOf(next);

			for (; exitCount > 0; exitCount--)
			{
				if (!iterator.hasNext())
					throw new IOException(errorMessageNumbers);
				
				String nodeName = iterator.next();

				if (nodes.containsKey(nodeName))
					aux = nodes.get(nodeName);
				else
				{
					aux = new Node(nodeName);

					nodes.put(nodeName, aux);
					graph.nodes.add(aux);
				}

				edge.head.add(aux);
			}

			for (Node node : edge.head)
			{
				node.tail().add(edge);
			}

			next = iterator.next();
			
			if (!next.matches("[0-9]+"))
				throw new IOException("Error: " + next + " no es un numero.");
			
			int entryCount = Integer.valueOf(next);

			for (; entryCount > 0; entryCount--)
			{
				if (!iterator.hasNext())
					throw new IOException(errorMessageNumbers);
				
				String nodeName = iterator.next();

				if (nodes.containsKey(nodeName))
					aux = nodes.get(nodeName);
				else
				{
					aux = new Node(nodeName);

					nodes.put(nodeName, aux);
					graph.nodes.add(aux);
				}

				edge.tail.add(aux);

				if (aux == start) 
					edge.isTop = true;
			}

			for (Node node : edge.tail)
			{
				node.head().add(edge);
			}

			graph.hEdges.add(edge);
		}
		
		reader.close();
		fileinput.close();

		return graph;
	}

	private static String parseSingleTag(String line) throws IOException
	{
		if (line.length() < minTagLength || line.length() > maxTagLength)
			throw new IOException(errorMessageLength);
		
		if (!line.matches(alphaNumericPattern))
			throw new IOException("Error en: \"" + line + "\": " + errorMessagePattern);
		
		return line;
	}

	private static LinkedList<String> parseMultipleTags(String lineString) throws IOException
	{
		LinkedList<String> tags = new LinkedList<String>();
		char[] line = lineString.toCharArray();
		StringBuilder current = new StringBuilder();
		char lastChar = 0;

		for (char ch : line)
		{
			switch (ch)
			{
				case ' ':
					if (lastChar == ' ')
						throw new IOException(errorMessageFormat);
					
					String aux = current.toString();
					
					if (aux.length() < minTagLength || aux.length() > maxTagLength)
						throw new IOException(errorMessageLength);

					if (!aux.matches(alphaNumericPattern))
						throw new IOException("Error en: \"" + aux + "\": " + errorMessagePattern);
					
					tags.add(aux);
					current = new StringBuilder();
				break;

				default:
					current.append(ch);
				break;
			}
			
			lastChar = ch;
		}
		
		tags.add(current.toString());

		if (lastChar == ' ' || tags.size() < minTagsPerLine)
			throw new IOException(errorMessageFormat);
		
		return tags;
	}
}
