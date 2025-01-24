package DataStructuresAndAlgorithms;

/*  Written by Ali Egemen Bilak using VSCode and JDK 21.0.5 on Ubuntu 24.04.1 LTS
    https://github.com/ducktumn
    Last Edit: 18.12.2024
    Made for MEF University COMP201 final project.
*/

//Imports
import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Objects;

/* Tool to turn a properly formatted .csv into a graph representation
   and also modify it if needed. 
*/

public class MyGraph implements Cloneable {
    // Node Inner Class to store the "nodes"
    // Cities in this case
    public class Node implements Cloneable {
        private String contentOfNode;

        private Node(String contentOfNode) {
            if (contentOfNode == null || contentOfNode.isEmpty()) {
                System.out
                        .println("Content is empty. If this was unintentional we suggest retrying with a valid input.");
                throw new IllegalArgumentException("Node content cannot be empty! ");
            }
            this.contentOfNode = contentOfNode;

        }

        public String getContent() {
            return contentOfNode;
        }

        private void setContent(String contentOfNode) {
            this.contentOfNode = contentOfNode;
        }

        @Override
        protected Node clone() {
            try {
                return (Node) super.clone();
            } catch (CloneNotSupportedException cnse) {
                return null;
            }

        }

        @Override
        public boolean equals(Object nodeToCheck) {
            if (!(nodeToCheck instanceof Node))
                return false;
            else if (this == nodeToCheck)
                return true;
            else if (Objects.equals(this.contentOfNode, ((Node) nodeToCheck).getContent()))
                return true;
            else
                return false;

        }

        @Override
        public String toString() {
            return "Node Content: " + this.contentOfNode;
        }
    }

    // An Inner Class to store and modify the matrix representation
    private class AdjacencyMatrix implements Cloneable {
        private ArrayList<ArrayList<Double>> matrix = new ArrayList<>();
        private int nodeCount;
        private boolean isOriginal = false;

        private AdjacencyMatrix(int nodeCount, boolean isOriginal) {
            this.nodeCount = nodeCount;
            this.isOriginal = isOriginal;
            for (int i = 0; i < nodeCount; i++) {
                matrix.add(new ArrayList<>(Collections.nCopies(nodeCount, MyGraph.this.DEFAULT_INVALID_VALUE)));
            }
        }

        private AdjacencyMatrix(int nodeCount) {
            this.nodeCount = nodeCount;
            for (int i = 0; i < nodeCount; i++) {
                matrix.add(new ArrayList<>(Collections.nCopies(nodeCount, MyGraph.this.DEFAULT_INVALID_VALUE)));
            }
        }

        public boolean isOriginal() {
            return isOriginal;
        }

        // Used in the initilization phase
        private void addElement(int fromIndex, int toIndex, double distance) {
            matrix.get(fromIndex).set(toIndex, distance);
        }

        // Used after the initilization phase
        private void setElement(int from, int to, double distance) {
            if (from < nodeCount && from >= 0 && to < nodeCount && to >= 0)
                matrix.get(from).set(to, distance);
        }

        // Returns the distance between the Nodes in two rows
        private double getDistance(int from, int to) {
            return matrix.get(from - 1).get(to - 1);
        }

        // Used after adding a new Node to create space in the matrix
        private void refreshForNewNode() {
            for (ArrayList<Double> a : matrix)
                a.add(MyGraph.this.DEFAULT_INVALID_VALUE);
            matrix.add(new ArrayList<>(Collections.nCopies(nodeCount + 1, MyGraph.this.DEFAULT_INVALID_VALUE)));
            nodeCount++;
        }

        // Used after removing a Node to remove useless space in the matrix
        private void refreshForRemovedNode(int indexOfNode) {
            matrix.remove(indexOfNode);
            for (ArrayList<Double> a : matrix)
                a.remove(indexOfNode);
            this.nodeCount--;
        }

        // Returns the full matrix without cloning or copying it
        private ArrayList<ArrayList<Double>> getRawMatrix() {
            return this.matrix;
        }

        protected int getSize() {
            return this.nodeCount;
        }

        // Prints the matrix to the terminal with alternating colors
        public void printMatrixToTerminal() {
            if (this.isOriginal) {
                System.out.print("\u001B[31m");
                System.out.printf("%12c", '|');
                for (int i = 0; i < nodeCount; i++) {
                    System.out.printf(" %-10s |", MyGraph.this.originalNodes.get(i).getContent());
                }
                for (int j = 0; j < nodeCount; j++) {
                    System.out.printf("\n%-10s | ", MyGraph.this.originalNodes.get(j).getContent());
                    System.out.print("\u001B[33m");
                    for (int i = 0; i < nodeCount; i++) {
                        System.out.printf("%-10.3f | ", matrix.get(j).get(i));
                    }
                    System.out.print("\u001B[31m");
                }
                System.out.print("\u001B[0m");
            } else {
                System.out.print("\u001B[31m");
                System.out.printf("%12c", '|');
                for (int i = 0; i < nodeCount; i++) {
                    System.out.printf(" %-10s |", MyGraph.this.nodes.get(i).getContent());
                }
                for (int j = 0; j < nodeCount; j++) {
                    System.out.printf("\n%-10s | ", MyGraph.this.nodes.get(j).getContent());
                    System.out.print("\u001B[33m");
                    for (int i = 0; i < nodeCount; i++) {
                        System.out.printf("%-10.3f | ", matrix.get(j).get(i));
                    }
                    System.out.print("\u001B[31m");
                }
                System.out.print("\u001B[0m");
            }

        }

        @Override
        protected AdjacencyMatrix clone() {
            AdjacencyMatrix returnMatrix = new AdjacencyMatrix(this.nodeCount);
            if (MyGraph.this.isInitialized == false)
                returnMatrix = new AdjacencyMatrix(this.nodeCount, true);
            if (this.isOriginal) {
                for (int i = 0; i < MyGraph.this.originalNodes.size(); i++) {
                    for (int j = 0; j < MyGraph.this.originalNodes.size(); j++) {
                        returnMatrix.addElement(i, j, this.matrix.get(i).get(j));
                    }
                }
            } else {
                for (int i = 0; i < MyGraph.this.nodes.size(); i++) {
                    for (int j = 0; j < MyGraph.this.nodes.size(); j++) {
                        returnMatrix.addElement(i, j, this.matrix.get(i).get(j));
                    }
                }
            }

            return returnMatrix;

        }
    }

    // An Inner Class to store and modify the list representation
    private class AdjacencyList implements Cloneable {
        // An inner class of another inner class :D
        // Used to store links in the representation
        private class Link implements Cloneable {
            Node element;
            Node owner;
            double distance;

            public Link(Node element, Node owner, double distance) {
                this.distance = distance;
                this.element = element;
                this.owner = owner;
            }

            @Override
            protected Link clone() {
                return new Link(this.element, this.owner, this.distance);
            }
        }

        private boolean isOriginal = false;
        private ArrayList<LinkedList<Link>> list;
        private int nodeCount;

        private AdjacencyList(int nodeCount, boolean isOriginal) {
            this.nodeCount = nodeCount;
            this.isOriginal = isOriginal;

            list = new ArrayList<>();
            for (int i = 0; i < nodeCount; i++) {
                list.add(new LinkedList<>());
            }
        }

        private AdjacencyList(int nodeCount, ArrayList<LinkedList<Link>> list, boolean isOriginal) {
            this.nodeCount = nodeCount;
            this.isOriginal = isOriginal;
            this.list = list;
        }

        private AdjacencyList(int nodeCount) {
            this.nodeCount = nodeCount;

            list = new ArrayList<>();
            for (int i = 0; i < nodeCount; i++) {
                list.add(new LinkedList<>());
            }
        }

        public boolean isOriginal() {
            return isOriginal;
        }

        private void refreshForNewNode() {
            this.list.add(new LinkedList<>());
            nodeCount++;
        }

        private void refreshForRemovedNode(int indexToRemove, Node nodeToRemove) {
            this.list.remove(indexToRemove);
            for (LinkedList<Link> L : this.list)
                for (int i = 0; i < L.size(); i++)
                    if (L.get(i).element == nodeToRemove)
                        L.remove(i);
            nodeCount--;
        }

        public int getSize() {
            return nodeCount;
        }

        private void addLink(Node from, Node to, double distance) {

            if (distance != MyGraph.this.DEFAULT_INVALID_VALUE && distance != 0) {
                list.get(nodes.indexOf(from)).add(new Link(to, from, distance));
                list.get(nodes.indexOf(to)).add(new Link(from, to, distance));
            }

        }

        private void setLink(int from, Node to, double distance) {
            for (Link L : this.list.get(from - 1))
                if (L.element.equals(to))
                    L.distance = distance;
        }

        private void removeEdge(int from, Node to) {
            for (int i = 0; i < this.list.get(from - 1).size(); i++) {
                if (this.list.get(from - 1).get(i).element == to) {
                    this.list.get(from - 1).remove(i);
                }
            }
        }

        public void printListToTerminal() {
            String yellow = "\u001B[33m";
            String red = "\u001B[31m";

            if (this.isOriginal) {
                for (int i = 0; i < nodeCount; i++) {
                    if (i % 2 == 0)
                        System.out.print(yellow);
                    else
                        System.out.print(red);
                    System.out.printf("\n|%-11s|", MyGraph.this.originalNodes.get(i).getContent());
                    for (int j = 0; j < list.get(i).size(); j++) {
                        Link tempLink = list.get(i).get(j);
                        System.out.printf(" -----> %s (%-8.3f)", tempLink.element.getContent(), tempLink.distance);
                    }
                }
            } else {
                for (int i = 0; i < nodeCount; i++) {
                    if (i % 2 == 0)
                        System.out.print(yellow);
                    else
                        System.out.print(red);
                    System.out.printf("\n|%-11s|", MyGraph.this.nodes.get(i).getContent());
                    for (int j = 0; j < list.get(i).size(); j++) {
                        Link tempLink = list.get(i).get(j);
                        System.out.printf(" -----> %s (%-8.3f)", tempLink.element.getContent(), tempLink.distance);
                    }
                }
            }

            System.out.print("\u001B[0m");

        }

        private LinkedList<Link> getLinkedListOf(int index) {
            return list.get(index);
        }

        @Override
        protected AdjacencyList clone() {
            ArrayList<LinkedList<Link>> tempList = new ArrayList<>();

            for (int i = 0; i < this.nodeCount; i++) {
                tempList.add(new LinkedList<>());
                for (int j = 0; j < this.list.get(i).size(); j++)
                    tempList.get(i).add(this.list.get(i).get(j).clone());
            }

            boolean isCloneOriginal = false;
            if (!MyGraph.this.isInitialized)
                isCloneOriginal = true;

            return new AdjacencyList(this.nodeCount, tempList, isCloneOriginal);
        }
    }

    // Class Fields
    // Initial and modified states are both stored
    // Changed the invalid value from 99999.0 to -1 as it makes more sense
    // #region
    private LinkedList<Node> nodes = new LinkedList<>();
    private HashSet<String> nodeContents = new HashSet<>();
    private AdjacencyMatrix matrixRepresentation;
    private AdjacencyList listRepresentation;
    private int nodeCount;

    private int originalNodeCount;
    private LinkedList<Node> originalNodes = new LinkedList<>();
    private HashSet<String> originalNodeContents = new HashSet<>();
    private AdjacencyMatrix originalMatrixRepresentation;
    private AdjacencyList originalListRepresentation;

    private File csv;
    private boolean isInitialized = false;
    private final double DEFAULT_INVALID_VALUE = -1.0;
    // #endregion

    // Constructor
    // Exception should be handled by the caller
    public MyGraph(File csv) throws FileNotFoundException {
        this.csv = csv;
        initializeRepresentations(initializeNodes(this.csv), this.csv);

        for (Node i : nodes) {
            originalNodes.add(i.clone());
            originalNodeContents.add(i.getContent());
        }
        originalNodeCount = nodeCount;

        originalMatrixRepresentation = (AdjacencyMatrix) matrixRepresentation.clone();
        originalListRepresentation = (AdjacencyList) listRepresentation.clone();

        isInitialized = true;
    }

    // Used to get the node names from the .csv
    // Assumes a properly formatted file but still checks for some exceptions
    private HashSet<Integer> initializeNodes(File csv) throws FileNotFoundException {
        Scanner reader = new Scanner(csv);
        int currentLine = 1;
        HashSet<Integer> linesToSkip = new HashSet<>();
        linesToSkip.add(1);
        reader.nextLine();

        while (reader.hasNextLine()) {
            currentLine++;
            String rawLine = reader.nextLine();
            if (!rawLine.isBlank()) {
                String nodeValue = rawLine.split(",", 2)[0];
                if (!nodeValue.isBlank()) {
                    if (this.nodeContents.contains(nodeValue)) {
                        linesToSkip.add(currentLine);
                    } else {
                        this.nodes.add(new Node(nodeValue));
                        this.nodeContents.add(nodeValue);
                        this.nodeCount++;
                    }
                } else {
                    linesToSkip.add(currentLine);
                }
            } else {
                linesToSkip.add(currentLine);
            }
        }

        reader.close();
        return linesToSkip;
    }

    // Used to get the distances from the .csv
    // Works with the previous method and both are only executed once per object
    private void initializeRepresentations(HashSet<Integer> linesToSkip, File csv) throws FileNotFoundException {
        Scanner reader = new Scanner(csv);

        this.matrixRepresentation = new AdjacencyMatrix(nodeCount);
        this.listRepresentation = new AdjacencyList(nodeCount);
        HashSet<String> checkedHeaders = new HashSet<>();
        String[] rawHeaders = reader.nextLine().split(",");
        int currentLine = 2;

        while (reader.hasNextLine()) {
            if (!linesToSkip.contains(currentLine)) {
                checkedHeaders = new HashSet<>();
                String[] rawLine = reader.nextLine().split(",");
                String lineNode = rawLine[0];

                for (int i = 1; i < rawLine.length; i++) {

                    double distance = DEFAULT_INVALID_VALUE;
                    try {
                        distance = Double.parseDouble(rawLine[i]);
                        if (distance == 99999.0)
                            distance = DEFAULT_INVALID_VALUE;
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }

                    if (nodeContents.contains(rawHeaders[i]) && !checkedHeaders.contains(rawHeaders[i])) {
                        checkedHeaders.add(rawHeaders[i]);
                        matrixRepresentation.addElement(nodes.indexOf(new Node(lineNode)),
                                nodes.indexOf(new Node(rawHeaders[i])), distance);
                        listRepresentation.addLink(new Node(lineNode), new Node(rawHeaders[i]), distance);
                    }
                }
            }
            currentLine++;

        }

        reader.close();

    }

    // Returns the Node object corresponding to the String content inside
    public Node getNodeByName(String content) {
        if (this.nodeContents.contains(content))
            return this.nodes.get(this.nodes.indexOf(new Node(content)));
        return null;
    }

    // Returns the Node object in that row (uses row not index so 0 is invalid)
    public Node getNodeByLocation(int row) {
        if (this.nodeCount >= row && row > 0)
            return this.nodes.get(row - 1);
        return null;
    }

    // Returns the distance between two specified nodes
    public double getEdgeByNode(Node from, Node to) {
        double returnValue = -1;
        if (this.nodeContents.contains(from.getContent()) && this.nodeContents.contains(to.getContent()))
            returnValue = matrixRepresentation.getDistance(this.nodes.indexOf(from) + 1, this.nodes.indexOf(to) + 1);
        return returnValue;
    }

    // Returns the distance between two nodes in the specified rows
    public double getEdgeByLocation(int row, int col) {
        double returnValue = -1;
        if ((this.nodeCount >= row) && (this.nodeCount >= col) && (row > 0) && (col > 0))
            returnValue = this.matrixRepresentation.getDistance(row, col);
        return returnValue;
    }

    // Returns an array of Node objects that are adjacent to the one provided by the
    // caller
    public Node[] getNeighbours(Node rootNode) {
        int index;
        if (this.nodeContents.contains(rootNode.getContent()))
            index = this.nodes.indexOf(rootNode);
        else
            return null;
        LinkedList<AdjacencyList.Link> tempList = this.listRepresentation.getLinkedListOf(index);
        Node[] tempArray = new Node[tempList.size()];
        for (int i = 0; i < tempArray.length; i++)
            tempArray[i] = tempList.get(i).element;
        return tempArray;

    }

    // Changes the content of a Node
    public void setNode(Node toChange, String newContent) {
        if (this.nodeContents.contains(toChange.getContent()) && !this.nodeContents.contains(newContent)) {
            this.nodes.get(this.nodes.indexOf(toChange)).setContent(newContent);
            this.nodeContents.remove(toChange.getContent());
            this.nodeContents.add(newContent);
        }
    }

    // Adds a new Node
    public void addNode(String content) {
        if (!this.nodeContents.contains(content)) {
            this.nodes.add(new Node(content));
            this.nodeContents.add(content);
            this.matrixRepresentation.refreshForNewNode();
            this.matrixRepresentation.setElement(this.nodeCount, this.nodeCount, 0);
            this.listRepresentation.refreshForNewNode();
            this.nodeCount++;
        }
    }

    // Returns the node count
    public int size() {
        return this.nodeCount;
    }

    // Changes the distance between two Nodes
    public void setEdge(Node from, Node to, double newDistance) {
        double currentDistance = this.matrixRepresentation.getDistance(this.nodes.indexOf(from) + 1,
                this.nodes.indexOf(to) + 1);

        if (this.nodeContents.contains(from.getContent()) && this.nodeContents.contains(to.getContent())
                && currentDistance != 0) {

            if (currentDistance != this.DEFAULT_INVALID_VALUE)
                this.listRepresentation.setLink(this.nodes.indexOf(from) + 1, to, newDistance);
            else
                this.listRepresentation.addLink(from, to, newDistance);
            this.matrixRepresentation.setElement(this.nodes.indexOf(from), this.nodes.indexOf(to),
                    newDistance);
            this.matrixRepresentation.setElement(this.nodes.indexOf(to), this.nodes.indexOf(from),
                    newDistance);
        }
    }

    // Returns the row of a Node (not index)
    public int rowOf(Node nodeToCheck) {
        if (this.nodeContents.contains(nodeToCheck.getContent()))
            return this.nodes.indexOf(nodeToCheck) + 1;
        else
            return -1;
    }

    // Resets the whole object to the inital state
    // Uses the "original" fields
    public void resetGraph() {
        this.matrixRepresentation = originalMatrixRepresentation.clone();
        this.listRepresentation = originalListRepresentation.clone();
        nodeCount = this.originalNodeCount;
        this.nodeContents = this.originalNodeContents;
        this.nodes = this.originalNodes;
        LinkedList<Node> tempNodes = new LinkedList<>();
        for (Node n : this.nodes)
            tempNodes.add(n.clone());
        this.originalNodes = tempNodes;
    }

    // Removes a Node and any edges associated with it
    public void removeNode(Node toRemove) {
        if (this.nodeContents.contains(toRemove.getContent())) {
            int tempIndex = this.nodes.indexOf(toRemove);
            this.listRepresentation.refreshForRemovedNode(tempIndex, toRemove);
            this.matrixRepresentation.refreshForRemovedNode(tempIndex);
            this.nodes.remove(toRemove);
            this.nodeContents.remove(toRemove.getContent());
        }
    }

    // Removes the edge between two Nodes
    public void removeEdge(Node from, Node to) {
        boolean isNeighbour = getEdgeByNode(from, to) != this.DEFAULT_INVALID_VALUE;
        if (this.nodeContents.contains(from.getContent()) && this.nodeContents.contains(to.getContent())
                && isNeighbour) {
            this.matrixRepresentation.setElement(this.nodes.indexOf(from) + 1, this.nodes.indexOf(to) + 1,
                    this.DEFAULT_INVALID_VALUE);
            this.listRepresentation.removeEdge(this.nodes.indexOf(from) + 1, to);
        }
    }

    @Override
    public String toString() {
        return "Graph has " + this.nodeCount + " nodes.";
    }

    // Prints the AdjacencyMatrix to the terminal
    public void printMatrix() {
        this.matrixRepresentation.printMatrixToTerminal();
    }

    // Prints the initial AdjacencyMatrix to the terminal
    public void printOriginalMatrix() {
        this.originalMatrixRepresentation.printMatrixToTerminal();
    }

    // Prints the AdjacencyList to the terminal
    public void printList() {
        this.listRepresentation.printListToTerminal();
    }

    // Prints the original AdjacencyList to the terminal
    public void printOriginalList() {
        this.originalListRepresentation.printListToTerminal();
    }

    // Returns a copy of the AdjacencyMatrix in ArrayList format
    public ArrayList<ArrayList<Double>> getMatrix() {
        ArrayList<ArrayList<Double>> returnMatrix = new ArrayList<>();
        ArrayList<ArrayList<Double>> tempMatrix = this.matrixRepresentation.getRawMatrix();
        for (int i = 0; i < tempMatrix.size(); i++) {
            returnMatrix.add(new ArrayList<>());
            for (Double d : tempMatrix.get(i))
                returnMatrix.get(i).add(d);
        }

        return returnMatrix;
    }

    // Returns a copy of the AdjacencyList in LinkedList format
    public LinkedList<LinkedHashMap<Node, Double>> getList() {
        LinkedList<LinkedHashMap<Node, Double>> returnList = new LinkedList<>();
        for (int i = 0; i < this.nodeCount; i++) {
            returnList.add(new LinkedHashMap<>());
            returnList.get(i).put(this.nodes.get(i), 0.0);
            for (Node n : getNeighbours(this.nodes.get(i)))
                returnList.get(i).put(n, this.getEdgeByNode(this.nodes.get(i), n));
        }
        return returnList;
    }

    // Returns the Nodes present in the graph
    public LinkedList<Node> getNodes() {
        LinkedList<Node> returnList = new LinkedList<>();
        for (Node n : this.nodes)
            returnList.add(n);
        return returnList;
    }

    // Prints the initial Nodes to the terminal
    public void printOriginalNodes() {
        LinkedList<Node> returnList = new LinkedList<>();
        for (Node n : this.originalNodes)
            System.out.println(n);
    }

}
