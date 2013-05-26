package back;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;

public class GraphSaver {

    public static void toDOT(HyperGraph hGraph) throws IOException {

	FileWriter fileOutput = new FileWriter(hGraph.name + ".dot");
	BufferedWriter writer = new BufferedWriter(fileOutput);

	writer.write("digraph  {");

	for (Node node : hGraph.nodes) {
	    writer.write(node.name + " [label=\"" + node.name + "\"]");
	    writer.newLine();

	}

	for (HyperEdge edge : hGraph.hEdges) {
	    writer.write(edge.name
		    + " [shape=box, height=0.18, fontsize=12, label=\""
		    + edge.name + " ( " + edge.weight + " )" + "\"]");
	    writer.newLine();

	    for (Node node : edge.heads) {
		writer.write(node.name + "->" + edge.name);
		writer.newLine();
	    }

	    for (Node node : edge.tails) {
		writer.write(edge.name + "->" + node.name);
		writer.newLine();
	    }

	}
	writer.write("}");
	writer.close();
    }

    public static void toDOT(HyperGraph hGraph, HyperGraph subgraph)
	    throws IOException {

	FileWriter fileOutput = new FileWriter(hGraph.name + ".min.dot");
	BufferedWriter writer = new BufferedWriter(fileOutput);

	writer.write("digraph  {");

	for (Node node : hGraph.nodes) {
	    if (subgraph.nodes.contains(node)) {
		writer.write(node.name + " [color = red,label=\"" + node.name + "\"]");
	    } else {
		writer.write(node.name + " [ label=\"" + node.name + "\"]");
	    }
	    writer.newLine();

	}

	for (HyperEdge edge : hGraph.hEdges) {
	    if (subgraph.hEdges.contains(edge)) {
		writer.write(edge.name
			+ " [color= red, shape=box, height=0.18, fontsize=12, label=\""
			+ edge.name + " ( " + edge.weight + " )" + "\"]");
	    } else {
		writer.write(edge.name
			+ " [shape=box, height=0.18, fontsize=12, label=\""
			+ edge.name + " ( " + edge.weight + " )" + "\"]");
	    }
	    writer.newLine();

	    for (Node node : edge.heads) {
		if (subgraph.hEdges.contains(edge)) {
		    writer.write(node.name + "->" + edge.name
			    + "[style=bold, color=red]");
		} else {
		    writer.write(node.name + "->" + edge.name);
		}
		writer.newLine();
	    }

	    for (Node node : edge.tails) {
		if (subgraph.nodes.contains(node)) {
		    writer.write(edge.name + "->" + node.name
			    + "[style=bold, color=red]");
		} else {
		    writer.write(edge.name + "->" + node.name);
		}
		writer.newLine();
	    }

	}
	writer.write("}");
	writer.close();
    }
}
