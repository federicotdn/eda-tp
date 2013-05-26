package back;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import back.HyperGraph.Node;

public class GraphLoader
{
	public static void main(String[] args) throws IOException
	{
		System.out.println(parseSingleTag("<holah3234c>"));
		
		FileReader fileinput = new FileReader("pinchador.hg");
		BufferedReader reader = new BufferedReader(fileinput);
		
		LinkedList<String> aux = null;
		
		while (reader.ready())
		{
			aux = parseMultipleTags(reader.readLine());
			
			for (String s : aux)
				System.out.println(s);
			System.out.println("-------FINAL LINEA-------");
		}
	}
	
	static String errorMessageLength = "Nombres deben tener longitud 1-10.";
	static String errorMessagePattern = "Nombres deben ser alfanumericos.";
	static String errorMessageFormat = "Archivo mal formado.";
	
	static String alphaNumericPattern = "^[a-zA-Z0-9]*$";
	
	final static int minTagLength = 1, maxTagLength = 10;
	
	static public HyperGraph loadGraph(String filename) throws IOException, FileNotFoundException
	{
		if (!filename.endsWith(".hg"))
			throw new FileNotFoundException();
		
		FileReader fileinput = new FileReader(filename);
		BufferedReader reader = new BufferedReader(fileinput);
		
		HashMap<String, Node> nodes = new HashMap<String, Node>();
		
		Node start;
		Node end;
		
		String firstLine, secondLine;
		firstLine = reader.readLine();
		secondLine = reader.readLine();
	
		start = new Node(parseSingleTag(firstLine));
		end = new Node(parseSingleTag(secondLine));
		
		
		
		
		while (reader.ready())
		{
			String line = reader.readLine();
			
			
			
		}
		
		return null;
	}
	
	public static String parseSingleTag(String line) throws IOException
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

	public static LinkedList<String> parseMultipleTags(String lineString) throws IOException
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
		
		if (inTag)
			throw new IOException(errorMessageFormat);
		
		return tags;
		
	}
}
