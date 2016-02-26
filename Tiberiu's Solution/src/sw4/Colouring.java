package sw4;

import java.util.ArrayList;
import java.util.Arrays;

public class Colouring
{
	Polygon pol = new Polygon();
	ArrayList<Edge> diagonals = new ArrayList<Edge>();
	ArrayList<Point> vertices = new ArrayList<Point>(Arrays.asList(pol.input));
	ArrayList<Edge> edges = new ArrayList<Edge>();
	ArrayList<Point> guards = new ArrayList<Point>();

	public void getDiagonals()
	{
		pol.triangulation();
		diagonals = pol.getDiagonals();
	}

	public void getEdges()
	{
		for(int i=0;i<vertices.size()-1;i++)
		{
			edges.add(new Edge(vertices.get(i),vertices.get(i+1)));
		}
		edges.add(new Edge(vertices.get(vertices.size()-1),vertices.get(0)));
		for(Edge e:diagonals)
			edges.add(e);
	}

	public char getColour(Edge e)
	{
		if(e.p1.colour!='r' && e.p2.colour!='r')
			return 'r';
		if(e.p1.colour!='g' && e.p2.colour!='g')
			return 'g';
		return 'b';
	}

	public boolean isEdge(Point p1, Point p2)
	{
		for(Edge e:edges)
		{
			if((e.p1==p1 && e.p2==p2) || (e.p1==p2 && e.p2==p1))
				return true;
		}
		return false;
	}

	public ArrayList<Character> available(Point p1)
	{
		ArrayList<Character> unavailable = new ArrayList<Character>();
		ArrayList<Character> available = new ArrayList<Character>();
		for(Point p:vertices)
		{
			if(isEdge(p1,p) && p.colour!='a')
			{
				unavailable.add(p.colour);
			}
		}
		boolean r=true,g=true,b=true;
		for(char c:unavailable)
		{
			if(c=='r')
				r=false;
			if(c=='g')
				g=false;
			if(c=='b')
				b=false;
		}
		if(r==true)
			available.add('r');
		if(g==true)
			available.add('g');
		if(b==true)
			available.add('b');
		return available;
	}

	public void colour()
	{
		while(diagonals.size()>0)
		{
			boolean check=true;
			for(Point p:vertices)
			{
				if(p.colour=='a')
					check=false;
			}
			if(check)
				return;
			Edge current = diagonals.get(0);
			ArrayList<Character> available = new ArrayList<Character>();
			if(current.p1.colour=='a')
			{
				available=available(current.p1);
				if(available.size()>0)
				current.p1.colour=available.get(0);
			}
			if(current.p2.colour=='a')
			{
				available=available(current.p2);
				if(available.size()>0)
				current.p2.colour=available.get(0);
			}
			for(Point p:vertices)
			{
				if(p!=current.p1 && p!=current.p2 && isEdge(p,current.p1) && isEdge(p,current.p2) && p.colour=='a')
				{
					p.colour=getColour(current);
				}
			}
			diagonals.remove(current);
		}
	}

	public void putGuards()
	{
		int[] count={0,0,0};
		for(Point p:vertices)
		{
			if(p.colour=='r')
				count[0]++;
			else
			{
				if(p.colour=='g')
					count[1]++;
				else
					count[2]++;
			}
		}
		if(count[0]<=count[1] && count[0]<=count[2])
		{
			for(Point p:vertices)
			{
				if(p.colour=='g')
					guards.add(p);
			}
		}
		else
		{
			if(count[1]<count[0] && count[1]<=count[2])
			{
				for(Point p:vertices)
				{
					if(p.colour=='g')
						guards.add(p);
				}
			}
			else
			{
				for(Point p:vertices)
				{
					if(p.colour=='b')
						guards.add(p);
				}
			}
		}
	}

	public static void main(String[] args)
	{
		Colouring c = new Colouring();
		c.getDiagonals();
		c.getEdges();
		c.colour();
		c.putGuards();
		for(Point p:c.guards)
		{
			System.out.println("("+p.x+", "+p.y+")");
		}
	}
}
