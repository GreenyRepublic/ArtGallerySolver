package com.company;

//MemeGallery, an implementation of Ben Clark's algorithm for Scenario Week 4.
//Written by Benjamin Clark
//23/02/2016

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;
import java.io.*;
import java.util.Random;

public class Main extends JPanel
{
    private class Vertex
    {
        private int ID;
        private double x, y;
        private ArrayList<Vertex> visiblePoints;

        private Vertex(int ID, double x, double y)
        {
            this.ID = ID;
            this.x = x;
            this.y = y;
            this.visiblePoints = new ArrayList<>();
        }

        public int getID()
        {
            return ID;
        }

        public ArrayList<Vertex> getVisiblePoints()
        {
            return visiblePoints;
        }

        public double getCoord(char xory)
        {
            switch (xory) {
                case 'x':
                    return x;
                case 'y':
                    return y;
            }
            return 0;
        }

        public void addVisible(Vertex v)
        {
            visiblePoints.add(v);
        }

        //IMPORTANT, USE THIS AT THE START OF EACH LOOP OF THE MAIN THING.
        public void clearVisible()
        {
            visiblePoints.clear();
        }

        public int visibleNum()
        {
            return visiblePoints.size();
        }

        public String printCoords()
        {
            StringBuilder s = new StringBuilder();
            s.append("(" + x + ", " + y + ')');
            return s.toString();
        }

    }

    private class Edge
    {
        private Vertex startVertex, endVertex;
        private Line2D.Double Line;

        private Edge(Vertex startVertex, Vertex endVertex)
        {
            this.startVertex = startVertex;
            this.endVertex = endVertex;
            this.Line = new Line2D.Double(startVertex.getCoord('x'), startVertex.getCoord('y'), endVertex.getCoord('x'), endVertex.getCoord('y'));
        }

        public Vertex getStart()
        {
            return startVertex;
        }

        public Vertex getEnd()
        {
            return endVertex;
        }

        public Line2D.Double getLine()
        {
            return Line;
        }

        //Returns the gradient of the edge (dy/dx).
        public double Gradient()
        {
            double y1 = startVertex.getCoord('y');
            double y2 = endVertex.getCoord('y');

            double x1 = startVertex.getCoord('x');
            double x2 = endVertex.getCoord('x');

            return ((y2-y1)/(x2-x1));
        }

        //Returns x1 and y1 values for line equations.
        public double getX()
        {
            return startVertex.getCoord('x');
        }

        public double getY()
        {
            return startVertex.getCoord('y');
        }


        //Returns the largest and smallest of the x and y values of the two vertices.
        public double bigX()
        {
            double x1 = startVertex.getCoord('x');
            double x2 = endVertex.getCoord('x');
            double result = (x1 > x2)? x1 : x2;
            return result;
        }

        public double smallX()
        {
            double x1 = startVertex.getCoord('x');
            double x2 = endVertex.getCoord('x');
            double result = (x1 < x2)? x1 : x2;
            return result;
        }

        public double bigY()
        {
            double y1 = startVertex.getCoord('y');
            double y2 = endVertex.getCoord('y');
            double result = (y1 > y2)? y1 : y2;
            return result;
        }

        public double smallY()
        {
            double y1 = startVertex.getCoord('y');
            double y2 = endVertex.getCoord('y');
            double result = (y1 < y2)? y1 : y2;
            return result;
        }
    }

    //MyPolygon class: containing data structures with all gallery data, and functions for populating the structures and calculating solutions.
    private class MyPolygon
    {
        private ArrayList<Vertex> Vertices;
        private ArrayList<Edge> Edges;
        private ArrayList<Vertex> Seen;
        private ArrayList<Vertex> Unseen;
        private ArrayList<Vertex> Guards; //THESE ARE THE SOLUTIONS FOR THE POLYGON. IMPORTANT!
        private ArrayList<Vertex> ScatterPoints;
        private Path2D Poly;
        private int PolyID;

        private MyPolygon(int PolyID)
        {
            this.Vertices = new ArrayList<>();
            this.Seen = new ArrayList<>();
            this.Unseen = new ArrayList<>();
            this.Guards = new ArrayList<>();
            this.Edges = new ArrayList<>();
            this.ScatterPoints = new ArrayList<>();
            this.Poly = new Path2D.Double();
            this.PolyID = PolyID;
        }

        public Path2D getPoly(){
            return Poly;
        }

        public void addVertex(Vertex v)
        {
            //System.out.println("Added Vertex " + v.printCoords());
            Vertices.add(v);
        }

        public void addSeen(Vertex v)
        {
            Seen.add(v);
        }

        public void addGuard(Vertex v)
        {
            Guards.add(v);
        }

        //Returns all the Guards (solutions) in the form of a String, ready to be written to the file.
        public String Solutions()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("    ");
            for (Vertex x : Guards)
            {
                builder.append("(" + x.getCoord('x') + ", " + x.getCoord('y') + ')' + ',' + ' ');
            }
            return builder.toString();
        }

        //Run after the MyPolygon has been populated with vertices, calculates the edges by working from left-to-right through the Vertices ArrayList and finding pairs.
        public void populateEdges()
        {
            int k = Vertices.size() - 1;
            for (int i = 1; i < k + 1; i++)
            {
                Edge e = new Edge(Vertices.get(i-1), Vertices.get(i));
                Edges.add(e);
                //System.out.print("Added Edge between ("+e.getLine().getX1()+", "+e.getLine().getY1()+") and ("+e.getLine().getX2()+", "+e.getLine().getY2()+")");
                //System.out.println(" Gradient: " + e.Gradient());
            }
            Edge p = new Edge(Vertices.get(k), Vertices.get(0));
            Edges.add(p);
            //System.out.print("Added Edge between ("+p.getLine().getX1()+", "+p.getLine().getY1()+") and ("+p.getLine().getX2()+", "+p.getLine().getY2()+")");
            //System.out.println(" Gradient: " + p.Gradient());
            //System.out.println("All edges added!");
        }

        public void populatePath()
        {
            boolean firstpt = true;
            for (Vertex x : Vertices)
            {
                if (firstpt)
                {
                    Poly.moveTo(x.getCoord('x'), x.getCoord('y'));
                    firstpt = false;
                }

                else
                {
                    Poly.lineTo(x.getCoord('x'), x.getCoord('y'));
                }
            }
            Poly.lineTo(Vertices.get(0).getCoord('x'), Vertices.get(0).getCoord('y'));
            Poly.closePath();
        }


        //Checks if two allegedly intersecting lines are doing so at their mutual origin.
        public boolean originIntersect(Line2D a, Line2D b)
        {
            //Start = Start
            if (a.getP1().equals(b.getP1()))
            {
                return true;
            }

            //Start = End
            if (a.getP1().equals(b.getP2()))
            {
                return true;
            }

            //End = Start
            if (a.getP2().equals(b.getP1()))
            {
                return true;
            }

            //End = End
            if (a.getP2().equals(b.getP2()))
            {
                return true;
            }
            return false;
        }

        public boolean sameLine(Line2D.Double a, Line2D.Double b)
        {
            //System.out.println("Line A: " + "(" + a.getX1() + "," + a.getY1() + ")" + "->" + "(" + a.getX2() + "," + a.getY2() + ")");
            //System.out.println("Line B: " + "(" + b.getX1() + "," + b.getY1() + ")" + "->" + "(" + b.getX2() + "," + b.getY2() + ")\n");

            //Start = Start and End = End
            if ((a.getP1().equals(b.getP1())) && (a.getP2().equals(b.getP2())))
            {
                //System.out.println("    SAME DIRECTION");
                return true;
            }

            //Start = End and End = Start
            else if ((a.getP1().equals(b.getP2())) && (a.getP2().equals(b.getP1())))
            {
                //System.out.println("    REVERSE DIRECTION");
                return true;
            }

            else
            {
                //System.out.println("    NEIN");
                return false;
            }
        }

        //Checks to see if the two argument vertices can 'see' one another.
        //Specifically: draw a line from one to the other and return TRUE if there are no lines in the way and FALSE if otherwise.
        public boolean checkSight(Vertex Start, Vertex End)
        {

            //If both vertices are the same, return true.
            if ((Start.getCoord('x') == End.getCoord('x')) && (Start.getCoord('y') == End.getCoord('y')))
            {
                //System.out.println("        PASS (Same Vertex!)");
                return true;
            }

            Edge sightLine = new Edge(Start, End);
            for (Edge e : Edges)
            {
                //System.out.println("Comparing edges: " + Start.printCoords() + "->" + End.printCoords() + " AND " + e.startVertex.printCoords() + "->" + e.endVertex.printCoords());
                if (sameLine(sightLine.getLine(), e.getLine()))
                {
                    //System.out.println("        PASS (Same Line!)");
                    return true;
                }

                else if (sightLine.getLine().intersectsLine(e.getLine()))
                {
                    if (!originIntersect(sightLine.getLine(), e.getLine()))
                    {
                        //System.out.println("        FAIL (Intersects!)");
                        return false;
                    }
                }
            }

            Point2D midpoint = new Point2D.Double();
            double midx = (Start.getCoord('x') + End.getCoord('x'))/2;
            double midy = (Start.getCoord('y') + End.getCoord('y'))/2;
            midpoint.setLocation(midx, midy);
            if (!Poly.contains(midpoint))
            {
                //System.out.println("        Midpoint coords: " + midx + ", " + midy);
                //System.out.println("        Line outside of Polygon SHAMEFUR DISPRAY.");
                return false;
            }

            //System.out.println("        PASS (Fallthrough)");
            return true;
        }


        //Scans the vertices array and finds the vertex with the most visible vertices.
        public Vertex mostSeen()
        {
            int largest = 0;
            Vertex result = ScatterPoints.get(0);
            for (Vertex k : ScatterPoints)
            {
                if (k.visibleNum() > largest)
                {
                    result = k;
                    largest = k.visibleNum();
                }
            }
            //System.out.println("Winning Vertex: " + result.printCoords());
            //System.out.print("Newly-seen Vertices: ");
            for (Vertex a : result.getVisiblePoints())
            {
                //System.out.print(a.printCoords());
            }
            //System.out.println("");
            return result;
        }

        public void populateUnseen()
        {

            //Get bounding dimensions of the polygon.
            double highestX = 0, highestY = 0, lowestX = 0, lowestY = 0;
            for(Vertex v : Vertices)
            {
                double x = v.getCoord('x');
                double y = v.getCoord('y');

                if (x > highestX)
                {
                    highestX = x;
                }

                else if (x < lowestX)
                {
                    lowestX = x;
                }

                if (y > highestY)
                {
                    highestY = y;
                }

                else if (y < lowestY)
                {
                    lowestY = y;
                }
            }

            //Create boundingbox.
            Rectangle2D boundingbox = getPoly().getBounds2D();

            //GENERATE A FUCKTONNE OF THINGS
            Random random = new Random();
            double rangex = Math.abs(lowestX) + Math.abs(highestX);
            double rangey = Math.abs(lowestY) + Math.abs(highestY);
            System.out.println("RangeX = " + rangex);
            System.out.println("RangeY = " + rangey);
            while(ScatterPoints.size() < (Vertices.size()*500))
            {
                double randX = (random.nextDouble()*rangex);
                double randY = (random.nextDouble()*rangey);

                if (lowestX < 0)
                {
                    randX += lowestX;
                }
                else
                {
                    randX -= lowestX;
                }

                if (lowestY < 0)
                {
                    randY += lowestY;
                }
                else
                {
                    randY -= lowestY;
                }


                //System.out.println("RandX = " + randX);
               // System.out.println("RandY = " + randY);
                //System.out.println("");
                //System.out.println("Created Vertex");
                if (getPoly().contains(randX, randY))
                {
                   // System.out.println("Adding Vertex");
                    Vertex scatterpoint = new Vertex(0, randX, randY);
                    ScatterPoints.add(scatterpoint);
                }
            }

        }

        //Run after the MyPolygon has been populated with edges and vertices, calculates guard solutions with THE ALGORITHM(TM).
        public void calculateSolutions()
        {
            System.out.println("Processing Polygon #" + PolyID + "(" + Vertices.size() + " Verts)");

            //System.out.println("Vertices in poly: " + Vertices.size());
            populatePath();

            //Populate the Unseen array with A FUCKTONNE OF SCATTERPOINTS.
            populateUnseen();

            for (Vertex v : Vertices)
            {
                Unseen.add(v);
                //System.out.println("Added!");
                //System.out.println("Added!");
            }

            //The main loop: goes through all vertices in Vertices
            //For each one check how many vertices in unseen are visible from it.
            //The one with the greatest number is added to both Guards and Seen, and all vertices visible from it are also added to Seen.
            //Repeat until all vertices are in 'Seen'.


            //THE BIG THING

            int count = 1;
            long startTimePoly, startTimeCycle;
            long endTimePoly, endTimeCycle;
            startTimePoly = System.currentTimeMillis();
            while (Unseen.size() > 0)
            {
                startTimeCycle = System.currentTimeMillis();
                System.out.println("Cycle " + count);
                //System.out.println("Initial Seen Size: " + Seen.size());
                for (Vertex r : ScatterPoints)
                {
                    r.clearVisible();
                }

                for (Vertex x : ScatterPoints)
                {
                    if (!Guards.contains(x))
                    {
                        //System.out.println("\nChecking vertex: " + x.getID() + x.printCoords());
                        for (Vertex u : Unseen)
                        {
                            //System.out.println("    Sighting for point: " + u.getID() + u.printCoords());
                            if (checkSight(x, u))
                            {
                                x.addVisible(u);
                            }
                        }
                        //System.out.println("Found: " + x.visibleNum());
                    }
                }
                Vertex p = mostSeen();
                Guards.add(p);
                for(Vertex k : p.getVisiblePoints())
                {
                    if (!Seen.contains(k))
                    {
                        Seen.add(k);
                        Unseen.remove(k);
                    }
                }
                Unseen.remove(p);
                //System.out.println("Seen Elements: " + Seen.size());
                //System.out.println("Unseen Elements: " + Unseen.size());
                //System.out.println("Guards: " + Solutions());
                count++;

                //System.out.println("SeenSize: " + Seen.size());
                //System.out.print("SEEN: ");
                /*for (Vertex z : Seen)
                {
                    z.printCoords();
                }*/
                endTimeCycle = System.currentTimeMillis();
                System.out.println("Cycle Time: " + (endTimeCycle - startTimeCycle)+" Milliseconds");
            }
            endTimePoly = System.currentTimeMillis();
            System.out.println("Poly Time: " + (endTimePoly - startTimePoly)+" Milliseconds");

            System.out.println("Solutions/TotalVerts = " + (count-1) + "/" + Vertices.size());
            System.out.println("");
        }
    }

    //Returns the desired coordinate from a bracketed input.
    public double getCoords(String sequence, char select)
    {
        Pattern pattern = Pattern.compile("-*[0-9]+(.[0-9]+)*");
        Matcher matcher = pattern.matcher(sequence);
        select = Character.toLowerCase(select);

        int count = 1;

        while (matcher.find())
        {
            if ((count == 1) && (select == 'x'))
            {
                return Double.parseDouble(matcher.group());
            }
            else if ((count == 2) && (select == 'y'))
            {
                return Double.parseDouble(matcher.group());
            }
            count++;
        }
        return 0;
    }

    //Takes a text line, parses it into a polygon and calculates solutions.
    public MyPolygon processLine(String line, int ID)
    {
        int vID = 0;
        boolean guards = false;
        MyPolygon Poly = new MyPolygon(ID);
        String[] split = line.split(";(\\((-?[0-9]+(.[0-9]+)*), (-?[0-9]+(.[0-9]+)*)\\))*");
        String polyVerts = split[0];
        String guardsString = split[1];
        Pattern pattern = Pattern.compile("\\((-?[0-9]+(.[0-9]+)*), (-?[0-9]+(.[0-9]+)*)\\)");
        Matcher matcher = pattern.matcher(polyVerts);

        //Fill the polygon with all vertices on the current line.
        while (matcher.find())
        {
            polyVerts = matcher.group();
            guardsString = matcher.group();
            double x = getCoords(polyVerts, 'x');
            double y = getCoords(polyVerts, 'y');
            double x2 = getCoords(guardsString, 'x');
            double y2 = getCoords(guardsString, 'y');
            Poly.addVertex(new Vertex(vID, x, y));
            Poly.ScatterPoints.add(new Vertex(0, x2, y2));
            vID++;
            System.out.printf("Added vertex with coordinates (%f, %f).\n", x, y);
        }
        //System.out.println("All vertices added!");
        //Poly.populateEdges();
        //Poly.calculateSolutions();
        return Poly;
    }

    //Processes the entire file one line at a time, passing each line to the processLine function.
    //The solutions are then written to an output file.
    public void processFile(File inputPolygons) throws IOException
    {
        BufferedReader File = new BufferedReader(new FileReader(inputPolygons));
        String line;

        //Writing the output file, team details first followed by the solution for each MyPolygon.
        try {
            int c = 1;
            int ID = 1;
            FileWriter outWriter = new FileWriter(new File("TeamElephantGuardSolutions.txt"));
            outWriter.write("elephant");
            outWriter.write(System.lineSeparator());
            outWriter.write("dm2vcrccra5n7rj1qsnkl9ciq9");
            outWriter.write(System.lineSeparator());

            while((line = File.readLine()) != null)
            {
                MyPolygon p = (processLine(line, ID));
                outWriter.write(c + ":");
                outWriter.write(p.Solutions());
                outWriter.write(System.lineSeparator());
                c++;
                ID++;
                break;
            }

            outWriter.close();
        }
        catch (IOException e)
        {
            System.out.println("Oops problem with file writing.");
        }
        System.out.println("Data written to file!");
    }

    public void memegallery()
    {
        //Reads the input polygons file.
        File inputPolygons = new File("check.pol");
        //System.out.println(inputPolygons.length());
        try
        {
            processFile(inputPolygons);
        }
        catch(IOException e)
        {
            System.out.println("File read error! Or something.");
        }

    }

    public static void main(String[] args)
    {
        new Main().memegallery();
    }
}
