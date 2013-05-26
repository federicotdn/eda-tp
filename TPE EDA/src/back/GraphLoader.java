package back;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import back.HyperGraph.Node;

public class GraphLoader
{
	static public HyperGraph loadGraph(String filename) throws IOException, FileNotFoundException
	{
		if (!filename.endsWith(".hg"))
			throw new FileNotFoundException();
		
		FileReader fileinput = new FileReader(filename);
		BufferedReader reader = new BufferedReader(fileinput);
		
		Node start;
		Node end;
		
		String firstLine, secondLine;
		firstLine = reader.readLine();
		secondLine = reader.readLine();
		
		LinkedList<String> aux = parseLine(firstLine);
		
		
		
		
		while (reader.ready())
		{
			String line = reader.readLine();
			
			
			
		}
		
		return null;
	}

	public static LinkedList<String> parseLine(String lineString) throws IOException
	{
		LinkedList<String> tags= new LinkedList<String>();
		StringBuilder current = null;
		char[] line = lineString.toCharArray();
		boolean inTag = false;
		
		String errorMessage = "Archivo mal formado.";
		
		for (char ch : line)
		{
			switch (ch)
			{
				case '<':
					if (inTag)
						throw new IOException(errorMessage);
					inTag = true;
					current = new StringBuilder();
				break;
				
				case '>':
					if (!inTag)
						throw new IOException(errorMessage);
					inTag = false;
					tags.add(current.toString());
				break;
				
				default:
					if (!inTag)
						throw new IOException(errorMessage);
					current.append(ch);
				break;
			}
		}
		
		if (inTag)
			throw new IOException(errorMessage);
		
		return tags;
		
	}
}
