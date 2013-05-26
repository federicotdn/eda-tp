package back;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class GraphLoader
{
	static String errorMessageLength = "Nombres deben tener longitud 1-10.";
	static String errorMessagePattern = "Nombres deben ser alfanumericos.";
	static String errorMessageFormat = "Archivo mal formado.";
	static String errorMessageFile = "Archivo debe ser formato .hg";
	
	static String alphaNumericPattern = "^[a-zA-Z0-9]*$";
	
	final static int minTagLength = 1, maxTagLength = 10;
	final static int minTagsPerLine = 6;
	
	static public HyperGraph loadGraph(String filename) throws IOException, FileNotFoundException
	{
		if (!filename.endsWith(".hg"))
			throw new FileNotFoundException(errorMessageFile);
		
		FileReader fileinput = new FileReader(filename);
		BufferedReader reader = new BufferedReader(fileinput);
		
		String graphName = filename.substring(0, filename.length() - 3);
		
		String line;
		LinkedList<String> lineTags;
		
		HashMap<String, Node> nodes = new HashMap<String, Node>();
		
		Node start;
		Node end;
		
		String firstLine, secondLine;
		firstLine = reader.readLine();
		secondLine = reader.readLine();
	
		start = new Node(parseSingleTag(firstLine));
		end = new Node(parseSingleTag(secondLine));
		
		HyperGraph graph = new HyperGraph(start, end);
		
		graph.name = graphName;
		
		nodes.put(start.name, start);
		nodes.put(end.name, end);
		
		while (reader.ready())
		{
			line = reader.readLine();
			lineTags = parseMultipleTags(line);
			Iterator<String> iterator = lineTags.iterator();
			
			String edgeName = iterator.next();
			double edgeWeight = Double.valueOf(iterator.next());
			
			int entryCount = Integer.valueOf(iterator.next());
			
			HyperEdge edge = new HyperEdge(edgeName, entryCount, edgeWeight);
			
			Node aux;
			
			for (; entryCount > 0; entryCount--)
			{
				String nodeName = iterator.next();
				
				if (nodes.containsKey(nodeName))
					aux = nodes.get(nodeName);
				else
				{
					aux = new Node(nodeName);
					
					nodes.put(nodeName, aux);			
					graph.nodes.put(aux.name, aux);
				}
				
				edge.heads.add(aux);
			}
			
			edge.prepareParentNodes();
			
			int exitCount = Integer.valueOf(iterator.next());
			
			for (; exitCount > 0; exitCount--)
			{
				String nodeName = iterator.next();
				
				if (nodes.containsKey(nodeName))
					aux = nodes.get(nodeName);
				else
				{
					aux = new Node(nodeName);
					
					nodes.put(nodeName, aux);
					graph.nodes.put(aux.name, aux);
				}
				
				edge.tails.add(aux);
			}
			
			graph.hEdges.put(edge.name, edge);
		}
		
		return graph;
	}
	
	private static String parseSingleTag(String line) throws IOException
	{
		if (!line.startsWith("<") || !line.endsWith(">"))
			throw new IOException(errorMessageFormat);
		
		if (line.length() < minTagLength + 2 || line.length() > maxTagLength + 2)
			throw new IOException(errorMessageLength);
		
		String content = line.substring(1, line.length() - 1);
		if (!content.matches(alphaNumericPattern))
			throw new IOException(errorMessagePattern);
		
		return content;
	}

	private static LinkedList<String> parseMultipleTags(String lineString) throws IOException
	{
		LinkedList<String> tags= new LinkedList<String>();
		StringBuilder current = null;
		char[] line = lineString.toCharArray();
		boolean inTag = false;
	
		
		for (char ch : line)
		{
			switch (ch)
			{
				case '<':
					if (inTag)
						throw new IOException(errorMessageFormat);
					inTag = true;
					current = new StringBuilder();
				break;
				
				case '>':
					String aux = current.toString();
					
					if (!inTag)
						throw new IOException(errorMessageFormat);
					
					if(aux.length() < minTagLength || aux.length() > maxTagLength)
						throw new IOException(errorMessageLength);
					
					if (!aux.matches(alphaNumericPattern))
						throw new IOException(errorMessagePattern);
					
					inTag = false;
						
					tags.add(aux);
				break;
				
				default:
					if (!inTag)
						throw new IOException(errorMessageFormat);
					current.append(ch);
				break;
			}
		}
		
		if (inTag || tags.size() < minTagsPerLine)
			throw new IOException(errorMessageFormat);
		
		return tags;
	}
}
