package sw4;

import java.util.ArrayList;
import java.util.Arrays;

public class Refutation
{
	ArrayList<Point> walls = new ArrayList<Point>();
	ArrayList<Point> seen = new ArrayList<Point>();
	ArrayList<Point> unseen = new ArrayList<Point>();
	ArrayList<Edge> edges = new ArrayList<Edge>();
	Polygon pol = new Polygon();
	ArrayList<Point> vertices = new ArrayList<Point>(Arrays.asList(pol.input));
	ArrayList<Point> guards = new ArrayList<Point>(Arrays.asList(pol.guards));
	double eps = 0.0000000001;



	public void getEdges()
	{
		for(int i=0;i<vertices.size()-1;i++)
		{
			edges.add(new Edge(vertices.get(i),vertices.get(i+1)));
		}
		edges.add(new Edge(vertices.get(vertices.size()-1),vertices.get(0)));
	}

	public double getX(Edge e,double y)
	{
		if(e.p1.x!=e.p2.x)
		{
			double m=(e.p1.y-e.p2.y)/(e.p1.x-e.p2.x);
			double n=e.p1.y-m*e.p1.x;
			if(e.p1.y!=e.p2.y)
				return (y-n)/m;
			else
				return 100;
		}
		return e.p1.x;
	}

	public double getY(Edge e,double x)
	{
		if(e.p1.x!=e.p2.x)
		{
			double m=(e.p1.y-e.p2.y)/(e.p1.x-e.p2.x);
			double n=e.p1.y-m*e.p1.x;
			if(e.p1.y!=e.p2.y)
				return m*x+n;
			else
				return e.p1.y;
		}
		return 100;
	}

	public boolean isInside(Point p)
	{
		ArrayList<Point> intersections = new ArrayList<Point>();
		double xmin=vertices.get(0).x;
		double xmax=vertices.get(0).x;
		for(Point p1:vertices)
		{
			if((p1.x)<xmin)
				xmin=p1.x;
			if((p1.x)>xmax)
				xmax=p1.x;
		}
		Point p2=new Point(xmax+0.1,p.y);
		Edge e=new Edge(p,p2);
		int count=0;
		for(Edge e1:edges)
		{
			double x=getX(e1,p.y);
			if(x!=100)
			if(x<=Math.max(e1.p1.x, e1.p2.x) && x>=Math.min(e1.p1.x, e1.p2.x) && x>p.x)
				count++;
		}
		if(count%2==1)
			return true;
		else
			return false;
	}

	public void getUnseen()
	{
		double xmin=100,xmax=-100,ymin=100,ymax=-100;
		for(Point p2:vertices)
		{
			if(p2.x<xmin)
				xmin=p2.x;
			if(p2.x>xmax)
				xmax=p2.x;
			if(p2.y<ymin)
				ymin=p2.y;
			if(p2.y>ymax)
				ymax=p2.y;
		}
		for(double i=xmin;i<=xmax;i+=eps)
		{
			for(double j=ymin;j<=ymax;j+=eps)
			{
				Point p=new Point(i,j);
				if(isInside(p))
				{
					boolean check2=false;
					for(Point p1:guards)
					{
						boolean check=true;
						Edge e = new Edge(p,p1);
						for(Edge e1:edges)
						{
							if(p1.x<p.x)
							{
								for(double k=p1.x;k<=p.x;k+=eps)
								{
									double y1=getY(e,k);
									double y2=getY(e1,k);
									if(y1==y2 && y2>=Math.min(e1.p1.y, e1.p2.y) && y2<=Math.max(e1.p1.y, e1.p2.y) && y1!=100 && y2!=100)
									{
										check=false;
										break;
									}
								}
							}
							else
							{
								for(double k=p1.x;k>=p.x;k-=eps)
								{
									double y1=getY(e,k);
									double y2=getY(e1,k);
									if(y1==y2 && y2>=Math.min(e1.p1.y, e1.p2.y) && y2<=Math.max(e1.p1.y, e1.p2.y) && y1!=100 && y2!=100)
									{
										check=false;
										break;
									}
								}
							}
							if(!check)
								break;
						}
						if(check)
						{
							check2=true;
							break;
						}
					}
					if(!check2)
					{
						System.out.println("("+p.x+", "+p.y+")");
						return;
					}
				}
			}
		}
	}

	public static void main(String[] args)
	{
		Refutation r = new Refutation();
		r.getEdges();
		r.getUnseen();
	}
}
