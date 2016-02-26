package sw4;


import java.util.ArrayList;
import java.util.Arrays;

public class Polygon
{
	//Point[] input = {new Point(0, 0), new Point(2, 0), new Point(2, 1), new Point(1, 1), new Point(1, 2), new Point(3, 2), new Point(3, 3), new Point(0, 3)};
	//Point[] guards = {new Point(2, 0), new Point(3, 2.5)};
	//Point[] input = {new Point(0, 0), new Point(2, 0), new Point(2, 2), new Point(1, 0.5)};
	//Point[] guards = {new Point(2, 2)};
	Point[] input = {new Point(-2, -2), new Point(-0.5, 0), new Point(5, 0), new Point(2, 0.5), new Point(0, 5), new Point(0.2, 3)};
	Point[] guards = {new Point(5, 0), new Point(0, 5), new Point(-2, -2)};
	ArrayList<Point> vertices = new ArrayList<Point>(Arrays.asList(input));
	int size=vertices.size();
	ArrayList<Point> convexVertices = new ArrayList<Point>();
	ArrayList<Point> ears = new ArrayList<Point>();
	ArrayList<Edge> diagonals = new ArrayList<Edge>();

	public double calculateArea(Point p1, Point p2, Point p3)
	{
		double area = ((p3.x-p1.x)*(p2.y-p1.y)-(p2.x-p1.x)*(p3.y-p1.y))/2;
		return area;
	}

	public void getConvex()
	{
		if(vertices.size()<4)
		{
			for(int i=0;i<vertices.size()-1;i++)
			{
					convexVertices.add(vertices.get(i));
			}
		}
		else
		{
			if(calculateArea(vertices.get(vertices.size()-1),vertices.get(0),vertices.get(1))<0)
				convexVertices.add(vertices.get(0));
		for(int i=1;i<vertices.size()-1;i++)
		{
			if(calculateArea(vertices.get(i-1),vertices.get(i),vertices.get(i+1))<0)
				convexVertices.add(vertices.get(i));
		}
		if(calculateArea(vertices.get(vertices.size()-2),vertices.get(vertices.size()-1),vertices.get(0))<0)
			convexVertices.add(vertices.get(vertices.size()-1));
		}


	}

	public int getIndex(Point p)
	{
		for(int i=0;i<vertices.size();i++)
		{
			if(vertices.get(i).equals(p))
				return i;
		}
		return -1;
	}

	public int modulo(int i)
	{
		int index=i%vertices.size();
		if(index<0)
		{
			index+=vertices.size();
		}
		return index;
	}

	public double calculateAngle(Point p1, Point p2, Point p3)
	{
		double s1=Math.sqrt((p3.y-p2.y)*(p3.y-p2.y)+(p3.x-p2.x)*(p3.x-p2.x));
		double s2=Math.sqrt((p3.y-p1.y)*(p3.y-p1.y)+(p3.x-p1.x)*(p3.x-p1.x));
		double s3=Math.sqrt((p1.y-p2.y)*(p1.y-p2.y)+(p1.x-p2.x)*(p1.x-p2.x));
		double angle = ((p3.x-p1.x)*(p2.y-p1.y)-(p2.x-p1.x)*(p3.y-p1.y))/2;
		return Math.acos(angle);

	}
	public boolean isInside(Point p)
	{
		int index=getIndex(p);
		for(int i=0;i<vertices.size();i++)
		{
			if(i!=modulo(index-1) && i!=index && i!=modulo(index+1))
			{
				Point p0=vertices.get(i);
				Point p1=vertices.get(modulo(index-1));
				Point p2=vertices.get(index);
				Point p3=vertices.get(modulo(index+1));
				if(calculateAngle(p1,p0,p2)+calculateAngle(p2,p0,p3)+calculateAngle(p3,p0,p1)==Math.PI)
				{
					return false;
				}
			}
		}
		return true;
	}

	public boolean intersects(Edge e1, Edge e2)
	{
		if(e1.p1.x!=e1.p2.x && e2.p1.x!=e2.p2.x)
		{
			double m1=(e1.p1.y-e1.p2.y)/(e1.p1.x-e1.p2.x);
			double m2=(e2.p1.y-e2.p2.y)/(e2.p1.x-e2.p2.x);
			if(m1==m2)
				return false;
			double n1=e1.p1.y-m1*e1.p1.x;
			double n2=e2.p1.y-m2*e2.p1.x;
			double x=(n2-n1)/(m1-m2);
			double y=m1*x+n1;
			if(x>Math.min(e1.p1.x,e1.p2.x) && x<Math.max(e1.p1.x,e1.p2.x) && y>Math.min(e1.p1.y,e1.p2.y) && y<Math.max(e1.p1.y,e1.p2.y) && x>Math.min(e2.p1.x,e2.p2.x) && x<Math.max(e2.p1.x,e2.p2.x) && y>Math.min(e2.p1.y,e2.p2.y) && y<Math.max(e2.p1.y,e2.p2.y))
			{
				return true;
			}
		}
		else
		{
			if(e1.p1.x-e1.p2.x==e2.p1.x-e2.p2.x)
			{
				return false;
			}
			if(e1.p1.x==e1.p2.x)
			{
				double m2=(e2.p1.y-e2.p2.y)/(e2.p1.x-e2.p2.x);
				double n2=e2.p1.y-m2*e2.p1.x;
				double y=m2*e1.p1.x+n2;
				if(y>Math.min(e1.p1.y,e1.p2.y) && y<Math.max(e1.p1.y,e1.p2.y) && y>Math.min(e2.p1.y,e2.p2.y) && y<Math.max(e2.p1.y,e2.p2.y))
				{
					return true;
				}
			}
			if(e2.p1.x==e2.p2.x)
			{
				double m1=(e1.p1.y-e1.p2.y)/(e1.p1.x-e1.p2.x);
				double n1=e2.p1.y-m1*e2.p1.x;
				double y=m1*e1.p1.x+n1;
				if(y>Math.min(e1.p1.y,e1.p2.y) && y<Math.max(e1.p1.y,e1.p2.y) && y>Math.min(e2.p1.y,e2.p2.y) && y<Math.max(e2.p1.y,e2.p2.y))
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isOutside(Edge e)
	{
		for(int i=0;i<size-1;i++)
		{
			if(intersects(e,new Edge(input[i],input[i+1])))
			{
				return false;
			}
		}
		if(intersects(e,new Edge(input[size-1],input[0])))
			return false;
		return true;
	}

	public void getEars()
	{
		for(Point p:convexVertices)
		{
			if(isInside(p))
			{
				Edge diagonal = new Edge(vertices.get(modulo(getIndex(p)-1)), vertices.get(modulo(getIndex(p)+1)));
				if(isOutside(diagonal))
				ears.add(p);
			}
		}
	}

	public void triangulation()
	{
		ears.add(new Point(0,0));
		while(ears.size()>0 && vertices.size()>3)
		{
			ears.clear();
			convexVertices.clear();
			getConvex();
			getEars();
			//System.out.println(ears.size());
			if(ears.size()>0)
			{
				Edge diagonal = new Edge(vertices.get(modulo(getIndex(ears.get(0))-1)), vertices.get(modulo(getIndex(ears.get(0))+1)));
				diagonals.add(diagonal);
				vertices.remove(ears.get(0));
			}
		}
	}

	public ArrayList<Edge> getDiagonals()
	{
		return diagonals;
	}

}
